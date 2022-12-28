package de.matul.lepton_sim.sim;

import de.matul.lepton_sim.data.Net;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Recorder {
    private String[] allPinNames;
    private List<String> log = new ArrayList<>();
    private JSONArray nets;

    public void registerPins(Map<String, Pin> allPins) {
        allPinNames = allPins.keySet().toArray(String[]::new);
    }

    public void recordPins(Map<String, Pin> allPins) {
        char[] chars = new char[allPinNames.length];
        int i = 0;
        for (Pin value : allPins.values()) {
            chars[i++] = value.getNetSignal().toChar();
        }
        log.add(new String(chars));
    }

    public String toJSON() {
        JSONObject result = new JSONObject();
        result.put("pins", new JSONArray(Arrays.asList(allPinNames)));
        result.put("nets", nets);
        result.put("log", new JSONArray(log));
        return result.toString(4);
    }

    public void registerNets(List<Net> nets) {
        JSONArray ns = new JSONArray();
        for (Net net : nets) {
            JSONObject o = new JSONObject();
            o.put("names", new JSONArray(net.getNames()));
            o.put("pins", new JSONArray(net.getConnectedPins().
                    stream().map(x -> x.replace(' ', '.')).
                    toArray()));
            ns.put(o);
        }
        this.nets = ns;
    }
}
