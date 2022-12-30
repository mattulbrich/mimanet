package de.matul.lepton_sim.viewer;

import de.matul.lepton_sim.data.Component;
import de.matul.lepton_sim.data.Net;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ChannelSelectionDialog extends JDialog {
    private static final Pattern LASTDOT = Pattern.compile("(.*)\\.([^.]*)");
    private final Data data;
    private DefaultMutableTreeNode treeRoot;

    public ChannelSelectionDialog(JFrame parent, Data data) {
        super(parent, "Select Channels", true);
        this.data = data;
        initGui();
    }

    private void initGui() {
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        this.treeRoot = makeTree();
        markSelected(treeRoot);
        JTree tree = new JTree(treeRoot);
        tree.setCellRenderer(new CheckBoxNodeRenderer());
        tree.setEditable(false);
        tree.addMouseListener(new ClickHandler());

        cp.add(new JScrollPane(tree), BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton ok = new JButton("OK");
        ok.addActionListener(this::ok);
        buttons.add(ok);
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());
        buttons.add(cancel);

        cp.add(buttons, BorderLayout.SOUTH);

        setSize(400,400);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    private void ok(ActionEvent actionEvent) {

        List<String> sels = new ArrayList<>();
        Enumeration<TreeNode> en = treeRoot.breadthFirstEnumeration();
        while (en.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) en.nextElement();
            if (node.getUserObject() instanceof Entry entry) {
                if (entry.isSelected()) {
                    sels.add(entry.name);
                }
            }
        }
        data.setSelectedChannels(sels);
        dispose();
    }

    private void markSelected(DefaultMutableTreeNode node) {
        Set<String> sels = new HashSet<>(data.getSelectedChannels());
        Enumeration<TreeNode> en = node.breadthFirstEnumeration();
        while (en.hasMoreElements()) {
            node = (DefaultMutableTreeNode) en.nextElement();
            if (node.getUserObject() instanceof Entry entry) {
                if (sels.contains(entry.name)) {
                    entry.setSelected(true);
                }
            }
        }
    }

    private DefaultMutableTreeNode makeTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        List<Component> comps = new ArrayList<>(data.getNetlist().getComponents());
        comps.sort(Comparator.comparing(Component::getName));
        Map<String, DefaultMutableTreeNode> nodeMap = new HashMap<>();
        addComponentsToTree(root, nodeMap);
        addNetsToTree(root, nodeMap);
        addPinsToTree(root, nodeMap);
        return root;
    }

    private void addComponentsToTree(DefaultMutableTreeNode root, Map<String, DefaultMutableTreeNode> nodeMap) {
        for (Component component : data.getNetlist().getComponents()) {
            String name = component.getName();
            int dot = name.lastIndexOf('.');
            DefaultMutableTreeNode parent;
            String shortName;
            if (dot >= 0) {
                parent = nodeMap.get(name.substring(0, dot));
                shortName = name.substring(dot + 1);
            } else {
                // There is no dot ==> toplevel
                parent = root;
                shortName = name;
            }
            String title = String.format("Component %s (%s)", shortName, component.getDevice());
            DefaultMutableTreeNode newNode =
                    new DefaultMutableTreeNode(title);
            parent.add(newNode);
            nodeMap.put(name, newNode);
        }
    }

    private void addNetsToTree(DefaultMutableTreeNode root, Map<String, DefaultMutableTreeNode> nodeMap) {
        for (Net net : data.getNetlist().getNets()) {
            for (String name : net.getNames()) {
                DefaultMutableTreeNode parent;
                String shortName;
                int slash = name.lastIndexOf(' ');
                if (slash < 0) {
                    int dot = name.lastIndexOf('.');
                    if (dot < 0) {
                        parent = root;
                        shortName = "Net " + name;
                    } else {
                        parent = nodeMap.get(name.substring(0, dot));
                        shortName = "Net " + name.substring(dot + 1);
                    }
                } else {
                    parent = nodeMap.get(name.substring(0, slash));
                    shortName = "Pin " + name.substring(slash + 1);
                }

                if(shortName.startsWith("Net unnamed_net")) {
                    continue;
                }

                DefaultMutableTreeNode newNode =
                        new DefaultMutableTreeNode(new Entry(shortName, name));
                parent.add(newNode);
                nodeMap.put(name, newNode);
            }
        }
    }

    private void addPinsToTree(DefaultMutableTreeNode root, Map<String, DefaultMutableTreeNode> nodeMap) {
        for (String pin : data.getPins()) {
            int dot = pin.lastIndexOf('.');
            DefaultMutableTreeNode parent;
            String shortName;
            if (dot >= 0) {
                parent = nodeMap.get(pin.substring(0, dot));
                shortName = pin.substring(dot + 1);
            } else {
                // There is no dot ==> toplevel
                parent = root;
                shortName = pin;
            }
            String title = String.format("Pin %s (primary)", shortName);
            DefaultMutableTreeNode newNode =
                    new DefaultMutableTreeNode(new Entry(title, pin));
            parent.add(newNode);
            nodeMap.put(pin, newNode);
        }
    }

    private static class Entry implements Selectable {
        private boolean selected;
        private final String title;

        private final String name;

        public Entry(String title, String name) {
            this.title = title;
            this.name = name;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        @Override
        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public String getTitle() {
            return title;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return title;
        }
    }

}
