package de.matul.lepton_sim;

import com.sun.source.doctree.InlineTagTree;
import de.matul.lepton_sim.data.Component;
import de.matul.lepton_sim.data.Netlist;
import de.matul.lepton_sim.data.NetlistParser;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws IOException {
        try {
            switch(args[0]) {
                case "sym":
                    SymbolMaker symbolMaker = new SymbolMaker();
                    symbolMaker.createSymbol(args);
                    break;

                case "dbg_out":
                    Netlist netList = NetlistParser.parseFile(args[1]);
                    netList.print(System.out);
                    break;

                case "inline":
                    Inliner inliner = new Inliner();
                    inliner.inline(args[1]).print(System.out);
                    break;

                case "expand":
                    Expander expander = new Expander();
                    expander.expand(args);
                    break;

                default:
                    System.err.println("No such command: ");
                    System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

}