package de.matul.lepton_sim.data;

import java.util.Collections;
import java.util.List;

public class Net {
    private final String name;
    private final List<String> connectedPins;

    public Net(String name, List<String> connectedPins) {
        this.name = name;
        this.connectedPins = connectedPins;
    }

    public String getName() {
        return name;
    }

    public List<String> getConnectedPins() {
        return Collections.unmodifiableList(connectedPins);
    }

    public Net addPrefix(String prefix) {
        return new Net(prefix + "." + name, connectedPins.stream().map(x -> prefix + "." + x).toList());
    }
}
