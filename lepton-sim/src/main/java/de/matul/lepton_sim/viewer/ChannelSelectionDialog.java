package de.matul.lepton_sim.viewer;

import de.matul.lepton_sim.data.Component;
import de.matul.lepton_sim.data.Net;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
    private final Data data;
    private MyNode treeRoot;
    private MouseListener rightClick = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            rightClick(e);
        }
    };

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
        tree.addMouseListener(rightClick);

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
        setResizable(true);
    }

    private void ok(ActionEvent actionEvent) {

        List<String> sels = new ArrayList<>();
        Enumeration<TreeNode> en = treeRoot.breadthFirstEnumeration();
        while (en.hasMoreElements()) {
            MyNode node = (MyNode) en.nextElement();
            if (node.getUserObject() instanceof Entry entry) {
                if (entry.isSelected()) {
                    sels.add(entry.name);
                }
            }
        }
        data.setSelectedChannels(sels);
        dispose();
    }

    private void markSelected(MyNode node) {
        Set<String> sels = new HashSet<>(data.getSelectedChannels());
        Enumeration<TreeNode> en = node.breadthFirstEnumeration();
        while (en.hasMoreElements()) {
            node = (MyNode) en.nextElement();
            if (node.getUserObject() instanceof Entry entry) {
                if (sels.contains(entry.name)) {
                    entry.setSelected(true);
                }
            }
        }
    }

    private void rightClick(MouseEvent e) {
        if(SwingUtilities.isRightMouseButton(e)) {
            JTree tree = (JTree) e.getComponent();
            int row = tree.getClosestRowForLocation(e.getX(), e.getY());
            tree.setSelectionRow(row);
            MyNode sel = (MyNode) tree.getSelectionPath().getLastPathComponent();
            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem item = new JMenuItem("Deselect all below");
            item.addActionListener(a -> selectRec(sel, false));
            popupMenu.add(item);
            item = new JMenuItem("Select all below");
            item.addActionListener(a -> selectRec(sel, true));
            popupMenu.add(item);
            item = new JMenuItem("Select all direct children");
            item.addActionListener(a -> selectChildren(sel, true));
            popupMenu.add(item);
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private void selectChildren(MyNode sel, boolean b) {
        Enumeration<TreeNode> en = sel.children();
        while (en.hasMoreElements()) {
            MyNode node = (MyNode) en.nextElement();
            if (node.getUserObject() instanceof Entry entry) {
                entry.setSelected(b);
            }
        }
    }

    private void selectRec(MyNode sel, boolean b) {
        Enumeration<TreeNode> en = sel.breadthFirstEnumeration();
        while (en.hasMoreElements()) {
            MyNode node = (MyNode) en.nextElement();
            if (node.getUserObject() instanceof Entry entry) {
                entry.setSelected(b);
            }
        }
    }

    private MyNode makeTree() {
        MyNode root = new MyNode();
        List<Component> comps = new ArrayList<>(data.getNetlist().getComponents());
        comps.sort(Comparator.comparing(Component::getName));
        Map<String, MyNode> nodeMap = new HashMap<>();
        addComponentsToTree(root, nodeMap);
        addNetsToTree(root, nodeMap);
        addPinsToTree(root, nodeMap);
        root.sort();
        return root;
    }

    private void addComponentsToTree(MyNode root, Map<String, MyNode> nodeMap) {
        for (Component component : data.getNetlist().getComponents()) {
            String name = component.getName();
            int dot = name.lastIndexOf('.');
            MyNode parent;
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
            MyNode newNode =
                    new MyNode(title);
            parent.add(newNode);
            nodeMap.put(name, newNode);
        }
    }

    private void addNetsToTree(MyNode root, Map<String, MyNode> nodeMap) {
        for (Net net : data.getNetlist().getNets()) {
            for (String name : net.getNames()) {
                int hash = name.indexOf('#');
                String baseName = name.substring(0, hash);
                MyNode parent = nodeMap.get(baseName);
                if(parent == null) {
                    MyNode grandparent;
                    String shortName;
                    int space = name.lastIndexOf(' ');
                    if (space < 0) {
                        int dot = name.lastIndexOf('.');
                        if (dot < 0) {
                            grandparent = root;
                            shortName = "Net " + baseName;
                        } else {
                            grandparent = nodeMap.get(name.substring(0, dot));
                            shortName = "Net " + baseName.substring(dot + 1);
                        }
                    } else {
                        grandparent = nodeMap.get(name.substring(0, space));
                        shortName = "Pin " + baseName.substring(space + 1);
                    }
                    if(shortName.startsWith("Net unnamed_net") || shortName.startsWith("Net _")) {
                        continue;
                    }
                    parent = new MyNode(new Entry(shortName, baseName));
                    grandparent.add(parent);
                    nodeMap.put(baseName, parent);
                }
                MyNode newNode = new MyNode(new Entry(name.substring(hash), name));
                parent.add(newNode);
                nodeMap.put(name, newNode);
            }
        }
    }

    private void addPinsToTree(MyNode root, Map<String, MyNode> nodeMap) {
        for (String pin : data.getPins()) {
            int hash = pin.indexOf('#');
            String baseName = pin.substring(0, hash);
            MyNode parent = nodeMap.get(baseName);
            if(parent == null) {
                int dot = pin.lastIndexOf('.');
                MyNode grandparent;
                String shortName;
                if (dot >= 0) {
                    grandparent = nodeMap.get(pin.substring(0, dot));
                    shortName = baseName.substring(dot + 1);
                } else {
                    // There is no dot ==> toplevel
                    grandparent = root;
                    shortName = baseName;
                }
                String title = String.format("Pin %s (primary)", shortName);
                parent = new MyNode(new Entry(title, baseName));
                grandparent.add(parent);
                nodeMap.put(baseName, parent);
            }
            MyNode newNode = new MyNode(new Entry(pin.substring(hash), pin));
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

    static class MyNode extends DefaultMutableTreeNode implements Comparator<Object> {

        public MyNode(Object userObject) {
            super(userObject);
        }

        public MyNode() {
            super();
        }

        public Entry getEntry() {
            return (Entry) getUserObject();
        }

        public void sort() {
            if (children != null) {
                children.sort(this);
                children.forEach(x -> ((MyNode)x).sort());
            }
        }

        @Override
        public int compare(Object o1, Object o2) {
            MyNode m1 = (MyNode) o1;
            MyNode m2 = (MyNode) o2;
            Object uo1 = m1.getUserObject();
            Object uo2 = m2.getUserObject();
            if(uo1 instanceof String s1) {
                if(uo2 instanceof String s2) {
                    return s1.compareTo(s2);
                }
                return -1;
            }
            if (uo1 instanceof Entry e1) {
                if (uo2 instanceof Entry e2) {
                    return e1.title.compareTo(e2.title);
                }
                return 1;
            }
            return 0;
        }
    }

}
