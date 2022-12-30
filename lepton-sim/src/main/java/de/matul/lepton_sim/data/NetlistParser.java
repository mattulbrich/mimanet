package de.matul.lepton_sim.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NetlistParser {

    public static Netlist parse(List<String> lines) {
        List<Component> components = new ArrayList<>();
        List<Net> nets = new ArrayList<>();

        Component curComponent = null;
        for (String line : lines) {
            if(line.trim().length() == 0 || line.trim().startsWith("#")) {
                continue;
            }

            if(line.startsWith("Component ")) {
                curComponent = new Component(line.substring(10));
                components.add(curComponent);
            } else if (line.startsWith("Net ")) {
                String[] parts = line.split(":");
                String[] names = parts[0].substring(4).trim().split(" *, *");
                String[] pins = parts[1].trim().split(" *, *");
                nets.add(new Net(names, new ArrayList<>(Arrays.asList(pins))));
            } else if (line.startsWith("  .")) {
                String[] parts = line.substring(3).split("=");
                curComponent.putAttribute(parts[0].trim(), parts[1].trim());
            } else {
                System.err.println("Illegal line " + line);
            }
        }

        return new Netlist(components, nets);
    }

    public static Netlist parseFile(String file) throws IOException {
        return parseFile(Paths.get(file));
    }

    public static Netlist parseFile(Path libPath) throws IOException {
        return parse(Files.readAllLines(libPath));
    }

    public static Netlist parse(String string) {
        return parse(Arrays.asList(string.split("\n")));
    }
}
