package de.matul.lepton_sim.viewer;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.List;

public class Signals extends JComponent {

    private static final Color LIGHTEST_GRAY = new Color(0xf0f0f0);

    private static final int PIN_HEIGHT = 60;
    private static final int MARGIN_Y = 10;
    private static final int STEP_X = 30;
    private static final int NOT_SET = -4;
    private static final int SIGNAL_HEIGHT = PIN_HEIGHT - 2 * MARGIN_Y;
    private final Data data;

    public Signals(Data data) {
        this.data = data;
        recomputeSizeFromData();
    }

    public void recomputeSizeFromData() {
        setPreferredSize(new Dimension(STEP_X * data.getTraceLength(), PIN_HEIGHT * data.countChannels()));
    }

    private class RowHeader extends JComponent {
        private static final int HEADER_WIDTH = 120;
        private static final Dimension PREF_SIZE =
                new Dimension(HEADER_WIDTH, 20);
        private static final int Y_LABEL_OFFSET = 40;

        RowHeader() {
            setBorder(new BevelBorder(BevelBorder.RAISED));
            setOpaque(false);
            setPreferredSize(PREF_SIZE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D graphics2D = (Graphics2D) g;

            //Set  anti-alias!
//            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Set anti-alias for text
            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            List<String> labels = data.getSelectedChannels();
            int y = Y_LABEL_OFFSET + Signals.this.getY();
            for (String label : labels) {
                g.drawString(label, 5, y);
                y += PIN_HEIGHT;
            }
        }
    }

    public void addNotify() {
        super.addNotify();
        configureEnclosingScrollPane();
    }

    private void configureEnclosingScrollPane() {
        Container parent = this;
        while (parent != null) {
            if (parent instanceof JScrollPane scrollPane) {
                scrollPane.setRowHeaderView(new RowHeader());
                break;
            }
            parent = parent.getParent();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        ((Graphics2D)g).setStroke(new BasicStroke(2f));
        int signals = data.countChannels();
        int len = data.getTraceLength();
        int y = 0;
        for (int i = 0; i < signals; i++) {
            int lastValue = NOT_SET;
            g.setColor(i % 2 == 0 ? Color.WHITE : LIGHTEST_GRAY);
            g.fillRect(0, y, getWidth(), PIN_HEIGHT);
            for (int c = 0; c < len; c++) {
                int value = data.getValue(i, c);
                if(value >= 0) {
                    g.setColor(Color.BLACK);
                    if (value != lastValue && lastValue >= 0) {
                        g.drawLine(c * STEP_X, y + MARGIN_Y, c * STEP_X, y + SIGNAL_HEIGHT + MARGIN_Y);
                    }
                    int lvl = y  + MARGIN_Y+ (1 - value) * SIGNAL_HEIGHT;
                    g.drawLine(c * STEP_X, lvl, (c + 1) * STEP_X, lvl);
                } else {
                    if(value == Data.HIGH_IMP) {
                        g.setColor(Color.lightGray);
                    } else {
                        g.setColor(Color.red.brighter());
                    }
                    g.fillRect(c * STEP_X, y + MARGIN_Y, STEP_X, SIGNAL_HEIGHT);
                }
                lastValue = value;
            }
            y += PIN_HEIGHT;
        }
    }
}
