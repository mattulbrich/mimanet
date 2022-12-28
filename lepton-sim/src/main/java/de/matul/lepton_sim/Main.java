package de.matul.lepton_sim;

import de.matul.lepton_sim.data.Netlist;
import de.matul.lepton_sim.data.NetlistParser;
import de.matul.lepton_sim.sim.Simulator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
                    String compilation = compiler.expand(args);
                    if (args.length > 2) {
                        Files.writeString(Paths.get(args[2]), compilation);
                    } else {
                        System.out.println(compilation);
                    }
                    break;

                case "simulate":
                    inliner = new Inliner();
                    netlist = inliner.inline(args[1]);
                    new WidthChecker().checkWidth(netlist);
                    netlist = new BusExpander().expand(netlist);
                    Simulator simulator = new Simulator();
                    simulator.simulate(netlist);
                    simulator.print();
                    if (args.length > 2) {
                        simulator.printTo(args[2]);
                    }
                    break;

                default:
                    System.err.println("No such command: " + args[0]);
                    System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

}