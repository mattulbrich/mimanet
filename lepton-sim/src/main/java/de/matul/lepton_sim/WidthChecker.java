package de.matul.lepton_sim;

import de.matul.lepton_sim.data.Component;
import de.matul.lepton_sim.data.Net;
import de.matul.lepton_sim.data.Netlist;
import de.matul.lepton_sim.data.NetlistParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class WidthChecker {

    public void checkWidths(String file) throws IOException {
        String filename = file;
        Netlist netlist = NetlistParser.parseFile(filename);
        checkWidth(netlist);
        // netlist.print(System.out);
    }

    public void checkWidth(Netlist netlist) throws IOException {
        Map<String, Integer> widthMap = new HashMap<>();

        for (Component component : netlist.getComponents()) {
            switch (component.getDevice()) {
                case "IPAD", "OPAD", "IOPAD" -> {
                    int w = component.getWidth();
                    widthMap.put(component.getName() + " " + component.getDevice(), w);
                }

                case "BUSTAP" -> {
                    int[] spec = component.expandSpec();
                    widthMap.put(component.getName() + " tapped", spec.length);
                    widthMap.put(component.getName() + " bus", max(spec) + 1);
                }

                case "IMPLEMENTATION" -> {
                    System.out.println("Implementation detected");
                    return;
                }

                default -> {
                    for (Component c : Library.getLibNetlist(component.getDevice()).getComponents()) {
                        if (c.getDevice().matches("(I|O|IO)PAD")) {
                            widthMap.put(component.getName() + " " + c.getName(), c.getWidth());
                        }
                    }
                }
            }
        }

        for (Net net : netlist.getNets()) {
            int width = -1;
            int reqWidthForBuses = 0;
            for (String connectedPin : net.getConnectedPins()) {
                Integer w;
                if (connectedPin.contains("#")) {
                    w = 1;
                } else {
                    w = widthMap.get(connectedPin);
                    if (w == null) {
                        throw new NoSuchElementException("Unknown pin or no width on pin " + connectedPin);
                    }
                }
                if(connectedPin.endsWith(" bus")) {
                    reqWidthForBuses = Math.max(w, reqWidthForBuses);
                } else {
                    if(width == -1) {
                        width = w;
                    } else {
                        if (width != w) {
                            widthMap.entrySet().forEach(System.out::println);
                            throw new IllegalArgumentException(
                                    String.format("Inconsistent width %d and %d on %s (%s)",
                                            width, w, net.getNames().get(0), net));
                        }
                    }
                }
            }
            if (width == -1) {
                width = reqWidthForBuses;
            } else {
                if (reqWidthForBuses > width) {
                    throw new IllegalArgumentException(
                            String.format("Tap %d above width %d on %s",
                                    reqWidthForBuses, width, net.getNames().get(0)));
                }
            }

            net.setWidth(width);
        }
    }

    private static int max(int[] values) {
        int maxValue = Integer.MIN_VALUE;
        for (int i : values) {
            if (i > maxValue) {
                maxValue = i;
            }
        }
        return maxValue;
    }
}
