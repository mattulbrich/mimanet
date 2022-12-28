package de.matul.lepton_sim.viewer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class Data {

    public static final int ERROR = -1;
    public static final int HIGH_IMP = -2;
    public static final int MIXED = -3;

    private JSONObject rawData;

    private List<String> selectedChannels = new ArrayList<>();

    private int[][] resolvedData;

    public int getValue(String channel, int cycle) {
        int index = selectedChannels.indexOf(channel);
        if (index < 0) {
            throw new NoSuchElementException(channel);
        }
        return getValue(index, cycle);
    }

    public int getValue(int channel, int cycle) {
        return resolvedData[channel][cycle];
    }

    public static boolean isBus(String channel) {
        return !channel.contains("#");
    }

    public void load(String filename) throws IOException {
        this.rawData = new JSONObject(Files.readString(Paths.get(filename)));
        setSelectedChannels(
                rawData.getJSONArray("pins").toList().
                        stream().map(Object::toString).toList());
    }

    private void setSelectedChannels(List<String> pins) {
        this.selectedChannels = pins;
        resolveData();
    }

    private void resolveData() {
        JSONArray trace = rawData.getJSONArray("log");
        JSONArray allpins = rawData.getJSONArray("pins");
        Map<String, Integer> pinMap = new HashMap<>();
        for (int i = 0; i < allpins.length(); i++) {
            pinMap.put((String) allpins.get(i), i);
        }

        int[][] values = new int[selectedChannels.size()][trace.length()];
        for (int i = 0; i < values.length; i++) {
            int pinIndex = pinMap.get(selectedChannels.get(i));
            for (int j = 0; j < values[i].length; j++) {
                values[i][j] = toInt(((String) trace.get(j)).charAt(pinIndex));
            }
        }

        this.resolvedData = values;
    }

    public int getTraceLength() {
        if (resolvedData == null || resolvedData.length == 0)
            return 0;
        else
            return resolvedData[0].length;
    }

    private int toInt(char charAt) {
        switch (charAt) {
            case '0': return 0;
            case '1': return 1;
            case 'Z': return HIGH_IMP;
            default: return ERROR;
        }
    }

    public List<String> getSelectedChannels() {
        return Collections.unmodifiableList(selectedChannels);
    }

    public int countChannels() {
        return selectedChannels.size();
    }
}
