package de.matul.lepton_sim.viewer;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.NoSuchElementException;

public class Signals extends JComponent {

    private static final Color LIGHTEST_GRAY = new Color(0xf0f0f0);

    private static final Font FONT = new Font(Font.MONOSPACED, Font.BOLD, 16);

    private static final int PIN_HEIGHT = 60;
    private static final int MARGIN_Y = 10;
    private static final int NOT_SET = -4;
    private static final int SLOPE_LEN = 8;
    private static final int SIGNAL_HEIGHT = PIN_HEIGHT - 2 * MARGIN_Y;

    private MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            mouseClick(e);
        }
    };

    private final Data data;

    private int stepX = 30;

    public Signals(Data data) {
        this.data = data;
        addMouseListener(mouseListener);
        recomputeSizeFromData();
    }

    public void recomputeSizeFromData() {
        setPreferredSize(new Dimension(stepX * data.getTraceLength(), PIN_HEIGHT * data.countChannels()));
    }

    public void scaleX(double v) {
        stepX = (int)Math.max(Math.min(stepX * v, 200.), 10.);
        recomputeSizeFromData();
        repaint();
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
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setStroke(new BasicStroke(2f));

        //Set  anti-alias!
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Set anti-alias for text
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int signals = data.countChannels();
        int y = 0;
        for (int i = 0; i < signals; i++) {
            g.setColor(i % 2 == 0 ? Color.WHITE : LIGHTEST_GRAY);
            g.fillRect(0, y, getWidth(), PIN_HEIGHT);
            boolean isBus = data.getChannelWidth(i) > 1;
            if (isBus) {
                paintBus(g, y, i);
            } else {
                paintNet(g, y, i);
            }
            y += PIN_HEIGHT;
        }
    }

    private void paintBus(Graphics g, int y, int i) {
        int lastValue = NOT_SET;
        for (int c = 0; c < data.getTraceLength(); c++) {
            int value = data.getValue(i, c);
            if (value >= 0) {
                g.setColor(Color.BLACK);
                if (value != lastValue) {
                    if (lastValue >= 0) {
                        g.drawLine(c * stepX - SLOPE_LEN, y + MARGIN_Y,
                                c * stepX + SLOPE_LEN, y + SIGNAL_HEIGHT + MARGIN_Y);
                        g.drawLine(c * stepX + SLOPE_LEN, y + MARGIN_Y,
                                c * stepX - SLOPE_LEN, y + SIGNAL_HEIGHT + MARGIN_Y);
                        paintValueLabel(g, y, i, c - 1);
                    } else {
                        g.drawLine(c * stepX, y + MARGIN_Y + SIGNAL_HEIGHT/2,
                                c * stepX + SLOPE_LEN, y + SIGNAL_HEIGHT + MARGIN_Y);
                        g.drawLine(c * stepX, y + MARGIN_Y + SIGNAL_HEIGHT/2,
                                c * stepX + SLOPE_LEN, y + MARGIN_Y);
                    }
                } else {
                    g.drawLine(c * stepX - SLOPE_LEN, y + MARGIN_Y,
                            c * stepX + SLOPE_LEN, y + MARGIN_Y);
                    g.drawLine(c * stepX - SLOPE_LEN, y + MARGIN_Y + SIGNAL_HEIGHT,
                            c * stepX + SLOPE_LEN, y + MARGIN_Y + SIGNAL_HEIGHT);
                }
                g.drawLine(c * stepX + SLOPE_LEN, y + MARGIN_Y,
                        (c + 1) * stepX - SLOPE_LEN, y + MARGIN_Y);
                g.drawLine(c * stepX + SLOPE_LEN, y + MARGIN_Y + SIGNAL_HEIGHT,
                        (c + 1) * stepX - SLOPE_LEN, y + MARGIN_Y + SIGNAL_HEIGHT);
            } else {
                if (lastValue >= 0) {
                    g.drawLine(c * stepX - SLOPE_LEN, y + MARGIN_Y,
                            c * stepX, y + MARGIN_Y + SIGNAL_HEIGHT / 2);
                    g.drawLine(c * stepX - SLOPE_LEN, y + MARGIN_Y + SIGNAL_HEIGHT,
                            c * stepX, y + MARGIN_Y + SIGNAL_HEIGHT / 2);
                    paintValueLabel(g, y, i, c - 1);
                }
                if (value == Data.HIGH_IMP) {
                    g.setColor(Color.lightGray);
                } else {
                    g.setColor(Color.red.brighter());
                }
                g.fillRect(c * stepX, y + MARGIN_Y, stepX, SIGNAL_HEIGHT);
            }
            lastValue = value;
        }
    }

    private void paintValueLabel(Graphics g, int y, int channel, int cycle) {
        int val = data.getValue(channel, cycle);
        int width = data.getChannelWidth(channel);
        int start = cycle - 1;
        while(start >= 0 && data.getValue(channel, start) == val) {
            start --;
        }
        start++;
        String label = String.format("%0" + (width + 3)/4 + "X", val);
        g.setFont(FONT);
        int strLen = SwingUtilities.computeStringWidth(g.getFontMetrics(), label);
        if (strLen > (cycle + 1 - start) * stepX) {
            // label too wide
            return;
        }

        int x = (2 * start + (cycle + 1 - start)) * stepX / 2 - strLen / 2;

        g.drawString(label, x, y + SIGNAL_HEIGHT);
        Graphics gx = g.create();
        /*gx.setColor(Color.green);
        gx.drawRect(x,y+SIGNAL_HEIGHT-20,strLen,20);
        gx.setColor(Color.blue);
        gx.drawRect(start*stepX, y, (cycle-start+1)*stepX, SIGNAL_HEIGHT);*/
    }

    private void paintNet(Graphics g, int y, int i) {
        int lastValue = NOT_SET;
        for (int c = 0; c < data.getTraceLength(); c++) {
            int value = data.getValue(i, c);
            if (value >= 0) {
                g.setColor(Color.BLACK);
                if (value != lastValue && lastValue >= 0) {
                    g.drawLine(c * stepX, y + MARGIN_Y, c * stepX, y + SIGNAL_HEIGHT + MARGIN_Y);
                }
                int lvl = y + MARGIN_Y + (1 - value) * SIGNAL_HEIGHT;
                g.drawLine(c * stepX, lvl, (c + 1) * stepX, lvl);
            } else {
                if (value == Data.HIGH_IMP) {
                    g.setColor(Color.lightGray);
                } else {
                    g.setColor(Color.red.brighter());
                }
                g.fillRect(c * stepX, y + MARGIN_Y, stepX, SIGNAL_HEIGHT);
            }
            lastValue = value;
        }
    }

    private void mouseClick(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            int no = e.getX() / stepX;
            int channelIdx = e.getY() / PIN_HEIGHT;
            int width = data.getChannelWidth(channelIdx);
            StringBuilder msg;
            if(width == 1) {
                String channel = data.getSelectedChannels().get(channelIdx);
                List<String> pins = data.getPinsForNet(channel);
                if(pins == null) {
                    String net = data.getNetForPin(channel);
                    pins = data.getPinsForNet(net);
                    int value = data.getValue(channelIdx, no);
                    msg = new StringBuilder(String.format("Step %d%n%n%s is a primary pin with value %s.%nThe values of the pins in its net %s are:%n",
                            no, channel, net, value));
                } else {
                    int value = data.getValue(channelIdx, no);
                    msg = new StringBuilder(String.format("Step %d%n%n%s is a net with value %s.%nThe values of the pins in the net are:%n",
                            no, channel, value));
                }
                for (String pin : pins) {
                    try {
                        int value = data.getRawPinValue(pin.replace(" ", "."), no);
                        msg.append(String.format("  %-30s : %c%n", pin, value));
                    } catch (NoSuchElementException exception) {
                        msg.append(String.format("  %-30s : not found%n", pin));
                    }
                }
                JTextArea ta = new JTextArea(msg.toString());
                ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
                ta.setEditable(false);
                JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this), ta);

            }

        }
    }
}
