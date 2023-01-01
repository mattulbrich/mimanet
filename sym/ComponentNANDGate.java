import de.matul.lepton_sim.data.*;
import de.matul.lepton_sim.sim.*;

import static de.matul.lepton_sim.sim.Signal.*;

public class ComponentNANDGate implements SimComponent {

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
        this.in1 = simulator.getPin(component, "IN1#0");
        this.in2 = simulator.getPin(component, "IN2#0");
        this.out = simulator.getPin(component, "OUT#0");

        in1.setDriver(this, true);
        in2.setDriver(this, true);
        out.setDriver(this, false);

    }

    @Override
    public void recomputeOutputs(Simulator simulator) {
        Signal signal1 = in1.getNetSignal();
        Signal signal2 = in2.getNetSignal();
        Signal sout = NAND[signal1.ordinal()][signal2.ordinal()];
        out.setDriverSignal(simulator, sout);
    }

}
