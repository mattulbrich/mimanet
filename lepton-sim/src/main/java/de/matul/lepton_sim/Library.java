package de.matul.lepton_sim;

import de.matul.lepton_sim.data.Netlist;
import de.matul.lepton_sim.data.NetlistParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Library {

    private static Map<String, Netlist> library = new HashMap<>();

    public static Netlist getLibNetlist(String device) throws IOException {
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
