/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.jdesktop.application.Action
 *  org.jdesktop.application.Application
 *  org.jdesktop.application.ApplicationActionMap
 *  org.jdesktop.application.ApplicationContext
 *  org.jdesktop.application.ResourceMap
 */
package dsdtparser;

import dsdtparser.fixes.AutoFix;
import dsdtparser.parser.AMLCompiler;
import dsdtparser.parser.CompilerError;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LineErrorsWindow
extends JFrame {
    private static final boolean DEBUG = false;
    private DSDTParserView parent;
    private ArrayList<CompilerError> errors;
    private JButton jButton1;
    private JLabel jLabel1;
    private JScrollPane jScrollPane1;
    private JTable jTable1;

    public LineErrorsWindow(DSDTParserView parent, ArrayList<CompilerError> errors) {
        this.errors = errors;
        this.parent = parent;
        final DSDTParserView ref = parent;
        this.setTitle("Compile");
        this.initComponents();
        this.refresh();
        this.jTable1.setSelectionMode(0);
        ListSelectionModel rowSM = this.jTable1.getSelectionModel();
        rowSM.addListSelectionListener(new ListSelectionListener(){

            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                if (!lsm.isSelectionEmpty()) {
                    int selectedRow = lsm.getMinSelectionIndex();
                    Integer val = (Integer)LineErrorsWindow.this.jTable1.getModel().getValueAt(selectedRow, 0);
                    if (val != null) {
                        ref.setCaretAtLine(val);
                    }
                }
            }
        });
    }

    public void refresh() {
        this.jTable1.setModel(new DefaultTableModel(new Object[0][], new String[]{"Line", "Type", "Message"}){
            Class[] types;
            boolean[] canEdit;

            public Class getColumnClass(int columnIndex) {
                return this.types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return this.canEdit[columnIndex];
            }
        });
        this.jTable1.getColumnModel().getColumn(0).setMinWidth(40);
        this.jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
        this.jTable1.getColumnModel().getColumn(0).setMaxWidth(60);
        this.jTable1.getColumnModel().getColumn(1).setMinWidth(60);
        this.jTable1.getColumnModel().getColumn(1).setPreferredWidth(70);
        this.jTable1.getColumnModel().getColumn(1).setMaxWidth(100);
        this.jTable1.getColumnModel().getColumn(2).setPreferredWidth(50);
        for (int i = 0; i < this.errors.size(); ++i) {
            CompilerError erro = this.errors.get(i);
            ((DefaultTableModel)this.jTable1.getModel()).insertRow(i, erro.getTableLine());
        }
    }

    public void setLabelText(String text) {
        this.jLabel1.setText(text);
    }

    @Action
    public void autoFix() {
        LineErrorsWindow ref = this;
        Thread t = new Thread(new Runnable(){

            public void run() {
                LineErrorsWindow.this.parent.startLoading();
                DefaultTableModel dtm = (DefaultTableModel)LineErrorsWindow.this.jTable1.getModel();
                int count = 0;
                LineErrorsWindow.this.parent.loadLineBuffer();
                LineErrorsWindow.this.parent.loadBuffer();
                for (int i = dtm.getRowCount() - 1; i >= 0; --i) {
                    String error = (String)dtm.getValueAt(i, 2);
                    int line = (Integer)dtm.getValueAt(i, 0);
                    if (!AutoFix.isKnownError(error)) continue;
                    ++count;
                    AutoFix.fix(LineErrorsWindow.this.parent, error, line);
                }
                LineErrorsWindow.this.parent.commitBuffer();
                LineErrorsWindow.this.parent.saveDsdtDsl();
                AMLCompiler comp = new AMLCompiler(LineErrorsWindow.this.parent, 0);
                comp.compile();
                LineErrorsWindow.this.errors = comp.getErrors();
                LineErrorsWindow.this.refresh();
                LineErrorsWindow.this.setLabelText(comp.getLabel());
            }
        });
        t.start();
    }

    private void initComponents() {
        this.jScrollPane1 = new JScrollPane();
        this.jTable1 = new JTable();
        this.jLabel1 = new JLabel();
        this.jButton1 = new JButton();
        this.setName("Form");
        this.jScrollPane1.setName("jScrollPane1");
        this.jTable1.setModel(new DefaultTableModel(new Object[0][], new String[]{"Line", "Type", "Message"}){
            Class[] types;
            boolean[] canEdit;

            public Class getColumnClass(int columnIndex) {
                return this.types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return this.canEdit[columnIndex];
            }
        });
        this.jTable1.setName("jTable1");
        this.jTable1.getTableHeader().setReorderingAllowed(false);
        this.jScrollPane1.setViewportView(this.jTable1);
        ResourceMap resourceMap = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getResourceMap((Class)LineErrorsWindow.class);
        this.jTable1.getColumnModel().getColumn(0).setMinWidth(40);
        this.jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
        this.jTable1.getColumnModel().getColumn(0).setMaxWidth(60);
        this.jTable1.getColumnModel().getColumn(1).setMinWidth(60);
        this.jTable1.getColumnModel().getColumn(1).setPreferredWidth(70);
        this.jTable1.getColumnModel().getColumn(1).setMaxWidth(100);
        this.jTable1.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("jTable1.columnModel.title2", new Object[0]));
        this.jTable1.getColumnModel().getColumn(2).setPreferredWidth(50);
        this.jTable1.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("jTable1.columnModel.title1", new Object[0]));
        this.jLabel1.setText("0 Errors, 0 Warnings, 1 Remarks, 41 Optimizations");
        this.jLabel1.setName("jLabel1");
        ApplicationActionMap actionMap = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getActionMap((Class)LineErrorsWindow.class, (Object)this);
        this.jButton1.setAction(actionMap.get("autoFix"));
        this.jButton1.setText("Fix errors");
        this.jButton1.setToolTipText("Try to fix common errors (will apply patches and recompile).");
        this.jButton1.setName("jButton1");
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGap(12, 12, 12).addComponent(this.jLabel1).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 112, 32767).addComponent(this.jButton1)).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(this.jScrollPane1, -1, 540, 32767))).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addComponent(this.jScrollPane1, -1, 184, 32767).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jLabel1).addComponent(this.jButton1)).addContainerGap()));
        this.pack();
    }

}

