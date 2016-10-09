/*
 * Decompiled with CFR 0_115.
 */
package dsdtparser;

import dsdtparser.fixes.AutoFix;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ButtonRenderer
extends JButton
implements TableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.setText("Fix");
        String line = (String)((DefaultTableModel)table.getModel()).getValueAt(row, 2);
        if (!AutoFix.isKnownError(line)) {
            this.setEnabled(false);
        } else {
            this.setEnabled(true);
        }
        return this;
    }
}

