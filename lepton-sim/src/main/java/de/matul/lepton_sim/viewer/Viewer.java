package de.matul.lepton_sim.viewer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Viewer extends JFrame {

    private final Signals signals;
    private final Data data;

    public Viewer() throws IOException {
        super("Waveform Viewer");
        getContentPane().setLayout(new BorderLayout());

        this.data = new Data();
        data.load("test.json");

        this.signals = new Signals(data);
        JScrollPane scrollPane = new JScrollPane(signals);

        getContentPane().add(scrollPane, BorderLayout.CENTER);
        setSize(1000,1000);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) throws IOException {
        new Viewer().setVisible(true);
    }
}
