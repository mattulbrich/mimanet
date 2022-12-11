package de.matul.lepton_sim.data;

import java.util.Collections;
import java.util.List;

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
}
