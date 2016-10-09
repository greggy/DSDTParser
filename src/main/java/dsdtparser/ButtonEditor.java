/*
 * Decompiled with CFR 0_115.
 */
package dsdtparser;

import dsdtparser.fixes.AutoFix;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.EventObject;

public class ButtonEditor
extends JButton
implements TableCellEditor {
    DSDTParserView parent;

    public ButtonEditor(DSDTParserView view) {
        super("Fix");
        this.parent = view;
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        String line = (String)((DefaultTableModel)table.getModel()).getValueAt(row, 2);
        if (AutoFix.isKnownError(line)) {
            this.buttonPressed(table, row, column);
        }
        this.setEnabled(false);
        return this;
    }

    public void cancelCellEditing() {
        System.out.println("Cancel");
    }

    public boolean stopCellEditing() {
        return true;
    }

    public Object getCellEditorValue() {
        return null;
    }

    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    public boolean shouldSelectCell(EventObject anEvent) {
        return true;
    }

    public void addCellEditorListener(CellEditorListener l) {
    }

    public void removeCellEditorListener(CellEditorListener l) {
    }

    protected void fireCellEditing(ChangeEvent e) {
    }

    private void buttonPressed(JTable table, int row, int column) {
        String line = (String)((DefaultTableModel)table.getModel()).getValueAt(row, 2);
        int number = (Integer)((DefaultTableModel)table.getModel()).getValueAt(row, 0);
        AutoFix.fix(this.parent, line, number);
        ((DefaultTableModel)table.getModel()).setValueAt("Fixed :-)", row, 2);
    }
}

