/*
 * Decompiled with CFR 0_115.
 */
package dsdtparser;

import dsdtparser.parser.DSDTItem;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class TreeNodeRenderer
extends DefaultTreeCellRenderer {
    private Icon deviceIcon;
    private Icon rootIcon;
    private Icon folderIcon;
    private Icon folderOpenIcon;
    private Icon methodIcon;
    private Icon processorIcon;
    private Icon thermalIcon;

    public TreeNodeRenderer(Icon icon1, Icon icon2, Icon icon3, Icon icon4, Icon icon5, Icon icon6, Icon icon7) {
        this.deviceIcon = icon1;
        this.rootIcon = icon2;
        this.folderIcon = icon3;
        this.folderOpenIcon = icon4;
        this.methodIcon = icon5;
        this.processorIcon = icon6;
        this.thermalIcon = icon7;
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (!c.isEnabled()) {
            c.setForeground(new Color(100, 100, 100));
        }
        c.setEnabled(true);
        if (leaf) {
            if (this.getNodeType(value).equalsIgnoreCase("Device")) {
                this.setIcon(this.deviceIcon);
            } else if (this.getNodeType(value).equalsIgnoreCase("Processor")) {
                this.setIcon(this.processorIcon);
            } else if (this.getNodeType(value).equalsIgnoreCase("ThermalZone")) {
                this.setIcon(this.thermalIcon);
            } else {
                this.setIcon(this.methodIcon);
            }
        } else if (this.getNodeType(value).equalsIgnoreCase("Device")) {
            this.setIcon(this.deviceIcon);
        } else if (this.getNodeType(value).equalsIgnoreCase("Processor")) {
            this.setIcon(this.processorIcon);
        } else if (this.getNodeType(value).equalsIgnoreCase("DefinitionBlock")) {
            this.setIcon(this.rootIcon);
        } else if (this.getNodeType(value).equalsIgnoreCase("ThermalZone")) {
            this.setIcon(this.thermalIcon);
        } else if (expanded) {
            this.setIcon(this.folderOpenIcon);
        } else {
            this.setIcon(this.folderIcon);
        }
        return this;
    }

    protected String getNodeType(Object value) {
        try {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            DSDTItem nodeInfo = (DSDTItem)node.getUserObject();
            return nodeInfo.getTipo();
        }
        catch (ClassCastException e) {
            try {
                String temp = (String)value;
                return temp.split(" ")[0];
            }
            catch (Exception ex) {
                return "Error";
            }
        }
    }
}

