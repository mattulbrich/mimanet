package de.matul.lepton_sim.data;

import java.io.PrintStream;
import java.util.Collections;
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
            System.out.println("Component " + component.getName());
            for (Entry<String, String> entry : component.allAttributes().entrySet()) {
                System.out.println("  ." + entry.getKey() + " = " + entry.getValue());
            }
        }
        for (Net net : nets) {
            System.out.println("Net " + net.getName() + " : " +
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
}
