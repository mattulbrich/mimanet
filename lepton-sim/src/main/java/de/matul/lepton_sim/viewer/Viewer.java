package de.matul.lepton_sim.viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Viewer extends JFrame {

    private static final double ZOOMFACTOR = 1.2;
    private final Signals signals;
    private final Data data;
    private File lastLoaded;

    public Viewer(String filename) throws IOException {
        super("Waveform Viewer");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        this.data = new Data();
        if (filename != null) {
            data.load(filename);
            lastLoaded = new File(filename);
        }

        this.signals = new Signals(data);
        JScrollPane scrollPane = new JScrollPane(signals);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        contentPane.add(makeToolbar(), BorderLayout.NORTH);

        setSize(1000,1000);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private JToolBar makeToolbar() {
        JToolBar result = new JToolBar();
        JButton b = new JButton("Load");
        b.addActionListener(this::load);
        result.add(b);
        b = new JButton("Reload");
        b.addActionListener(this::reload);
        result.add(b);
        result.addSeparator();
        b = new JButton("Select channels");
        b.addActionListener(this::selectChannels);
        result.add(b);
        b = new JButton("Save channel list");
        b.addActionListener(this::saveChannelList);
        result.add(b);
        b = new JButton("Load channel list");
        b.addActionListener(this::loadChannelList);
        result.add(b);
        result.addSeparator();
        b = new JButton("Zoom in");
        b.addActionListener(this::zoomIn);
        result.add(b);
        b = new JButton("Zoom out");
        b.addActionListener(this::zoomOut);
        result.add(b);
        return result;
    }

    private void loadChannelList(ActionEvent actionEvent) {
        JFileChooser jfc = new JFileChooser(".");
        int answer = jfc.showOpenDialog(this);
        if (answer == JFileChooser.APPROVE_OPTION) {
            try {
                List<String> lines = Files.readAllLines(jfc.getSelectedFile().toPath());
                data.setSelectedChannels(lines);
                signals.recomputeSizeFromData();
                repaint();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void saveChannelList(ActionEvent actionEvent) {
        JFileChooser jfc = new JFileChooser(".");
        int answer = jfc.showSaveDialog(this);
        if (answer == JFileChooser.APPROVE_OPTION) {
            try {
                Files.write(jfc.getSelectedFile().toPath(),
                        data.getSelectedChannels());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void selectChannels(ActionEvent actionEvent) {
        ChannelSelectionDialog csd = new ChannelSelectionDialog(this, data);
        csd.setVisible(true);
        signals.recomputeSizeFromData();
        repaint();
    }

    private void zoomOut(ActionEvent actionEvent) {
        signals.scaleX(1. / ZOOMFACTOR);
    }

    private void zoomIn(ActionEvent actionEvent) {
        signals.scaleX(ZOOMFACTOR);
    }

    private void reload(ActionEvent actionEvent) {
        if (lastLoaded != null) {
            load(lastLoaded);
        } else {
            JOptionPane.showMessageDialog(this, "No last file.");
        }
    }

    private void load(ActionEvent actionEvent) {
        JFileChooser jfc = new JFileChooser(".");
        int answer = jfc.showOpenDialog(this);
        if (answer == JFileChooser.APPROVE_OPTION) {
            load(jfc.getSelectedFile());
        }
    }

    private void load(File file) {
        try {
            data.load(file.getAbsolutePath());
            signals.recomputeSizeFromData();
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        lastLoaded = file;
    }

    public static void main(String[] args) throws IOException {
        new Viewer(args.length > 0 ? args[0] : null).setVisible(true);
    }
}
