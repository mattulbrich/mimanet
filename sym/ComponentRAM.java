import de.matul.lepton_sim.data.Component;
import de.matul.lepton_sim.data.Netlist;
import de.matul.lepton_sim.sim.Pin;
import de.matul.lepton_sim.sim.PinBus;
import de.matul.lepton_sim.sim.Signal;
import de.matul.lepton_sim.sim.Simulator;
import de.matul.lepton_sim.sim.TickReceiver;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import static de.matul.lepton_sim.sim.Signal.ONE;
import static de.matul.lepton_sim.sim.Signal.ZERO;

public class ComponentRAM implements TickReceiver {

    private static final int MEM_SIZE = 1 << 20;
    private static final int ERROR_VAL = -1;
    private static final int HIGH_IMP_VAL = -2;
    private PinBus addr;
    private Pin write;
    private Pin read;
    private int[] memory;
    private int waitingTime;
    private int value;
    private Pin waitingOn;

    private int rememberedAddress, rememberedValue;
    private PinBus data;

    @Override
    public void register(Simulator simulator, Component component, Netlist compnet) {
        this.read = simulator.getPin(component, "R#0");
        this.write = simulator.getPin(component, "W#0");
        this.addr = simulator.getPinBus(component, "ADDR");
        this.data = simulator.getPinBus(component, "DATA");

        read.setDriver(this, true);
        write.setDriver(this, true);
        addr.setDriver(this, true);

        this.memory = new int[MEM_SIZE];
        String img = compnet.getImplementation().getAttribute("image");
        if (img != null) {
            try {
                fillMemory(compnet.getImplementation().getAttribute("image"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        simulator.addTickReceiver(this);
    }

    private void fillMemory(String file) throws IOException {
        try(DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            for (int i = 0; i < memory.length; i++) {
                memory[i] = dis.readInt();
            }
        }
    }

    @Override
    public void recomputeOutputs(Simulator simulator) {
        if (value == HIGH_IMP_VAL) {
            data.setAllDriverSignals(simulator, Signal.HIGH_IMP);
        } else if(value < 0) {
            data.setAllDriverSignals(simulator, Signal.ERROR);
        } else {
            data.setDriverBusSignal(simulator, value);
        }
    }

    @Override
    public void tick(int no) {
        this.value = ERROR_VAL;
        if(read.getNetSignal() == ONE) {
            tickRead();
        } else if(write.getNetSignal() == ONE) {
            tickWrite();
        }
    }

    private void tickWrite() {
        if (read.getNetSignal() != ZERO) {
            waitingOn = null;
            return;
        }
        if(waitingOn == write) {
            if(rememberedAddress != addr.getNetBusValue() ||
                    rememberedValue != data.getNetBusValue()) {
                rememberedAddress = (int) addr.getNetBusValue();
                rememberedValue = (int) data.getNetBusValue();

                memory[rememberedAddress] = ERROR_VAL;
                waitingTime = 0;
            } else {
                waitingTime ++;
                if (waitingTime >= 6) {
                    memory[rememberedAddress] = rememberedValue;
                }
            }
        } else {
            rememberedAddress = (int) addr.getNetBusValue();
            rememberedValue = (int) data.getNetBusValue();
            waitingOn = write;
            waitingTime = 0;
        }

        value = HIGH_IMP_VAL;
    }

    private void tickRead() {
        if (write.getNetSignal() != ZERO) {
            waitingOn = null;
            return;
        }
        if(waitingOn == read) {
            if(rememberedAddress != addr.getNetBusValue()) {
                rememberedAddress = (int) addr.getNetBusValue();
                waitingTime = 0;
            } else {
                waitingTime ++;
                if (waitingTime >= 6) {
                    value = memory[rememberedAddress];
                }
            }
        } else {
            waitingOn = read;
            waitingTime = 0;
            rememberedAddress = (int) addr.getNetBusValue();
        }
    }


}
