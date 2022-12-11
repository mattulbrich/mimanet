package de.matul.lepton_sim.data;

import java.util.ArrayList;

public class Net {
    private final String name;
    private final ArrayList<String> connectedPins;

    public Net(String name, ArrayList<String> connectedPins) {
        this.name = name;
        this.connectedPins = connectedPins;
    }
}
