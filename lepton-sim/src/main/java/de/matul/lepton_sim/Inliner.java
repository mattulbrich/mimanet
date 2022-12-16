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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Inliner {

    private static Pattern PAD_PATTERN = Pattern.compile("(.*)\\.([^.]+) (I|IO|O)PAD");
    private static final Set<String> PAD_DEVICES = Set.of("IPAD", "OPAD", "IOPAD");
    private final ArrayList<Component> components = new ArrayList<>();
    private final ArrayList<Net> nets = new ArrayList<>();

    private Map<String, Netlist> library = new HashMap<>();

    public Netlist inline(String filename) throws IOException {
        Netlist netlist = NetlistParser.parseFile(filename);
        netlist.print(System.out);
        inlineComponents(netlist);
        new Netlist(components, nets).print(System.out);
        resolveNets();
        return new Netlist(components, nets);
    }

    private void resolveNets() {

        boolean changed = true;
        while (changed) {
            changed = false;

            for (Iterator<Net> it = nets.iterator(); it.hasNext(); ) {
                Net net = it.next();
                for (String pin : net.getConnectedPins()) {
                    Matcher m = PAD_PATTERN.matcher(pin);
                    if(m.matches()) {
                        String counter = m.group(1) + " " + m.group(2);
                        Net other = findNet(counter);
                        if (other == null) {
                            throw new RuntimeException("Missing pin: " + counter);
                        }
                        other.getConnectedPins().remove(counter);
                        net.getConnectedPins().stream().
                                filter(x -> !x.equals(pin)).
                                forEach(other.getConnectedPins()::add);
                        other.getNames().addAll(net.getNames());
                        it.remove();
                        changed = true;
                        break;
                    }
                }
            }
        }

    }

    private Net findNet(String name) {
        return nets.stream().
                filter(x -> x.getConnectedPins().contains(name)).
                findAny().orElse(null);
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
