package de.matul.lepton_sim;

import de.matul.lepton_sim.data.Component;
import de.matul.lepton_sim.data.Netlist;
import de.matul.lepton_sim.data.NetlistParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws IOException {
        switch(args[0]) {
            case "sym":
                SymbolMaker symbolMaker = new SymbolMaker();
                symbolMaker.createSymbol(args);
                break;
            default:
                System.err.println("No such command: ");
        }
    }

}