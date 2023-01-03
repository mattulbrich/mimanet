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
    private static final int DELAY = 5;
    private PinBus addr;
    private Pin write;
    private Pin read;
    private int[] memory;
    private int waitingSince;
    private int value;
    private Pin waitingOn;

    private int rememberedAddress, rememberedValue;
    private PinBus data;
    private int currentTick;

    private static void log(String format, Object... args) {
        /*if (args.length == 0) {
            System.out.println(format);
        } else {
            System.out.printf(format, args);
        }*/
    }

    @Override
    public void register(Simulator simulator, Component component, Netlist compnet) {
        this.read = simulator.getPin(component, "R#0");
        this.write = simulator.getPin(component, "W#0");
        this.addr = simulator.getPinBus(component, "ADDR");
        this.data = simulator.getPinBus(component, "DATA");

        read.setDriver(this, true);
        write.setDriver(this, true);
        addr.setDriver(this, true);
        data.setDriver(this, true);

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
        update();
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
        // we are on the update list ...
        currentTick = no;
    }
    
    private void update() {
        log("Update in " + currentTick);
        this.value = HIGH_IMP_VAL;
        if(read.getNetSignal() == ONE) {
            tickRead();
        } else if(write.getNetSignal() == ONE) {
            tickWrite();
        } else {
            waitingOn = null;
        }
        log("End of update: " );
        log("value = " + value);
        log("waitingSince = " + waitingSince);
        log("waitingOn = " + waitingOn);
    }

    private void tickWrite() {
        if (read.getNetSignal() != ZERO) {
            this.value = ERROR_VAL;
            waitingOn = null;
            return;
        }
        if(waitingOn == write) {
            if(rememberedAddress != -1) {
                if (rememberedAddress != addr.getNetBusValue() ||
                        rememberedValue != data.getNetBusValue()) {
                    rememberedAddress = (int) addr.getNetBusValue();
                    rememberedValue = (int) data.getNetBusValue();

                    memory[rememberedAddress] = ERROR_VAL;
                    log("xxx");
                    waitingSince = currentTick;
                } else {
                    if (currentTick - waitingSince >= DELAY) {
                        log("Storing %x at %x tick %d%n", rememberedValue, rememberedAddress, currentTick);
                        memory[rememberedAddress] = rememberedValue;
                    } else {
                        log("Storing ERR at %x tick %d%n", rememberedAddress, currentTick);
                        memory[rememberedAddress] = ERROR_VAL;
                    }
                }
            } else {
                System.err.println("Caution: Writing to ERROR address in tick " + currentTick);
            }
        } else {
            rememberedAddress = (int) addr.getNetBusValue();
            rememberedValue = (int) data.getNetBusValue();
            waitingOn = write;
            log("xxx");
            waitingSince = currentTick;
        }
    }

    private void tickRead() {
        if (write.getNetSignal() != ZERO) {
            this.value = ERROR_VAL;
            waitingOn = null;
            return;
        }
        this.value = ERROR_VAL;
        if(waitingOn == read) {
            if(rememberedAddress != addr.getNetBusValue()) {
                rememberedAddress = (int) addr.getNetBusValue();
                log("Resetting read address to %x at tick %d%n", rememberedAddress, currentTick);
                waitingSince = currentTick;
            } else {
                if (currentTick - waitingSince >= DELAY) {
                    if(rememberedAddress >= 0) {
                        value = memory[rememberedAddress];
                        log("Reading %x at %x tick %d%n", value, rememberedAddress, currentTick);
                    }
                } else {
                    log("Waiting for result since %d in tick %d%n", waitingSince, currentTick);
                }
            }
        } else {
            log("Reading initiated in %d%n", currentTick);
            waitingOn = read;
            waitingSince = currentTick;
            rememberedAddress = (int) addr.getNetBusValue();
        }
    }

}
