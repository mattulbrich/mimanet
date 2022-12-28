package de.matul.lepton_sim;

import de.matul.lepton_sim.data.Component;
import de.matul.lepton_sim.data.Net;
import de.matul.lepton_sim.data.Netlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BusExpander {


    private static final String BUS_PATTERN = "([^ ]+) bus#([0-9]+)";
    private static final String TAPPED_PATTERN = "([^ ]+) tapped#([0-9]+)";
    private static final Pattern COMP_TAPPED_PATTERN = Pattern.compile(TAPPED_PATTERN);

    public Netlist expand(Netlist netlist) {

        var newNets = new ArrayList<Net>();
        for (Net net : netlist.getNets()) {
            for (int i = 0; i < net.getWidth(); i++) {
                var fi = i;
                var newNames = net.getNames().stream().map(x -> x + "#" + fi).toList();
                var newPins = new ArrayList<>(net.getConnectedPins().stream().map(x -> x + "#" + fi).toList());
                newNets.add(new Net(newNames, newPins));
            }
        }

        // resolve the "tapped" bus tap ports
        resolveTaps(netlist.getComponents(), newNets);
        removeBuses(newNets);

        return new Netlist(netlist.getComponents(), newNets);
    }

    private void removeBuses(ArrayList<Net> nets) {
        nets.forEach(n -> n.getConnectedPins().removeIf(x -> x.matches(BUS_PATTERN)));
    }

    private void resolveTaps(List<Component> components, ArrayList<Net> nets) {
        var specMap = new HashMap<String, int[]>();
        for (Component component : components) {
            if (component.getDevice().equals("BUSTAP")) {
                specMap.put(component.getName(), component.expandSpec());
            }
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            var it = nets.iterator();
            while (it.hasNext()) {
                Net net = it.next();
                for (String pin : net.getConnectedPins()) {
                    Matcher m = COMP_TAPPED_PATTERN.matcher(pin);
                    if (m.matches()) {
                        int[] spec = specMap.get(m.group(1));
                        int index = Integer.parseInt(m.group(2));
                        int tap = spec[index];
                        String toFind = String.format("%s bus#%d", m.group(1), tap);
                        Net other = findNet(nets, toFind);
                        if (other == null) {
                            System.err.println("Unconnected pin: " + toFind);
                            continue;
                        }
                        net.getConnectedPins().remove(pin);
                        other.getConnectedPins().addAll(net.getConnectedPins());
                        other.getNames().addAll(net.getNames());
                        it.remove();
                        changed = true;
                        break;
                    }
                }
            }
        }
    }

    private Net findNet(ArrayList<Net> nets, String toFind) {
        return nets.stream().filter(n -> n.getConnectedPins().contains(toFind)).findAny().orElse(null);
    }
}