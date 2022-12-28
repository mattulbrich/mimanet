package de.matul.lepton_sim;

import de.matul.lepton_sim.data.Component;
import de.matul.lepton_sim.data.Net;
import de.matul.lepton_sim.data.Netlist;
import de.matul.lepton_sim.data.NetlistParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class Graphvizer {
    private List<String> graph = new ArrayList<>();

    public void visualize(String filename) throws IOException {
        Netlist netlist = NetlistParser.parseFile(filename);

        for (Component component : netlist.getComponents()) {
            String name = component.getName();

            List<String> padList = getPads(component.getDevice());
            String pads = padList.stream().
                    map(x -> String.format("<%s>%s", x, x)).
                    collect(Collectors.joining(" | "));

            graph.add(
                    String.format("\"%s\" [ label = \"%s | %s\"%n  shape=\"record\" ];",
                            name, name, pads));

        }

        for (Net net : netlist.getNets()) {
            String name = net.getNames().get(0);
            List<String> pins = net.getConnectedPins();
            for (int i = 0; i < pins.size()-1; i++) {
                String[] pinI = pins.get(i).split(" ");
                pinI[1] = pinI[1].replaceAll("#.*", "");
                String[] pinJ = pins.get(i+1).split(" ");
                pinJ[1] = pinJ[1].replaceAll("#.*", "");
                graph.add(String.format("\"%s\":%s -- \"%s\":%s [ label=\"%s\" ];%n",
                        pinI[0], pinI[1], pinJ[0], pinJ[1], name));
            }

        }

    }

    private List<String> getPads(String device) throws IOException {
        switch (device) {
            case "IPAD":
            case "OPAD":
            case "IOPAD":
                return List.of(device);

            case "BUSTAP":
                return List.of("bus", "tapped");

            default:
                return Library.getLibNetlist(device).getPads().stream().map(Component::getName).toList();
        }
    }

    public String getGraph() {
        return "graph G { graph [\n" +
                "rankdir = \"LR\"\n" +
                "];\n" + String.join("\n", graph) + "}";
    }
}
