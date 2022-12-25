package de.matul.lepton_sim.sim;

public interface TickReceiver extends SimComponent {
    void tick(int cycleNo);
}
