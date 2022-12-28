package de.matul.lepton_sim.sim;

import java.util.ArrayList;
import java.util.List;

public class SimNet {

    private final List<Pin> allPins = new ArrayList<>();

    private Signal lastSignal = Signal.ERROR;

    public void addPin(Pin pin) {
        allPins.add(pin);
    }

    public void recompute(Simulator sim) {
        Signal s = Signal.HIGH_IMP;
        for (Pin pin : allPins) {
            s = s.merge(pin.getDriverSignal());
        }
        if(s == lastSignal) {
            return;
        }
        lastSignal = s;
        for (Pin pin : allPins) {
            pin.setNetSignal(sim, s);
        }
    }
}
