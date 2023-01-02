import de.matul.lepton_sim.data.Component;
import de.matul.lepton_sim.data.Netlist;
import de.matul.lepton_sim.sim.Pin;
import de.matul.lepton_sim.sim.Signal;
import de.matul.lepton_sim.sim.Simulator;
import de.matul.lepton_sim.sim.TickReceiver;

import java.util.ArrayList;
import java.util.List;

import static de.matul.lepton_sim.sim.Signal.ONE;
import static de.matul.lepton_sim.sim.Signal.ZERO;

public class ComponentResetSender implements TickReceiver {

    private Pin out;
    private boolean inverted;
    private Signal signal;
    private List<Range> spec;

    private record Range(int from, int to) {
        public boolean contains(int no) {
            return from <= no && no <= to;
        }
    }

    @Override
    public void register(Simulator simulator, Component component, Netlist compnet) {
        this.out = simulator.getPin(component, "R#0");
        out.setDriver(this, false);

        this.spec = toRanges(component.getAttribute("spec"));
        this.inverted = "true".equals(component.getAttribute("inverted"));

        simulator.addTickReceiver(this);
    }

    private List<Range> toRanges(String spec) {
        List<Range> result = new ArrayList<>();
        for (String s : spec.split(" *, *")) {
            String[] parts = s.split("-");
            int from = Integer.parseInt(parts[0]);
            if(parts.length == 1) {
                result.add(new Range(from, from));
            } else {
                int to = Integer.parseInt(parts[1]);
                result.add(new Range(from, to));
            }
        }
        return result;
    }

    @Override
    public void recomputeOutputs(Simulator simulator) {
        out.setDriverSignal(simulator, signal);
    }

    @Override
    public void tick(int no) {
        for (Range range : spec) {
            if (range.contains(no)) {
                signal = inverted ? ZERO : ONE;
                return;
            }
        }
        signal = inverted ? ONE : ZERO;
    }

}
