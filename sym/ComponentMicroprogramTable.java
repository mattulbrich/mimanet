import de.matul.lepton_sim.data.Component;
import de.matul.lepton_sim.data.Netlist;
import de.matul.lepton_sim.sim.Pin;
import de.matul.lepton_sim.sim.PinBus;
import de.matul.lepton_sim.sim.Signal;
import de.matul.lepton_sim.sim.SimComponent;
import de.matul.lepton_sim.sim.Simulator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.matul.lepton_sim.sim.Signal.ERROR;
import static de.matul.lepton_sim.sim.Signal.ONE;
import static de.matul.lepton_sim.sim.Signal.ZERO;

public class ComponentMicroprogramTable implements SimComponent {


    private long table[] = new long[256];

    private PinBus addr;
    private PinBus data;

    @Override
    public void register(Simulator simulator, Component component, Netlist compnet) {
        this.addr = simulator.getPinBus(component, "ADDR");
        this.data = simulator.getPinBus(component, "DATA");

        addr.setDriver(this, true);
        data.setDriver(this, false);

        String img = compnet.getImplementation().getAttribute("table");
        if (img != null) {
            try {
                fillTable(compnet.getImplementation().getAttribute("table"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void recomputeOutputs(Simulator simulator) {
        int a = (int)addr.getNetBusValue();
        if (a >= 0) {
            data.setDriverBusSignal(simulator, table[a]);
        } else {
            data.setAllDriverSignals(simulator, ERROR);
        }
    }

    private void fillTable(String filename) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filename));
        Map<String, Integer> labelMap = new HashMap<>();
        Map<String, Integer> bitMap = new HashMap<>();
        int pc = 0;
        for (String line : lines) {
            switch (line.charAt(0)) {
                case '=':
                    pc = Integer.decode(line.substring(1).trim());
                    break;
                case '$':
                    int eq = line.indexOf('=');
                    bitMap.put(line.substring(1, eq - 1).trim(), Integer.decode(line.substring(eq + 1).trim()));
                case '#':
                    labelMap.put(line.substring(1).trim(), pc);
                    break;
                case ':':
                    pc++;
                    break;
            }
        }
        pc = 0;
        for (String line : lines) {
            switch (line.charAt(0)) {
                case '=':
                    pc = Integer.decode(line.substring(1).trim());
                    break;
                case ':':
                    String[] cmds = line.substring(1).trim().split(" +");
                    long value = 0;
                    for (String cmd : cmds) {
                        if(cmd.startsWith("#")) {
                            value |= labelMap.get(cmd.substring(1));
                        } else {
                            value |= (1L << bitMap.get(cmd));
                        }
                    }
                    table[pc] = value;
                    pc++;
                    break;
            }
        }
    }
}
