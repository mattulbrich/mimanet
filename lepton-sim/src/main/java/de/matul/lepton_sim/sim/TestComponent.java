package de.matul.lepton_sim.sim;

import de.matul.lepton_sim.data.Component;
import de.matul.lepton_sim.data.Netlist;

import static de.matul.lepton_sim.sim.Signal.*;

public class TestComponent implements TickReceiver {

    private static final Signal[][] NAND = new Signal[][] {
            { ONE,   ONE,   ERROR, ERROR },
            { ONE,   ZERO,  ERROR, ERROR },
            { ERROR, ERROR, ERROR, ERROR }
    };

    private Pin in1;
    private Pin in2;
    private Pin out;

    @Override
    public void register(Simulator simulator, Component component, Netlist compnet) {
        this.in1 = simulator.getPin(component, "IN1");
        this.in2 = simulator.getPin(component, "IN2");
        this.out = simulator.getPin(component, "OUT");

        in1.setDriver(this, true);
        in2.setDriver(this, true);
        out.setDriver(this, false);

        simulator.addTickReceiver(this);
    }

    @Override
    public void recomputeOutputs(Simulator simulator) {
        Signal signal1 = in1.getNetSignal();
        Signal signal2 = in2.getNetSignal();
        Signal sout = NAND[signal1.ordinal()][signal2.ordinal()];
        out.setDriverSignal(simulator, sout);
    }


    @Override
    public void tick(int cycleNo) {
        System.out.println("tick " + cycleNo);
    }
}
