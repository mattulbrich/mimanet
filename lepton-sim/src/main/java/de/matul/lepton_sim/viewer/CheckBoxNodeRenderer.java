package de.matul.lepton_sim.viewer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;


// https://github.com/aterai/java-swing-tips

public class CheckBoxNodeRenderer implements TreeCellRenderer {
    private final JCheckBox checkBox = new JCheckBox();
    private final DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof DefaultMutableTreeNode node) {
            Object userObject = node.getUserObject();
            if (userObject instanceof Selectable selectable) {
                checkBox.setText(node.toString());
                checkBox.setSelected(selectable.isSelected());
                checkBox.setEnabled(tree.isEnabled());
                checkBox.setFont(tree.getFont());
                checkBox.setOpaque(false);
                checkBox.setFocusable(false);
                return checkBox;
            }
        }
        return renderer.getTreeCellRendererComponent(
                tree, value, selected, expanded, leaf, row, hasFocus);
    }
}
