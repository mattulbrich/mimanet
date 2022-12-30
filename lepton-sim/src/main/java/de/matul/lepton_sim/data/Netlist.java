package de.matul.lepton_sim.data;

import de.matul.lepton_sim.sim.Simulator;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

public class Netlist {
    private final List<Component> components;
    private final List<Net> nets;

    public Netlist(List<Component> components, List<Net> nets) {
        this.components = components;
        this.nets = nets;
    }

    public List<Component> getComponents() {
        return Collections.unmodifiableList(components);
    }

    public void print(PrintStream out) {
        for (Component component : components) {
            out.println("Component " + component.getName());
            for (Entry<String, String> entry : component.allAttributes().entrySet()) {
               out.println("  ." + entry.getKey() + " = " + entry.getValue());
            }
        }
        for (Net net : nets) {
            out.println("Net " + String.join(", ", net.getNames()) + " : " +
                String.join(", ", net.getConnectedPins()));
        }
    }

    public Netlist addPrefix(String prefix) {
        List<Component> newComponents = components.stream().map(x -> x.addPrefix(prefix)).toList();
        List<Net> newNets = nets.stream().map(x -> x.addPrefix(prefix)).toList();
        return new Netlist(newComponents, newNets);
    }

    public List<Net> getNets() {
        return Collections.unmodifiableList(nets);
    }

    public boolean isImplementation() {
        return components.stream().anyMatch(x -> x.getDevice().equals("IMPLEMENTATION"));
    }

    public List<Component> getPads() {
        return getComponents().stream().
                filter(x -> x.getDevice().matches("(I|O|IO)PAD")).
                toList();
    }

    public Component getImplementation() {
        for (Component component : components) {
            if("IMPLEMENTATION".equals(component.getDevice())) {
                return component;
            }
        }
        return null;
    }

    public String makeString() {
        ByteArrayOutputStream w = new ByteArrayOutputStream();
        print(new PrintStream(w));
        return w.toString();
    }
}
