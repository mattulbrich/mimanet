package de.matul.lepton_sim;

import de.matul.lepton_sim.data.Component;
import de.matul.lepton_sim.data.Netlist;
import de.matul.lepton_sim.data.NetlistParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class SymbolMaker {

    private static final Comparator<? super NumberedPin> COMPARATOR =
            (x, y) -> y.number - x.number;
    private static final Set<String> PAD_DEVICES =
            Set.of("IPAD", "OPAD", "IOPAD");

    private static final int STEP = 100;

    private static final int BETWEEN_PINS = 3 * STEP;
    private static final int BORDER = 2 * STEP;
    private static final int WIDTH = 13 * STEP;
    private static final int LEAD = 3 * STEP;

    private record NumberedPin(String pin, int width, int number) { }

    List<NumberedPin> inPins = new ArrayList<>();
    List<NumberedPin> outPins = new ArrayList<>();
    List<NumberedPin> controlPins = new ArrayList<>();


    public void createSymbol(String[] args) throws IOException {
        String in = args[1];
        Netlist netlist = NetlistParser.parseFile(in);
        identifyPins(netlist);
        int max = Math.max(inPins.size(), outPins.size());
        int height = 2 * BORDER + (max - 1) * BETWEEN_PINS;

        System.out.println("v 20221211 2");
        // type x y width height color width capstyle dashstyle dashlength dashspace filltype fillwidth angle1 pitch1 angle2 pitch2
        System.out.printf("B %d 0 %d %d 3 0 0 0 -1 -1 0 -1 -1 -1 -1 -1%n", LEAD, WIDTH, height);

        int y = height - BORDER;
        for (NumberedPin inPin : inPins) {
            System.out.printf("P 0 %d %d %d 1 0 0%n", y, LEAD, y);
            System.out.println("{");
            System.out.printf("T %d %d 9 8 1 1 0 0 1%n", LEAD + STEP/2, y);
            System.out.println("pinlabel=" + inPin.pin);
            System.out.printf("T %d %d 5 9 0 1 0 2 1%n", LEAD + STEP/2, y);
            System.out.println("pintype=in");
            if (inPin.width != 1) {
                System.out.printf("T %d %d 10 5 1 1 0 3 1%n", LEAD / 2, y + STEP/2);
                System.out.println("width=" + inPin.width);
            }
            System.out.println("}");
            y -= BETWEEN_PINS;
        }

        y = height - BORDER;
        for (NumberedPin outPin : outPins) {
            System.out.printf("P %d %d %d %d 1 0 0%n",
                    WIDTH + 2 * LEAD, y, WIDTH + LEAD, y);
            System.out.println("{");
            System.out.printf("T %d %d 9 8 1 1 0 6 1%n", WIDTH + LEAD - STEP/2, y);
            System.out.println("pinlabel=" + outPin.pin);
            System.out.printf("T %d %d 5 9 0 1 0 8 1%n", WIDTH + LEAD - STEP/2, y);
            System.out.println("pintype=out");
            System.out.println("}");
            y -= BETWEEN_PINS;
        }

        System.out.printf("T %d %d 9 10 1 1 0 0 1%n", LEAD + STEP/2, height + STEP / 2);
        System.out.println("device=" + new File(in).getName().replaceAll("\\.net$", ""));
        System.out.printf("T %d %d 9 10 1 1 0 6 1%n", WIDTH + LEAD - STEP/2, height + STEP / 2);
        System.out.println("refdes=U?");

    }

    private void identifyPins(Netlist netlist) {
        for (Component component : netlist.getComponents()) {
            if(PAD_DEVICES.contains(component.getDevice())) {
                int number = component.getPinnumber();
                NumberedPin e = new NumberedPin(component.getName(), component.getWidth(), number);
                if ("control".equals(component.getAttribute("pintype"))) {
                    controlPins.add(e);
                } else if ("IPAD".equals(component.getDevice())) {
                    inPins.add(e);
                } else {
                    outPins.add(e);
                }
            }
        }
        inPins.sort(COMPARATOR);
        outPins.sort(COMPARATOR);
        controlPins.sort(COMPARATOR);
    }

}
