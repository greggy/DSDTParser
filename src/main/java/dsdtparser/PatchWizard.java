/*
 * Decompiled with CFR 0_115.
 */
package dsdtparser;

import javax.swing.*;
import java.awt.*;

public class PatchWizard
extends JFrame {
    public PatchWizard() {
        this.initComponents();
    }

    private void initComponents() {
        this.setDefaultCloseOperation(3);
        this.setName("Form");
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 400, 32767));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 300, 32767));
        this.pack();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable(){

            public void run() {
                new PatchWizard().setVisible(true);
            }
        });
    }

}

