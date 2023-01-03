package de.matul.lepton_sim.sim;

import java.util.ArrayList;
import java.util.List;

public class PinBus {

    private List<Pin> pins = new ArrayList<>();
    public void add(Pin pin) {
        pins.add(pin);
    }

    public void setDriver(SimComponent driver, boolean reactsToChange) {
        for (Pin pin : pins) {
            pin.setDriver(driver, reactsToChange);
        }
    }

    public long getNetBusValue() {
        long result = 0;
        long pos = 1;
        for (Pin pin : pins) {
            Signal netSignal = pin.getNetSignal();
            int v;
            switch (netSignal) {
                case ONE: v = 1; break;
                case ZERO: v = 0; break;
                default: return -1;
            };
            result |= v * pos;
            pos <<= 1;
        }
        return result;
    }

    public void setDriverBusSignal(Simulator simulator, long value) {
        for (Pin pin : pins) {
            pin.setDriverSignal(simulator, (value & 1) == 1 ? Signal.ONE : Signal.ZERO);
            value >>= 1;
        }
    }

    public void setAllDriverSignals(Simulator simulator, Signal signal) {
        for (Pin pin : pins) {
            pin.setDriverSignal(simulator, signal);
        }
    }
}
