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

    public void checkWidths(String[] args) throws IOException {
        String filename = args[1];
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
                    widthMap.put(component.getName() + " bus", max(spec));
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
            int max = 0;
            for (String connectedPin : net.getConnectedPins()) {
                Integer w;
                if (connectedPin.contains("#")) {
                    w = 1;
                } else {
                    w = widthMap.get(connectedPin);
                    if (w == null) {
                        throw new NoSuchElementException("No width on pin " + connectedPin);
                    }
                }
                if(connectedPin.endsWith(" bus")) {
                    max = Math.max(w, max);
                } else {
                    if(width == -1) {
                        width = w;
                    } else {
                        if (width != w) {
                            widthMap.entrySet().forEach(System.out::println);
                            throw new IllegalArgumentException(
                                    String.format("Inconsistent width %d and %d on %s",
                                            width, w, net.getNames().get(0)));
                        }
                    }
                }
            }
            if (width == -1) {
                width = max;
            } else {
                if (max >= width) {
                    throw new IllegalArgumentException(
                            String.format("Tap %d above width %d on %s",
                                    max, width, net.getNames().get(0)));
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
