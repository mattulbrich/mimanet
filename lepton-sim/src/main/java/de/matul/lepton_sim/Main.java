package de.matul.lepton_sim;

import de.matul.lepton_sim.data.Netlist;
import de.matul.lepton_sim.data.NetlistParser;

import java.io.IOException;

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

                case "widthcheck":
                    WidthChecker wChecker = new WidthChecker();
                    wChecker.checkWidths(args);
                    break;

                case "inline":
                    Inliner inliner = new Inliner();
                    inliner.inline(args[1]).print(System.out);
                    break;

                case "expand":
                    Netlist netlist = NetlistParser.parseFile(args[1]);
                    new WidthChecker().checkWidth(netlist);
                    BusExpander expander = new BusExpander();
                    expander.expand(netlist).print(System.out);
                    break;

                case "compile":
                    NetlistCompiler compiler = new NetlistCompiler();
                    compiler.expand(args);
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