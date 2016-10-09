/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.jdesktop.application.Action
 *  org.jdesktop.application.Application
 *  org.jdesktop.application.ApplicationActionMap
 *  org.jdesktop.application.ApplicationContext
 */
package dsdtparser;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class AutoPatcherAbout
extends JFrame {
    private JButton jButton1;
    private JLabel jLabel1;
    private JLabel jLabel11;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;

    public AutoPatcherAbout() {
        this.initComponents();
    }

    private void initComponents() {
        this.jLabel1 = new JLabel();
        this.jLabel2 = new JLabel();
        this.jLabel3 = new JLabel();
        this.jLabel4 = new JLabel();
        this.jLabel5 = new JLabel();
        this.jLabel6 = new JLabel();
        this.jLabel7 = new JLabel();
        this.jButton1 = new JButton();
        this.jLabel11 = new JLabel();
        this.setTitle("About");
        this.setName("Form");
        this.setResizable(false);
        this.jLabel1.setFont(new Font("Monaco", 0, 10));
        this.jLabel1.setText("<html>DSDT Auto-Patcher is a simplified version of DSDT Editor.<br>For more information and other software refer to olarila.com site.</html>");
        this.jLabel1.setName("jLabel1");
        this.jLabel2.setFont(this.jLabel2.getFont().deriveFont(this.jLabel2.getFont().getStyle() | 1));
        this.jLabel2.setText("Software:");
        this.jLabel2.setName("jLabel2");
        this.jLabel3.setFont(this.jLabel3.getFont().deriveFont(this.jLabel3.getFont().getStyle() | 1));
        this.jLabel3.setText("Release version:");
        this.jLabel3.setName("jLabel3");
        this.jLabel4.setName("jLabel4");
        this.jLabel5.setText("Cassandro Davi Emer (el coniglio)");
        this.jLabel5.setName("jLabel5");
        this.jLabel6.setText("cassandro@gmail.com");
        this.jLabel6.setName("jLabel6");
        this.jLabel7.setText("0.7 [Sep 25 2011]");
        this.jLabel7.setName("jLabel7");
        ApplicationActionMap actionMap = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getActionMap((Class)AutoPatcherAbout.class, (Object)this);
        this.jButton1.setAction(actionMap.get("close"));
        this.jButton1.setName("jButton1");
        this.jButton1.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent evt) {
                AutoPatcherAbout.this.jButton1ActionPerformed(evt);
            }
        });
        this.jLabel11.setIcon(new ImageIcon(this.getClass().getResource("/dsdtparser/resources/olarila.png")));
        this.jLabel11.setText("jLabel11");
        this.jLabel11.setCursor(new Cursor(0));
        this.jLabel11.setName("jLabel11");
        this.jLabel11.addMouseListener(new MouseAdapter(){

            public void mouseClicked(MouseEvent evt) {
                AutoPatcherAbout.this.jLabel11MouseClicked(evt);
            }
        });
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel1, -1, 447, 32767).addGroup(layout.createSequentialGroup().addComponent(this.jLabel4).addGap(4, 4, 4).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel3).addComponent(this.jLabel2)).addGap(59, 59, 59).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel7).addComponent(this.jLabel5).addComponent(this.jLabel6))).addGroup(layout.createSequentialGroup().addComponent(this.jLabel11, -2, 286, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 82, 32767).addComponent(this.jButton1))).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jLabel1, -2, 40, -2).addGap(18, 18, 18).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(60, 60, 60).addComponent(this.jLabel4)).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel3).addComponent(this.jLabel7)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel2).addComponent(this.jLabel5)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel6))).addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addGroup(layout.createSequentialGroup().addGap(18, 18, 18).addComponent(this.jLabel11).addContainerGap(43, 32767)).addGroup(layout.createSequentialGroup().addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jButton1).addContainerGap()))));
        this.pack();
    }

    private void jButton1ActionPerformed(ActionEvent evt) {
    }

    private void jLabel11MouseClicked(MouseEvent evt) {
        try {
            Desktop.getDesktop().browse(new URI("http://olarila.com/"));
        }
        catch (Exception ex) {
            // empty catch block
        }
    }

    @Action
    public void close() {
        this.setVisible(false);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable(){

            public void run() {
                new AutoPatcherAbout().setVisible(true);
            }
        });
    }

}

