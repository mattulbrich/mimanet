package de.matul.lepton_sim.sim;

import de.matul.lepton_sim.Library;
import de.matul.lepton_sim.data.Component;
import de.matul.lepton_sim.data.Net;
import de.matul.lepton_sim.data.Netlist;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class Simulator {
    private final Map<String, Pin> allPins = new LinkedHashMap<>();

    private final ClassLoader localClassLoader;
    private Collection<SimComponent> queuedComponents = new LinkedHashSet<>();
    private List<TickReceiver> tickReceivers = new ArrayList<>();

    private Recorder recorder;


    public Simulator() throws MalformedURLException {
        localClassLoader = new URLClassLoader(
                new URL[] { Paths.get("sym").toUri().toURL() });
        this.recorder = new Recorder();
    }


    public void simulate(Netlist netlist) throws Exception {
        buildEnv(netlist);
        recorder.registerPins(allPins);
        recorder.registerNets(netlist.getNets());
        runSimulation(1000);
    }

    private void runSimulation(int rounds) {

        for (int round = 0; round < rounds; round++) {
            int finalRound = round;
            tickReceivers.forEach(x -> x.tick(finalRound));
            queuedComponents.clear();
            queuedComponents.addAll(tickReceivers);
            while (!queuedComponents.isEmpty()) {
                SimComponent simComponent = queuedComponents.iterator().next();
                queuedComponents.remove(simComponent);
                simComponent.recomputeOutputs(this);
            }
            recorder.recordPins(allPins);
        }
    }

    private void buildEnv(Netlist netlist) throws Exception {
        buildComponents(netlist);
        buildNets(netlist);
    }

    private void buildNets(Netlist netlist) {
        for (Net net : netlist.getNets()) {
            SimNet simNet = new SimNet();
            for (String connectedPin : net.getConnectedPins()) {
                connectedPin = connectedPin.replace(' ', '.');
                Pin pin = allPins.get(connectedPin);
                if (pin == null) {
                    throw new NoSuchElementException(connectedPin);
                }
                simNet.addPin(pin);
                pin.setNet(simNet);
            }
        }
    }

    private void buildComponents(Netlist netlist) throws Exception {
        for (Component component : netlist.getComponents()) {
            String name = component.getName();
            Netlist compnet = Library.getLibNetlist(component.getDevice());
            for (Component pad : compnet.getPads()) {
                for (int i = 0; i < pad.getWidth(); i++) {
                    Pin pin = new Pin(name + "." + pad.getName() + "#" + i);
                    allPins.put(name + "." + pad.getName() + "#" + i, pin);
                }
            }

            Component implComp = compnet.getImplementation();
            String className = implComp.getAttribute("class");
            Class<? extends SimComponent> impClass;
            try {
                impClass = Class.forName("Component" + className).asSubclass(SimComponent.class);
            } catch (ClassNotFoundException ex) {
                impClass = localClassLoader.loadClass("Component" + className).asSubclass(SimComponent.class);
            }

            SimComponent simComponent = impClass.getDeclaredConstructor().newInstance();
            simComponent.register(this, component, compnet);
        }
    }

    public Pin getPin(Component component, String name) {
        return getPin(component.getName() + "." + name);
    }

    private Pin getPin(String pinName) {
        Pin result = allPins.get(pinName);
        if (result == null) {
            throw new NoSuchElementException("Unknown pin " + pinName);
        }
        return result;
    }

    public void addToQueue(SimComponent driver) {
        queuedComponents.add(driver);
    }

    public void addTickReceiver(TickReceiver tickReceiver) {
        tickReceivers.add(tickReceiver);
    }

    public void removeTickReceiver(TickReceiver tickReceiver) {
        tickReceivers.remove(tickReceiver);
    }

    public void print() {
        System.out.println(recorder.toJSON());
    }

    public void printTo(String filename) throws IOException {
        Files.writeString(Paths.get(filename), recorder.toJSON());
    }
}
