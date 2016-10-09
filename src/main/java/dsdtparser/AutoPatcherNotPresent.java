/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 *  org.jdesktop.application.Action
 *  org.jdesktop.application.Application
 *  org.jdesktop.application.ApplicationActionMap
 *  org.jdesktop.application.ApplicationContext
 *  org.jdesktop.application.ResourceMap
 *  org.jdesktop.layout.GroupLayout
 *  org.jdesktop.layout.GroupLayout$Group
 *  org.jdesktop.layout.GroupLayout$ParallelGroup
 *  org.jdesktop.layout.GroupLayout$SequentialGroup
 */
package dsdtparser;

import comm.HttpUpload;
import dsdtparser.parser.DSDTExtractor;
import org.apache.commons.codec.binary.Base64;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.layout.GroupLayout;
import parser.IORegParser;
import parser.LsPciParser;
import parser.RegParser;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AutoPatcherNotPresent
extends JFrame {
    private JButton jButton1;
    private JButton jButton2;
    private JCheckBox jCheckBox1;
    private JLabel jLabel1;
    private JScrollPane jScrollPane1;
    private JTextArea jTextArea1;
    private JTextField jTextField1;

    public AutoPatcherNotPresent() {
        this.initComponents();
        this.jCheckBox1.addChangeListener(new ChangeListener(){

            public void stateChanged(ChangeEvent ce) {
                if (AutoPatcherNotPresent.this.jCheckBox1.isSelected()) {
                    AutoPatcherNotPresent.this.jButton2.setEnabled(true);
                } else {
                    AutoPatcherNotPresent.this.jButton2.setEnabled(false);
                }
            }
        });
        this.appendLine("Hardware info:");
        String s = System.getProperty("os.name").toLowerCase();
        if (s.indexOf("windows") != -1) {
            RegParser reg = new RegParser(this);
            Thread t = new Thread(reg);
            t.start();
        } else if (s.indexOf("mac") != -1) {
            IORegParser reg = new IORegParser(this);
            Thread t = new Thread(reg);
            t.start();
        } else if (s.indexOf("linux") != -1) {
            LsPciParser reg = new LsPciParser(this);
            Thread t = new Thread(reg);
            t.start();
        }
    }

    @Action
    public void closeWindow() {
        this.dispose();
    }

    public void appendLine(String line) {
        this.jTextArea1.setText(this.jTextArea1.getText() + line + "\n");
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        int offset;
        FileInputStream is = new FileInputStream(file);
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // empty if block
        }
        byte[] bytes = new byte[(int)length];
        int numRead = 0;
        for (offset = 0; offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0; offset += numRead) {
        }
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }

    public void loadFinished() {
        try {
            this.appendLine("DSDT binary:");
            File deleta = new File("dsdt.aml");
            if (deleta.exists()) {
                deleta.delete();
            }
            DSDTExtractor extractor = new DSDTExtractor(null);
            Thread tr = new Thread(extractor);
            tr.run();
            byte[] buffer = AutoPatcherNotPresent.getBytesFromFile(new File("dsdt.aml"));
            byte[] encoded = Base64.encodeBase64((byte[])buffer);
            this.appendLine(new String(encoded));
        }
        catch (FileNotFoundException e) {
            System.err.println(e);
        }
        catch (IOException e) {
            System.err.println(e);
        }
        this.jCheckBox1.setEnabled(true);
    }

    private void initComponents() {
        this.jLabel1 = new JLabel();
        this.jTextField1 = new JTextField();
        this.jScrollPane1 = new JScrollPane();
        this.jTextArea1 = new JTextArea();
        this.jCheckBox1 = new JCheckBox();
        this.jButton1 = new JButton();
        this.jButton2 = new JButton();
        this.setDefaultCloseOperation(2);
        this.setTitle("Not present?");
        this.setName("Form");
        this.setResizable(false);
        this.jLabel1.setFont(new Font("Monaco", 0, 10));
        this.jLabel1.setText("<html>If your MOBO or computer model isn't listed you have the option to submit the required data for us to create a patch file. Make sure you have an original DSDT before posting it. Write below the vendor/model of your mobo/computer:");
        this.jLabel1.setName("jLabel1");
        this.jTextField1.setName("jTextField1");
        this.jScrollPane1.setName("jScrollPane1");
        this.jTextArea1.setColumns(20);
        this.jTextArea1.setEditable(false);
        this.jTextArea1.setFont(new Font("Monaco", 0, 10));
        this.jTextArea1.setLineWrap(true);
        this.jTextArea1.setRows(5);
        this.jTextArea1.setEnabled(false);
        this.jTextArea1.setName("jTextArea1");
        this.jScrollPane1.setViewportView(this.jTextArea1);
        this.jCheckBox1.setText("<html>I authorize the application to send the preceding<br>data to olarila.com server.");
        this.jCheckBox1.setEnabled(false);
        this.jCheckBox1.setName("jCheckBox1");
        ApplicationActionMap actionMap = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getActionMap((Class)AutoPatcherNotPresent.class, (Object)this);
        this.jButton1.setAction(actionMap.get("closeWindow"));
        ResourceMap resourceMap = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getResourceMap((Class)AutoPatcherNotPresent.class);
        this.jButton1.setText(resourceMap.getString("close.text", new Object[0]));
        this.jButton1.setName("jButton1");
        this.jButton2.setAction(actionMap.get("sendData"));
        this.jButton2.setText(resourceMap.getString("send.text", new Object[0]));
        this.jButton2.setName("jButton2");
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout((LayoutManager)layout);
        layout.setHorizontalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jLabel1, -1, 338, 32767).add((Component)this.jScrollPane1, -1, 338, 32767).add((Component)this.jTextField1, -1, 338, 32767).add((Component)this.jCheckBox1, -2, -1, -2).add(2, (GroupLayout.Group)layout.createSequentialGroup().add((Component)this.jButton2).addPreferredGap(1).add((Component)this.jButton1))).addContainerGap()));
        layout.setVerticalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((Component)this.jLabel1, -2, -1, -2).addPreferredGap(0).add((Component)this.jTextField1, -2, -1, -2).addPreferredGap(0).add((Component)this.jScrollPane1, -2, 140, -2).addPreferredGap(1).add((Component)this.jCheckBox1, -2, -1, -2).add(18, 18, 18).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jButton1).add((Component)this.jButton2)).addContainerGap(74, 32767)));
        this.pack();
    }

    public void disableSendButton() {
        this.jButton2.setEnabled(false);
    }

    @Action
    public void sendData() {
        HttpUpload upload = new HttpUpload(this, this.jTextArea1.getText() + "\nUser data:\n" + this.jTextField1.getText());
        Thread t = new Thread(upload);
        t.start();
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable(){

            public void run() {
                new AutoPatcherNotPresent().setVisible(true);
            }
        });
    }

}

