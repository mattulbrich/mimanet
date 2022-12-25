package de.matul.lepton_sim.sim;

import de.matul.lepton_sim.data.Component;
import de.matul.lepton_sim.data.Netlist;

public interface SimComponent {
    void register(Simulator simulator, Component component, Netlist compnet);

    void recomputeOutputs(Simulator simulator);
}
