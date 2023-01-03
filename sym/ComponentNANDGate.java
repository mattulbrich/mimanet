import de.matul.lepton_sim.data.*;
import de.matul.lepton_sim.sim.*;

import java.util.ArrayList;
import java.util.List;

import static de.matul.lepton_sim.sim.Signal.*;

public class ComponentNANDGate implements SimComponent {

    // Order: 0, 1, X, Z
    private static final Signal[][] AND = new Signal[][] {
            { ZERO,   ZERO,  ZERO,  ZERO },
            { ZERO,   ONE,   ERROR, ERROR },
            { ZERO,   ERROR, ERROR, ERROR },
            { ZERO,   ERROR, ERROR, ERROR }
    };

    private static final Signal[] NOT = new Signal[] {
            ONE, ZERO, ERROR, ERROR
    };

    private List<Pin> ins;
    private Pin out;

    @Override
    public void register(Simulator simulator, Component component, Netlist compnet) {
        ins = new ArrayList<>();
        if (compnet.getImplementation().getAttribute("bus") != null) {
            int width = compnet.getImplementation().getIntAttribute("bus", 1);
            for (int i = 0; i < width; i++) {
                ins.add(simulator.getPin(component, "IN#" + i));
            }
        } else {
            int width = compnet.getImplementation().getIntAttribute("width", 2);
            for (int i = 1; i <= width; i++) {
                ins.add(simulator.getPin(component, "IN" + i + "#0"));
            }
        }
        this.out = simulator.getPin(component, "OUT#0");

        ins.forEach(in -> in.setDriver(this, true));
        out.setDriver(this, false);
    }

    @Override
    public void recomputeOutputs(Simulator simulator) {
        Signal signal = ONE;
        for (Pin in : ins) {
            signal = AND[signal.ordinal()][in.getNetSignal().ordinal()];
        }
        Signal sout = NOT[signal.ordinal()];
        out.setDriverSignal(simulator, sout);
    }

}
