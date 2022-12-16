package de.matul.lepton_sim.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Net {
    private final List<String> names;
    private final List<String> connectedPins;

    public Net(String[] name, List<String> connectedPins) {
        this(new ArrayList<>(Arrays.asList(name)), connectedPins);
    }

    public Net(List<String> names, List<String> connectedPins) {
        this.names = names;
        this.connectedPins = connectedPins;
    }

    public List<String> getNames() {
        return names;
    }

    public List<String> getConnectedPins() {
        return connectedPins;
    }

    public Net addPrefix(String prefix) {
        return new Net(names.stream().map(x -> prefix + "." + x).toList(),
                connectedPins.stream().map(x -> prefix + "." + x).toList());
    }
}
