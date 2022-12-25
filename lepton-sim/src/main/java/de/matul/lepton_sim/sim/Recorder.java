package de.matul.lepton_sim.sim;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Recorder {
    private String[] allPinNames;
    private List<String> log = new ArrayList<>();

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

    public void print() {
        JSONObject result = new JSONObject();
        result.append("pins", new JSONArray(Arrays.asList(allPinNames)));
        result.append("log", new JSONArray(log));
        System.out.println(result.toString());
    }


}
