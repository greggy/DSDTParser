/*
 * Decompiled with CFR 0_115.
 */
package dsdtparser;

import dsdtparser.parser.CompilerError;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AutoPatcherCompilerError
extends JFrame {
    private JLabel jLabel1;
    private JScrollPane jScrollPane1;
    private JTextArea jTextArea1;

    public AutoPatcherCompilerError() {
        this.initComponents();
    }

    public void setMessage(ArrayList<CompilerError> erros) {
        String buffer = "";
        for (int i = 0; i < erros.size(); ++i) {
            buffer = buffer + erros.get(i).toString() + "\n";
        }
        this.jTextArea1.setText(buffer);
    }

    private void initComponents() {
        this.jLabel1 = new JLabel();
        this.jScrollPane1 = new JScrollPane();
        this.jTextArea1 = new JTextArea();
        this.setTitle("Compilation error");
        this.setName("Form");
        this.jLabel1.setText("The compilation had errors, please report in the forum.");
        this.jLabel1.setName("jLabel1");
        this.jScrollPane1.setName("jScrollPane1");
        this.jTextArea1.setColumns(20);
        this.jTextArea1.setEditable(false);
        this.jTextArea1.setRows(1);
        this.jTextArea1.setName("jTextArea1");
        this.jScrollPane1.setViewportView(this.jTextArea1);
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false).addComponent(this.jScrollPane1, GroupLayout.Alignment.LEADING).addComponent(this.jLabel1, GroupLayout.Alignment.LEADING, -1, 373, 32767)).addContainerGap(-1, 32767)));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jLabel1).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jScrollPane1, -1, 196, 32767).addContainerGap()));
        this.pack();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable(){

            public void run() {
                new AutoPatcherCompilerError().setVisible(true);
            }
        });
    }

}

