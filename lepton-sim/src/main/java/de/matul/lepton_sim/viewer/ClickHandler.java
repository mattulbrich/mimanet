package de.matul.lepton_sim.viewer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClickHandler extends MouseAdapter {

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && e.getComponent() instanceof JTree tree) {
            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            if (path != null && path.getLastPathComponent() instanceof DefaultMutableTreeNode treeNode) {
                if (treeNode.getUserObject() instanceof Selectable selectable) {
                    selectable.setSelected(!selectable.isSelected());
                    tree.repaint();
                }
            }
        }
    }
}
