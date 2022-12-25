package de.matul.lepton_sim.sim;

public class Pin {
    private Signal driverSignal;
    private Signal netSignal;
    private SimNet simNet;
    private SimComponent driver;
    private boolean driverReactsToSignal;

    public Pin(String name) {
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
    }

    public SimComponent getDriver() {
        return driver;
    }

    public boolean isDriverReactsToSignal() {
        return driverReactsToSignal;
    }
}
