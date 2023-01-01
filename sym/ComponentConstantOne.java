import de.matul.lepton_sim.data.*;
import de.matul.lepton_sim.sim.*;

import static de.matul.lepton_sim.sim.Signal.*;

public class ComponentConstantOne implements TickReceiver {

    private Pin out;

    @Override
    public void register(Simulator simulator, Component component, Netlist compnet) {
        this.out = simulator.getPin(component, "ONE#0");
        out.setDriver(this, false);
        simulator.addTickReceiver(this);
    }

    @Override
    public void recomputeOutputs(Simulator simulator) {
        out.setDriverSignal(simulator, ONE);
        simulator.removeTickReceiver(this);
    }

    @Override
    public void tick(int cycleNo) {
        // do nothing, but trigger recomputation!
    }
}
