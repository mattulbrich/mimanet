package de.matul.lepton_sim.sim;

import de.matul.lepton_sim.data.Netlist;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Recorder {
    public static final String KEY_PINS = "pins";
    public static final String KEY_NETLIST = "netlist";
    public static final String KEY_TRACE = "trace";
    private String[] allPinNames;
    private final List<String> trace = new ArrayList<>();
    private Netlist netlist;

    public void registerPins(Map<String, Pin> allPins) {
        allPinNames = allPins.keySet().toArray(String[]::new);
    }

    public void recordPins(Map<String, Pin> allPins) {
        char[] chars = new char[allPinNames.length];
        int i = 0;
        for (Pin value : allPins.values()) {
//            chars[i++] = value.getNetSignal().toChar();
            chars[i++] = value.getDriverSignal().toChar();
        }
        trace.add(new String(chars));
    }

    public String toJSON() {
        JSONObject result = new JSONObject();
        result.put(KEY_PINS, new JSONArray(Arrays.asList(allPinNames)));
        result.put(KEY_NETLIST, netlist.makeString());
        result.put(KEY_TRACE, new JSONArray(trace));
        return result.toString(4);
    }

    public void registerNet(Netlist netlist) {
        this.netlist = netlist;
    }

}
