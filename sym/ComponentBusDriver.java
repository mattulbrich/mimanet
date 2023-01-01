import de.matul.lepton_sim.data.*;
import de.matul.lepton_sim.sim.*;

import static de.matul.lepton_sim.sim.Signal.*;

public class ComponentBusDriver implements SimComponent {

    private Pin in;
    private Pin en;
    private Pin out;

    @Override
    public void register(Simulator simulator, Component component, Netlist compnet) {
        this.in = simulator.getPin(component, "IN#0");
        this.en = simulator.getPin(component, "EN#0");
        this.out = simulator.getPin(component, "OUT#0");

        in.setDriver(this, true);
        en.setDriver(this, true);
        out.setDriver(this, false);
    }

    @Override
    public void recomputeOutputs(Simulator simulator) {        
        Signal signalEn = en.getNetSignal();
        Signal sout;
        switch(signalEn) {
        case ZERO:
            sout = HIGH_IMP;
            break;
        case ONE:
            Signal signalIn = in.getNetSignal();
            sout = signalIn;
            break;
        default:
            sout = ERROR;
        }
        out.setDriverSignal(simulator, sout);
    }

}
