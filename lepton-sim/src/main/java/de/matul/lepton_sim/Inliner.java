package de.matul.lepton_sim;

import de.matul.lepton_sim.data.Component;
import de.matul.lepton_sim.data.Net;
import de.matul.lepton_sim.data.Netlist;
import de.matul.lepton_sim.data.NetlistParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class Inliner {

    private static final Set<String> PAD_DEVICES = Set.of("IPAD", "OPAD", "IOPAD");
    private final ArrayList<Component> components = new ArrayList<>();
    private final ArrayList<Net> nets = new ArrayList<>();

    private Map<String, Netlist> library = new HashMap<>();

    public Netlist inline(String filename) throws IOException {
        Netlist netlist = NetlistParser.parseFile(filename);
        inlineComponents(netlist);
        // TODO resolveNets();
        return new Netlist(components, nets);
    }

    private void inlineComponents(Netlist netlist) throws IOException {

        // toplevel pads to components
        netlist.getComponents().stream()
                .filter(c -> PAD_DEVICES.contains(c.getDevice()))
                .forEach(components::add);

        nets.addAll(netlist.getNets());

        Deque<Component> todo = new LinkedList<>(netlist.getComponents());
        while (!todo.isEmpty()) {
            Component component = todo.removeFirst();
            if(PAD_DEVICES.contains(component.getDevice())) {
                // ignore pads ...
                continue;
            }
            if (component.getDevice().equals("BUSTAP")) {
                // pass thru bus tap
                components.add(component);
                continue;
            }
            Netlist compNetlist = getLibNetlist(component.getDevice());
            if (compNetlist.isImplementation()) {
                // pass thru implementations
                components.add(component);
                continue;
            }
            Netlist prefixed = compNetlist.addPrefix(component.getName());
            nets.addAll(prefixed.getNets());
            todo.addAll(prefixed.getComponents());
        }
    }

    private Netlist getLibNetlist(String device) throws IOException {
        Netlist cached = library.get(device);
        if (cached != null) {
            return cached;
        }

        Path libPath = Paths.get("lib", device + ".net");
        if(Files.isReadable(libPath)) {
            Netlist netlist = NetlistParser.parseFile(libPath);
            library.put(device, netlist);
            return netlist;
        }

        Path symPath = Paths.get("sym", device + ".net");
        if(Files.isReadable(symPath)) {
            Netlist netlist = NetlistParser.parseFile(symPath);
            library.put(device, netlist);
            return netlist;
        }

        throw new IllegalArgumentException("Device " + device + " unknown.");
    }
}
