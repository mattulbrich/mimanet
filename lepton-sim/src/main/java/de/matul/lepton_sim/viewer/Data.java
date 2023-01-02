package de.matul.lepton_sim.viewer;

import de.matul.lepton_sim.data.Net;
import de.matul.lepton_sim.data.Netlist;
import de.matul.lepton_sim.data.NetlistParser;
import de.matul.lepton_sim.sim.Recorder;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
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

    private Netlist netlist;

    private List<String> selectedChannels = new ArrayList<>();

    private int[] channelWidths;

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
        this.netlist = NetlistParser.parse(rawData.getString(Recorder.KEY_NETLIST));
        setSelectedChannels(
                netlist.getNets().stream().flatMap(x->x.getNames().stream()).
                        filter(x -> !x.contains("unnamed_net") && !x.contains(" ") && !x.contains("._"))
                        .toList());
    }

    public void setSelectedChannels(List<String> pins) {
        List<String> oldSelection = this.selectedChannels;
        try {
            this.selectedChannels = pins;
            resolveData();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Cannot set channels!");
            this.selectedChannels = oldSelection;
        }
    }

    private void resolveData() {
        JSONArray trace = rawData.getJSONArray(Recorder.KEY_TRACE);
        JSONArray allpins = rawData.getJSONArray(Recorder.KEY_PINS);
        Map<String, Integer> pinMap = new HashMap<>();
        for (int i = 0; i < allpins.length(); i++) {
            pinMap.put((String) allpins.get(i), i);
        }

        int[][] values = new int[selectedChannels.size()][trace.length()];
        int[] widths = new int[selectedChannels.size()];
        for (int i = 0; i < values.length; i++) {
            String channel = selectedChannels.get(i);
            if (channel.contains("#")) {
                List<Integer> pinsForNet = resolveRef(pinMap, channel);
                for (int j = 0; j < values[i].length; j++) {
                    values[i][j] = resolveNetValue(pinsForNet, (String)trace.get(j));
                }
                widths[i] = 1;
            } else {
                List<List<Integer>> pinNetList = new ArrayList<>();
                try {
                    for (int j = 0; true; j++) {
                        pinNetList.add(resolveRef(pinMap, channel + "#" + j));
                        widths[i] = j + 1;
                    }
                } catch (NoSuchElementException ex) {
                    // we are done now ...
                }
                for (int j = 0; j < values[i].length; j++) {
                    int acc = 0;
                    int l = 0;
                    for (List<Integer> pinsForNet : pinNetList) {
                        int v = resolveNetValue(pinsForNet, trace.getString(j));
                        if(v < 0) {
                            acc = ERROR;
                            break;
                        }
                        acc += v << l++;
                    }
                    values[i][j] = acc;
                }
            }
        }

        this.resolvedData = values;
        this.channelWidths = widths;
    }

    private int resolveNetValue(List<Integer> pinsForNet, String values) {
        int value = HIGH_IMP;
        for (Integer pin : pinsForNet) {
            int newVal = toInt(values.charAt(pin));
            value = combineValues(value, newVal);
        }
        return value;
    }

    private int combineValues(int v1, int v2) {
        if (v1 == HIGH_IMP) {
            return v2;
        }
        if (v2 == HIGH_IMP) {
            return v1;
        }
        if (v1 == v2) {
            return v1;
        }
        return ERROR;
    }

    private List<Integer> resolveRef(Map<String, Integer> pinMap, String channel) {
        Integer pinRes = pinMap.get(channel);
        if (pinRes != null) {
            return Collections.singletonList(pinRes);
        }

        for (Net net : netlist.getNets()) {
            if(net.getNames().contains(channel)) {
                return net.getConnectedPins().stream().
                        map(x -> x.replace(' ', '.')).
                        map(pinMap::get).toList();
            }
        }

        throw new NoSuchElementException(channel);
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

    public JSONObject getRawData() {
        return rawData;
    }

    public Netlist getNetlist() {
        return netlist;
    }

    public List<String> getPins() {
        return rawData.getJSONArray(Recorder.KEY_PINS).toList().stream().map(Object::toString).toList();
    }

    public int getChannelWidth(int channel) {
        return channelWidths[channel];
    }
}
