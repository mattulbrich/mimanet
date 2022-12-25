package de.matul.lepton_sim.sim;

import de.matul.lepton_sim.Library;
import de.matul.lepton_sim.data.Component;
import de.matul.lepton_sim.data.Net;
import de.matul.lepton_sim.data.Netlist;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Simulator {
    private final Map<String, Pin> allPins = new LinkedHashMap<>();

    private final ClassLoader localClassLoader;
    private Collection<SimComponent> queuedComponents;
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
                Pin pin = allPins.get(connectedPin);
                simNet.addPin(pin);
                pin.setNet(simNet);
            }
        }
    }

    private void buildComponents(Netlist netlist) throws Exception {
        for (Component component : netlist.getComponents()) {
            Netlist compnet = Library.getLibNetlist(component.getDevice());
            for (Component pad : compnet.getPads()) {
                Pin pin = new Pin(pad.getName());
                allPins.put(pad.getName(), pin);
            }

            Component implComp = compnet.getImplementation();
            String className = implComp.getAttribute("class");
            Class<? extends SimComponent> impClass = localClassLoader.loadClass("Component" + className).asSubclass(SimComponent.class);

            SimComponent simComponent = impClass.getDeclaredConstructor().newInstance();
            simComponent.register(this, component, compnet);
        }
    }

    public Pin getPin(Component component, String name) {
        return getPin(component.getName() + "." + name);
    }

    private Pin getPin(String pinName) {
        return allPins.get(pinName);
    }

    public void addToQueue(SimComponent driver) {
        queuedComponents.add(driver);
    }

    public void addTickReceiver(TickReceiver tickReceiver) {
        tickReceivers.add(tickReceiver);
    }
}
