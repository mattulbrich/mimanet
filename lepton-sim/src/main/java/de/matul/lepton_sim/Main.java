package de.matul.lepton_sim;

import de.matul.lepton_sim.data.Netlist;
import de.matul.lepton_sim.data.NetlistParser;
import de.matul.lepton_sim.sim.Simulator;
import freemarker.template.TemplateException;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Command
public class Main {

    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(new Main());
        if (args.length == 0) {
            commandLine.usage(System.out);
        } else {
            int exitCode = commandLine.execute(args);
            System.exit(exitCode);
        }
    }

    @Command(name = "sym",
            description = "Create symbol from netlist (.net -> .sym)")
    public void symCommand(@Parameters(paramLabel = "<file>", description = "file to operate on") String file,
                           @Parameters(paramLabel = "<outFile>", description = "file to write to", arity = "0..1") String toFile) throws IOException {
        SymbolMaker symbolMaker = new SymbolMaker();
        PrintStream os;
        if (toFile != null) {
            os = new PrintStream(Files.newOutputStream(Paths.get(toFile)));
        } else {
            os = System.out;
        }
        symbolMaker.createSymbol(file, os);
    }

    @Command(name = "dbg_out",
            description = "parse and print a netlist (.net -> .net)")
    public void dgbOutCommand(@Parameters(paramLabel = "<file>", description = "file to operate on") String file,
                              @Parameters(paramLabel = "<outFile>", description = "file to write to", arity = "0..1") String toFile) throws IOException {
        Netlist netList = NetlistParser.parseFile(file);
        PrintStream os;
        if (toFile != null) {
            os = new PrintStream(Files.newOutputStream(Paths.get(toFile)));
        } else {
            os = System.out;
        }
        netList.print(os);
    }

    @Command(name = "widthcheck",
            description = "check consistency of width annotations in a netlist (.net -> check)")
    public void widthCheckCommand(@Parameters(paramLabel = "<file>", description = "file to operate on") String file,
                                  @Parameters(paramLabel = "<outFile>", description = "file to write to", arity = "0..1") String toFile) throws IOException {
        WidthChecker wChecker = new WidthChecker();
        wChecker.checkWidths(file);
    }

    @Command(name = "inline",
            description = "inline the elements embedded in a netlist (.net -> .net)")
    public void inlineCommand(@Parameters(paramLabel = "<file>", description = "file to operate on") String file,
                              @Parameters(paramLabel = "<outFile>", description = "file to write to", arity = "0..1") String toFile) throws IOException {
        Inliner inliner = new Inliner();
        PrintStream os;
        if (toFile != null) {
            os = new PrintStream(Files.newOutputStream(Paths.get(toFile)));
        } else {
            os = System.out;
        }
        inliner.inline(file).print(os);
    }

    @Command(name = "expand",
            description = "expand buses with width>1 to single lines (.net -> .net)")
    public void expandCommand(@Parameters(paramLabel = "<file>", description = "file to operate on") String file,
                              @Parameters(paramLabel = "<outFile>", description = "file to write to", arity = "0..1") String toFile) throws IOException {
        Netlist netlist = NetlistParser.parseFile(file);
        new WidthChecker().checkWidth(netlist);
        BusExpander expander = new BusExpander();
        PrintStream os;
        if (toFile != null) {
            os = new PrintStream(Files.newOutputStream(Paths.get(toFile)));
        } else {
            os = System.out;
        }
        expander.expand(netlist).print(os);
    }


    @Command(name = "inline+expand",
            description = "inline embedded elements and expand buses (.net -> .net)")
    public void inlineAndExpandCommand(@Parameters(paramLabel = "<file>", description = "file to operate on") String file,
                                       @Parameters(paramLabel = "<outFile>", description = "file to write to", arity = "0..1") String toFile) throws IOException {
        Inliner inliner = new Inliner();
        Netlist netlist = inliner.inline(file);
        new WidthChecker().checkWidth(netlist);
        netlist = new BusExpander().expand(netlist);
        PrintStream os;
        if (toFile != null) {
            os = new PrintStream(Files.newOutputStream(Paths.get(toFile)));
        } else {
            os = System.out;
        }
        netlist.print(os);
    }

    @Command(name ="compile",
            description = "expand templates to full netlists (.props -> .net)")
    public void compileCommand(@Parameters(paramLabel = "<inFile>", description = "file to operate on", arity = "1") String file,
                               @Parameters(paramLabel = "<outFile>", description = "file to write to", arity = "0..1") String toFile)
            throws TemplateException, IOException {
        NetlistCompiler compiler = new NetlistCompiler();
        String compilation = compiler.expand(file);
        if (toFile != null) {
            Files.writeString(Paths.get(toFile), compilation);
        } else {
            System.out.println(compilation);
        }
    }

    @Command(name ="simulate",
            description = "run a simulation of a fully expanded netlist")
    public void simulateCommand(@Parameters(paramLabel = "<inFile>", description = "file to operate on", arity = "1") String file,
                                @Parameters(paramLabel = "<outFile>", description = "file to write to", arity = "0..1") String toFile)
            throws Exception {
        Inliner inliner = new Inliner();
        Netlist netlist = inliner.inline(file);
        try {
            new WidthChecker().checkWidth(netlist);
            netlist = new BusExpander().expand(netlist);
            Simulator simulator = new Simulator();
            simulator.simulate(netlist);
            if (toFile != null) {
                simulator.printTo(toFile);
            } else {
                simulator.print();
            }
        } catch (Exception ex) {
            System.err.println("Problematic netlist: ");
            netlist.print(System.err);
            throw ex;
        }
    }

    @Command(name = "graphviz",
            description = "produce a graphviz output")
    public void graphvizCommand(@Parameters(paramLabel = "<inFile>", description = "file to operate on", arity = "1") String file,
                                @Parameters(paramLabel = "<outFile>", description = "file to write to", arity = "0..1") String toFile)
            throws Exception {
        Graphvizer gv = new Graphvizer();
        gv.visualize(file);
        if (toFile != null) {
            Files.writeString(Paths.get(toFile), gv.getGraph());
        } else {
            System.out.println(gv.getGraph());
        }
    }

}