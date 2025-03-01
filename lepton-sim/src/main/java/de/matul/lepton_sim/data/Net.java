package de.matul.lepton_sim.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Net {
    private final List<String> names;
    private final List<String> connectedPins;

    private int width = -1;

    public Net(String[] name, List<String> connectedPins) {
        this(Arrays.asList(name), connectedPins);
    }

    public Net(List<String> names, List<String> connectedPins) {
        this.names = new ArrayList<>(names);
        this.connectedPins = new ArrayList<>(connectedPins);
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return String.join(", ", names) + ": " +
                String.join(", ", connectedPins);
    }
}
