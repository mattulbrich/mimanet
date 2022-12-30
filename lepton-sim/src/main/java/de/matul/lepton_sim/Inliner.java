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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Inliner {

    private static Pattern PAD_PATTERN = Pattern.compile("(.*)\\.([^.]+) (I|IO|O)PAD");
    private static final Set<String> PAD_DEVICES = Set.of("IPAD", "OPAD", "IOPAD", "BUSTAP");
    private final List<Component> components = new ArrayList<>();
    private final List<Net> nets = new ArrayList<>();



    public Netlist inline(String filename) throws IOException {
        Netlist netlist = NetlistParser.parseFile(filename);
        // netlist.print(System.out);
        inlineComponents(netlist);
        // new Netlist(components, nets).print(System.out);
        resolveNets();
        return new Netlist(components, nets);
    }

    private void resolveNets() {

        boolean changed = true;
        while (changed) {
            changed = false;

            for (Iterator<Net> it = nets.iterator(); it.hasNext(); ) {
                Net net = it.next();
                for (Iterator<String> pit = net.getConnectedPins().iterator(); pit.hasNext(); ) {
                    String pin = pit.next();
                    Matcher m = PAD_PATTERN.matcher(pin);
                    if(m.matches()) {
                        String counter = m.group(1) + " " + m.group(2);
                        Net other = findNet(counter);
                        if (other == null) {
                            pit.remove();
                            System.err.println("Unconnected pin: " + counter);
                            continue;
                        }
                        it.remove();
                        other.getConnectedPins().remove(counter);
                        net.getConnectedPins().stream().
                                filter(x -> !x.equals(pin)).
                                forEach(other.getConnectedPins()::add);
                        other.getNames().addAll(net.getNames());
                        other.getNames().add(counter);
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
        nets.addAll(netlist.getNets());

        Deque<Component> todo = new LinkedList<>(netlist.getComponents());
        while (!todo.isEmpty()) {
            Component component = todo.removeFirst();
            if(PAD_DEVICES.contains(component.getDevice())) {
                if (component.isToplevel()) {
                    components.add(component);
                }
                continue;
            }
            components.add(component);
            Netlist compNetlist = Library.getLibNetlist(component.getDevice());
            if (compNetlist.isImplementation()) {
                // pass thru implementations
                continue;
            }
            component.putAttribute("inlined", "true");
            Netlist prefixed = compNetlist.addPrefix(component.getName());
            nets.addAll(prefixed.getNets());
            todo.addAll(prefixed.getComponents());
        }
    }

}
