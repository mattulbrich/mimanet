package de.matul.lepton_sim.sim;

public class Pin {
    private final String name;
    private Signal driverSignal = Signal.ERROR;
    private Signal netSignal = Signal.ERROR;
    private SimNet simNet;
    private SimComponent driver;
    private boolean driverReactsToSignal;

    public Pin(String name) {
        // for debugging actually
        this.name = name;
    }

    @Override
    public String toString() {
        return "Pin{" + name + '\'' +
                ", D=" + driverSignal +
                ", N=" + netSignal +
                '}';
    }

    public void setNet(SimNet simNet) {
        this.simNet = simNet;
    }

    public Signal getDriverSignal() {
        return driverSignal;
    }

    public void setDriverSignal(Simulator sim, Signal driverSignal) {
        this.driverSignal = driverSignal;
        if (simNet != null) {
            simNet.recompute(sim);
        }
    }

    public Signal getNetSignal() {
        return netSignal;
    }

    public void setNetSignal(Simulator sim, Signal netSignal) {
        if(netSignal != this.netSignal) {
            this.netSignal = netSignal;
            if (driverReactsToSignal) {
                sim.addToQueue(driver);
            }
        }

    }

    public void setDriver(SimComponent driver, boolean driverReactsToSignal) {
        this.driver = driver;
        this.driverReactsToSignal = driverReactsToSignal;
        if(driverReactsToSignal) {
            // set this to 'Z' in case of pure input signals.
            driverSignal = Signal.HIGH_IMP;
        }
    }

    public SimComponent getDriver() {
        return driver;
    }

    public boolean isDriverReactsToSignal() {
        return driverReactsToSignal;
    }
}
