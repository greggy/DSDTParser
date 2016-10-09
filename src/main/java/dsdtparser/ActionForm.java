/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  jsyntaxpane.DefaultSyntaxKit
 *  jsyntaxpane.SyntaxDocument
 *  org.jdesktop.application.Action
 *  org.jdesktop.application.Application
 *  org.jdesktop.application.ApplicationActionMap
 *  org.jdesktop.application.ApplicationContext
 *  org.jdesktop.application.ResourceMap
 */
package dsdtparser;

import dsdtparser.parser.ActionParser;
import dsdtparser.parser.DSDTItem;
import dsdtparser.parser.InvalidParameterException;
import jsyntaxpane.DefaultSyntaxKit;
import jsyntaxpane.SyntaxDocument;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ActionForm
extends JFrame {
    private ArrayList<DSDTItem> parser;
    private DSDTParserView parent;
    private JButton jButton1;
    private JButton jButton2;
    private JButton jButton3;
    private JEditorPane jEditorPane1;
    private JEditorPane jEditorPane2;
    private JEditorPane jEditorPane3;
    private JPanel jPanel1;
    private JProgressBar jProgressBar1;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JScrollPane jScrollPane3;
    private JSplitPane jSplitPane1;
    private JTabbedPane jTabbedPane1;

    public ActionForm(DSDTParserView parent, DSDTItem root, boolean recursive) {
        this.initComponents();
        this.setTitle("Patch");
        this.jProgressBar1.setVisible(false);
        this.jTabbedPane1.setSelectedIndex(1);
        this.parent = parent;
        if (recursive) {
            this.parser = ActionForm.generateItems(root);
        } else {
            this.parser = new ArrayList();
            this.parser.add(root);
        }
        DefaultSyntaxKit.initKit();
        this.jEditorPane1.setContentType("text/dsdt");
        this.jEditorPane1.setFont(new Font("Monaco", 0, 12));
        this.jEditorPane2.setContentType("text/dsdt");
        this.jEditorPane2.setFont(new Font("Monaco", 0, 12));
        this.jEditorPane1.setEditable(false);
        this.jEditorPane2.setEditable(false);
        this.jEditorPane3.setContentType("text/plain");
        this.jEditorPane3.setFont(new Font("Monaco", 0, 12));
        this.jEditorPane1.setBackground(parent.getBackgroundColor());
        this.jEditorPane2.setBackground(parent.getBackgroundColor());
        this.jEditorPane3.setBackground(parent.getBackgroundColor());
        this.jEditorPane1.setComponentPopupMenu(null);
        this.jEditorPane2.setComponentPopupMenu(null);
    }

    public static ArrayList<DSDTItem> generateItems(DSDTItem node) {
        ArrayList<DSDTItem> retorno = new ArrayList<DSDTItem>();
        ActionForm.parseNode(node, retorno);
        return retorno;
    }

    private static void parseNode(DSDTItem node, ArrayList<DSDTItem> retorno) {
        retorno.add(node);
        if (node != null) {
            for (int i = 0; i < node.getChildCount(); ++i) {
                ActionForm.parseNode(node.getChildAt(i), retorno);
            }
        }
    }

    private void initComponents() {
        this.jSplitPane1 = new JSplitPane();
        this.jPanel1 = new JPanel();
        this.jScrollPane3 = new JScrollPane();
        this.jEditorPane3 = new JEditorPane();
        this.jButton1 = new JButton();
        this.jButton2 = new JButton();
        this.jButton3 = new JButton();
        this.jProgressBar1 = new JProgressBar();
        this.jTabbedPane1 = new JTabbedPane();
        this.jScrollPane1 = new JScrollPane();
        this.jEditorPane1 = new JEditorPane();
        this.jScrollPane2 = new JScrollPane();
        this.jEditorPane2 = new JEditorPane();
        this.setName("Form");
        this.setSize(new Dimension(800, 600));
        this.jSplitPane1.setDividerLocation(230);
        this.jSplitPane1.setOrientation(0);
        this.jSplitPane1.setName("jSplitPane1");
        this.jPanel1.setName("jPanel1");
        this.jScrollPane3.setName("jScrollPane3");
        ResourceMap resourceMap = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getResourceMap((Class)ActionForm.class);
        this.jEditorPane3.setFont(resourceMap.getFont("jEditorPane3.font"));
        this.jEditorPane3.setName("jEditorPane3");
        this.jScrollPane3.setViewportView(this.jEditorPane3);
        ApplicationActionMap actionMap = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getActionMap((Class)ActionForm.class, (Object)this);
        this.jButton1.setAction(actionMap.get("fecharJanela"));
        this.jButton1.setText(resourceMap.getString("jButton1.text", new Object[0]));
        this.jButton1.setName("jButton1");
        this.jButton2.setAction(actionMap.get("applyPatch"));
        this.jButton2.setText(resourceMap.getString("jButton2.text", new Object[0]));
        this.jButton2.setName("jButton2");
        this.jButton3.setAction(actionMap.get("preViewPatch"));
        this.jButton3.setText(resourceMap.getString("jButton3.text", new Object[0]));
        this.jButton3.setName("jButton3");
        this.jProgressBar1.setName("jProgressBar1");
        GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
        this.jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jScrollPane3).addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addContainerGap().addComponent(this.jProgressBar1, -2, 115, -2).addGap(132, 132, 132).addComponent(this.jButton3).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jButton2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jButton1).addGap(6, 6, 6)));
        jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addComponent(this.jScrollPane3, -1, 185, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jButton1, -2, 29, -2).addComponent(this.jButton2).addComponent(this.jButton3)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(this.jProgressBar1, -2, -1, -2).addContainerGap()))));
        this.jSplitPane1.setTopComponent(this.jPanel1);
        this.jTabbedPane1.setName("jTabbedPane1");
        this.jScrollPane1.setName("jScrollPane1");
        this.jEditorPane1.setFont(resourceMap.getFont("jEditorPane1.font"));
        this.jEditorPane1.setName("jEditorPane1");
        this.jScrollPane1.setViewportView(this.jEditorPane1);
        this.jTabbedPane1.addTab(resourceMap.getString("jScrollPane1.TabConstraints.tabTitle", new Object[0]), this.jScrollPane1);
        this.jScrollPane2.setName("jScrollPane2");
        this.jEditorPane2.setFont(resourceMap.getFont("jEditorPane2.font"));
        this.jEditorPane2.setName("jEditorPane2");
        this.jScrollPane2.setViewportView(this.jEditorPane2);
        this.jTabbedPane1.addTab(resourceMap.getString("jScrollPane2.TabConstraints.tabTitle", new Object[0]), this.jScrollPane2);
        this.jSplitPane1.setRightComponent(this.jTabbedPane1);
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jSplitPane1));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jSplitPane1, -1, 453, 32767));
        this.pack();
    }

    @Action
    public void fecharJanela() {
        this.setVisible(false);
        this.dispose();
    }

    @Action
    public void preViewPatch() {
        Thread t = new Thread(new Runnable(){

            public void run() {
                ActionForm.this.disableButtons();
                ActionForm.this.startLoading();
                ActionForm.this.patch(false);
                ActionForm.this.stopLoading();
                ActionForm.this.enableButtons();
            }
        });
        t.start();
    }

    @Action
    public void applyPatch() {
        Thread t = new Thread(new Runnable(){

            public void run() {
                ActionForm.this.disableButtons();
                ActionForm.this.startLoading();
                ActionForm.this.patch(true);
                ActionForm.this.stopLoading();
                ActionForm.this.enableButtons();
                ActionForm.this.parent.fullUpdate();
            }
        });
        t.start();
    }

    private void disableButtons() {
        this.jButton1.setEnabled(false);
        this.jButton2.setEnabled(false);
        this.jButton3.setEnabled(false);
    }

    private void enableButtons() {
        this.jButton1.setEnabled(true);
        this.jButton2.setEnabled(true);
        this.jButton3.setEnabled(true);
    }

    public void setContent(File file) {
        try {
            this.jEditorPane3.read(new FileReader(file), file);
        }
        catch (IOException ex) {
            // empty catch block
        }
    }

    private void patch(boolean apply) {
        if (!apply) {
            this.jEditorPane1.setText("");
            this.jEditorPane2.setText("");
        }
        String script = this.jEditorPane3.getText().replaceAll("\r", "");
        String[] temp = script.split("\n");
        script = "";
        for (int i = 0; i < temp.length; ++i) {
            if (temp[i].startsWith("#")) continue;
            script = script + temp[i] + " ";
        }
        String buffer1 = "";
        String buffer2 = "";
        String[] lines = script.split(";");
        ActionParser ap = new ActionParser(this.parser);
        for (int i2 = 0; i2 < lines.length; ++i2) {
            block8 : {
                if (!apply) {
                    String[] biru = lines[i2].split("begin");
                    buffer1 = buffer1 + "// " + biru[0] + "\n\n";
                    buffer2 = buffer2 + "// " + biru[0] + "\n\n";
                }
                try {
                    String[] tmp = ap.parse(lines[i2], apply);
                    if (!apply) {
                        buffer1 = buffer1 + tmp[0];
                        buffer2 = buffer2 + tmp[1];
                    }
                }
                catch (InvalidParameterException ex) {
                    if (apply) break block8;
                    buffer1 = buffer1 + ex.getReason();
                    buffer2 = buffer2 + ex.getReason();
                }
            }
            if (apply) continue;
            buffer1 = buffer1 + "\n\n";
            buffer2 = buffer2 + "\n\n";
        }
        if (!apply) {
            this.jEditorPane1.setText(buffer1);
            this.jEditorPane2.setText(buffer2);
            this.jEditorPane1.setCaretPosition(0);
            this.jEditorPane2.setCaretPosition(0);
            ((SyntaxDocument)this.jEditorPane1.getDocument()).clearUndos();
            ((SyntaxDocument)this.jEditorPane2.getDocument()).clearUndos();
        }
    }

    public void startLoading() {
        this.jProgressBar1.setVisible(true);
        this.jProgressBar1.setIndeterminate(true);
    }

    public void stopLoading() {
        this.jProgressBar1.setVisible(false);
        this.jProgressBar1.setValue(0);
    }

}

