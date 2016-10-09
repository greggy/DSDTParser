/*
 * Decompiled with CFR 0_115.
 */
package dsdtparser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CheckList extends JFrame {
    public CheckList() {
        super("AKCheckList");
        String[] listData = new String[]{"Apple", "Orange", "Cherry", "Blue Berry", "Banana", "Red Plum", "Watermelon"};
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            System.out.println("Unable to find System Look and Feel");
        }
        final JList<CheckBoxItem> listCheckBox = new JList<CheckBoxItem>(this.buildCheckBoxItems(listData.length));
        final JList<String> listDescription = new JList<String>(listData);
        listDescription.setSelectionMode(0);
        listDescription.addMouseListener((MouseListener)new MouseAdapter(){

            public void mouseClicked(MouseEvent me) {
                CheckBoxItem item;
                if (me.getClickCount() != 2) {
                    return;
                }
                int selectedIndex = listDescription.locationToIndex(me.getPoint());
                if (selectedIndex < 0) {
                    return;
                }
                //item.setChecked(!(item = (CheckBoxItem)listCheckBox.getModel().getElementAt(selectedIndex)).isChecked());
                listCheckBox.repaint();
            }
        });
        listCheckBox.setCellRenderer(new CheckBoxRenderer());
        listCheckBox.setSelectionMode(0);
        listCheckBox.addMouseListener((MouseListener)new MouseAdapter(){

            public void mouseClicked(MouseEvent me) {
                CheckBoxItem item;
                int selectedIndex = listCheckBox.locationToIndex(me.getPoint());
                if (selectedIndex < 0) {
                    return;
                }
                //item.setChecked(!(item = (CheckBoxItem)listCheckBox.getModel().getElementAt(selectedIndex)).isChecked());
                listDescription.setSelectedIndex(selectedIndex);
                listCheckBox.repaint();
            }
        });
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setRowHeaderView(listCheckBox);
        scrollPane.setViewportView(listDescription);
        listDescription.setFixedCellHeight(20);
        listCheckBox.setFixedCellHeight(listDescription.getFixedCellHeight());
        listCheckBox.setFixedCellWidth(20);
        this.getContentPane().add(scrollPane);
        this.setSize(350, 200);
        this.setVisible(true);
    }

    private CheckBoxItem[] buildCheckBoxItems(int totalItems) {
        CheckBoxItem[] checkboxItems = new CheckBoxItem[totalItems];
        for (int counter = 0; counter < totalItems; ++counter) {
            checkboxItems[counter] = new CheckBoxItem();
        }
        return checkboxItems;
    }

    public static void main(String[] args) {
        CheckList checkList = new CheckList();
        checkList.addWindowListener(new WindowAdapter(){

            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    class CheckBoxRenderer
    extends JCheckBox
    implements ListCellRenderer {
        public CheckBoxRenderer() {
            this.setBackground(UIManager.getColor("List.textBackground"));
            this.setForeground(UIManager.getColor("List.textForeground"));
        }

        public Component getListCellRendererComponent(JList listBox, Object obj, int currentindex, boolean isChecked, boolean hasFocus) {
            this.setSelected(((CheckBoxItem)obj).isChecked());
            return this;
        }
    }

    class CheckBoxItem {
        private boolean isChecked;

        public CheckBoxItem() {
            this.isChecked = false;
        }

        public boolean isChecked() {
            return this.isChecked;
        }

        public void setChecked(boolean value) {
            this.isChecked = value;
        }
    }

}

