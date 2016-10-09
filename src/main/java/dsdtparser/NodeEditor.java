/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  jsyntaxpane.DefaultSyntaxKit
 *  org.jdesktop.application.Action
 *  org.jdesktop.application.Application
 *  org.jdesktop.application.ApplicationActionMap
 *  org.jdesktop.application.ApplicationContext
 *  org.jdesktop.application.ResourceMap
 */
package dsdtparser;

import dsdtparser.parser.DSDTItem;
import jsyntaxpane.DefaultSyntaxKit;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;

public class NodeEditor
extends JFrame {
    private DSDTParserView parent;
    private DSDTItem node;
    private JButton jButton1;
    private JButton jButton2;
    private JEditorPane jEditorPane1;
    private JScrollPane jScrollPane1;

    public NodeEditor(DSDTParserView parent, DSDTItem node) {
        this.parent = parent;
        this.node = node;
        this.initComponents();
        this.setTitle(node.getHeader());
        DefaultSyntaxKit.initKit();
        this.jEditorPane1.setContentType("text/dsdt");
        this.jEditorPane1.setText(node.printEditableEntry());
        this.jEditorPane1.setCaretPosition(0);
        this.jEditorPane1.setBackground(parent.getBackgroundColor());
    }

    @Action
    public void closeWindow() {
        this.setVisible(false);
        this.dispose();
    }

    @Action
    public void save() {
        this.node.setEditableString(this.jEditorPane1.getText());
        this.closeWindow();
        this.parent.fullUpdate();
    }

    private void initComponents() {
        this.jScrollPane1 = new JScrollPane();
        this.jEditorPane1 = new JEditorPane();
        this.jButton1 = new JButton();
        this.jButton2 = new JButton();
        this.setDefaultCloseOperation(2);
        this.setName("Form");
        this.jScrollPane1.setName("jScrollPane1");
        this.jEditorPane1.setName("jEditorPane1");
        this.jScrollPane1.setViewportView(this.jEditorPane1);
        ApplicationActionMap actionMap = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getActionMap((Class)NodeEditor.class, (Object)this);
        this.jButton1.setAction(actionMap.get("closeWindow"));
        ResourceMap resourceMap = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getResourceMap((Class)NodeEditor.class);
        this.jButton1.setText(resourceMap.getString("jButton1.text", new Object[0]));
        this.jButton1.setName("jButton1");
        this.jButton2.setAction(actionMap.get("save"));
        this.jButton2.setText(resourceMap.getString("jButton2.text", new Object[0]));
        this.jButton2.setName("jButton2");
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap(466, 32767).addComponent(this.jButton2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jButton1).addContainerGap()).addComponent(this.jScrollPane1, GroupLayout.Alignment.TRAILING));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addComponent(this.jScrollPane1, -1, 297, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jButton1).addComponent(this.jButton2)).addContainerGap()));
        this.pack();
    }
}

