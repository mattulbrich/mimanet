import de.matul.lepton_sim.data.*;
import de.matul.lepton_sim.sim.*;

import static de.matul.lepton_sim.sim.Signal.*;

public class ComponentClock implements TickReceiver {

    private Pin out;
    private int factor;
    private boolean inverted;
    private Signal signal;

    @Override
    public void register(Simulator simulator, Component component, Netlist compnet) {
        this.out = simulator.getPin(component, "CLK#0");
        out.setDriver(this, false);

        this.factor = component.getIntAttribute("factor", 1);
        this.inverted = "true".equals(component.getAttribute("inverted"));

        simulator.addTickReceiver(this);
    }

    @Override
    public void recomputeOutputs(Simulator simulator) {
        out.setDriverSignal(simulator, signal);
    }

    @Override
    public void tick(int no) {
        boolean firstHalf = (no % (factor*2)) < factor;
        signal = firstHalf != inverted ? ZERO : ONE;
    }
        

}
