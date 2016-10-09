/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
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

import comm.HTTPListRequest;
import comm.HTTPPatchDownload;
import dsdtparser.fixes.AutoFix;
import dsdtparser.parser.*;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.layout.GroupLayout;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AutoPatcher2
extends JFrame {
    private boolean VERBOSE = false;
    private HTTPListRequest listas;
    private String dsdtfile = "dsdt.aml";
    private String[] files = new String[]{"/Extra/dsdt.aml", "/Extra/DSDT.aml", "/Extra/DSDT.AML", "/Extra/dsdt.AML", "/dsdt.aml", "/DSDT.aml", "/DSDT.AML", "/dsdt.AML"};
    private ButtonGroup buttonGroup1;
    private JButton jButton1;
    private JButton jButton2;
    private JButton jButton3;
    private JButton jButton4;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JList jList1;
    private JList jList2;
    private JRadioButton jRadioButton1;
    private JRadioButton jRadioButton2;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JScrollPane jScrollPane3;
    private JTextArea jTextArea1;

    public AutoPatcher2() {
        this.initComponents();
        this.listas = new HTTPListRequest(this);
        Thread t = new Thread(this.listas);
        t.start();
        this.jRadioButton2.addItemListener(new ItemListener(){

            public void itemStateChanged(ItemEvent ie) {
                AutoPatcher2.this.toggleButton();
            }
        });
        this.jRadioButton2.addMouseListener(new MouseListener(){

            public void mouseClicked(MouseEvent me) {
                AutoPatcher2.this.toggleButton();
            }

            public void mousePressed(MouseEvent me) {
            }

            public void mouseReleased(MouseEvent me) {
            }

            public void mouseEntered(MouseEvent me) {
            }

            public void mouseExited(MouseEvent me) {
            }
        });
        this.jRadioButton1.addItemListener(new ItemListener(){

            public void itemStateChanged(ItemEvent ie) {
                AutoPatcher2.this.toggleButton();
            }
        });
        this.jList1.addListSelectionListener(new ListSelectionListener(){

            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && AutoPatcher2.this.jList1.getSelectedIndex() != -1) {
                    AutoPatcher2.this.selectedVendor(AutoPatcher2.this.jList1.getSelectedIndex());
                }
            }
        });
        this.jList2.addListSelectionListener(new ListSelectionListener(){

            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    if (AutoPatcher2.this.jList2.getSelectedIndex() == -1) {
                        AutoPatcher2.this.jButton3.setEnabled(false);
                    } else {
                        AutoPatcher2.this.appendLog("Pack " + AutoPatcher2.this.listas.getVendor(AutoPatcher2.this.jList1.getSelectedIndex()) + " - " + AutoPatcher2.this.listas.getArrayList(AutoPatcher2.this.jList1.getSelectedIndex()).get(AutoPatcher2.this.jList2.getSelectedIndex()) + " selected.");
                        AutoPatcher2.this.jButton3.setEnabled(true);
                    }
                } else {
                    AutoPatcher2.this.jButton3.setEnabled(false);
                }
            }
        });
    }

    @Action
    public void openAboutWindow() {
        AutoPatcherAbout about = new AutoPatcherAbout();
        about.setVisible(true);
    }

    @Action
    public void openNotPresentWindow() {
        AutoPatcherNotPresent janela = new AutoPatcherNotPresent();
        janela.setVisible(true);
    }

    public void startPatching() {
        this.jButton3.setEnabled(false);
        this.jList1.setEnabled(false);
        this.jList2.setEnabled(false);
    }

    public void endPatching() {
        this.jButton3.setEnabled(true);
        this.jList1.setEnabled(true);
        this.jList2.setEnabled(true);
    }

    @Action
    public void patch(final String script) {
        Thread nt = new Thread(new Runnable(){

            public void run() {
                Thread t;
                File f;
                if (AutoPatcher2.this.jRadioButton1.isSelected()) {
                    File deleta = new File("dsdt.aml");
                    if (deleta.exists()) {
                        deleta.delete();
                    }
                    AutoPatcher2.this.appendLog("Extracting system DSDT...");
                    DSDTExtractor extractor = new DSDTExtractor(null);
                    Thread tr = new Thread(extractor);
                    tr.run();
                }
                System.out.println(AutoPatcher2.this.dsdtfile);
                boolean isDSL = true;
                boolean proceed = true;
                if (AutoPatcher2.this.dsdtfile.matches(".*\\.aml")) {
                    isDSL = false;
                    System.out.println("DSDT file is an AML, deleting dsdt.dsl if present...");
                    File deleta = new File("dsdt.dsl");
                    if (deleta.exists()) {
                        deleta.delete();
                    }
                    AutoPatcher2.this.appendLog("Decompiling DSDT...");
                    f = new File(AutoPatcher2.this.dsdtfile);
                    if (f.exists()) {
                        t = new Thread(new AMLDecompiler(null, f));
                        t.run();
                    } else {
                        System.out.println("AML file not found");
                        proceed = false;
                    }
                }
                DSDTParser parser = null;
                AutoPatcher2.this.appendLog("Parsing DSL code...");
                f = new File(isDSL ? AutoPatcher2.this.dsdtfile : "dsdt.dsl");
                if (f.exists() && proceed) {
                    System.out.println("Using DSL file: " + f.getAbsolutePath());
                    parser = new DSDTParser(f);
                    parser.setParent(null);
                    parser.setPane(null);
                    parser.setTree(null);
                    t = new Thread(parser);
                    t.run();
                } else {
                    System.out.println("DSL File not found");
                }
                if (proceed) {
                    AutoPatcher2.this.appendLog("Applying patches...");
                    ArrayList<DSDTItem> nodes = ActionForm.generateItems(parser.getRoot());
                    System.out.println("Generated nodes: " + nodes.size());
                    AutoPatcher2.this.applyPatches(script, nodes);
                    AutoPatcher2.this.appendLog("Saving the new DSL file...");
                    AutoPatcher2.this.dslSave(parser);
                    AutoFix.setBuffer(parser.getRoot().printEntry());
                    AutoPatcher2.this.appendLog("Compiling and checking compiler errors...");
                    AMLCompiler comp = new AMLCompiler(null, 2);
                    comp.run();
                    AutoPatcher2.this.appendLog("First compile errors: " + comp.getErrorsOnly().size());
                    boolean abort = false;
                    if (comp.getErrorsOnly().size() > 0) {
                        AutoPatcher2.this.appendLog("Fixing errors...");
                        AutoPatcher2.this.dslSave(AutoFix.getBuffer());
                        comp = new AMLCompiler(null, 2);
                        comp.run();
                        AutoPatcher2.this.appendLog("Second compile errors: " + comp.getErrorsOnly().size());
                        if (comp.getErrorsOnly().size() > 0) {
                            AutoPatcher2.this.appendLog("Compiling again...");
                            AutoPatcher2.this.dslSave(AutoFix.getBuffer());
                            comp = new AMLCompiler(null, 2);
                            comp.run();
                            AutoPatcher2.this.appendLog("Third compile errors: " + comp.getErrorsOnly().size());
                            if (comp.getErrorsOnly().size() > 0) {
                                abort = true;
                                String s = System.getProperty("os.name").toLowerCase();
                                if (s.indexOf("mac") != -1) {
                                    for (int i = 0; i < AutoPatcher2.this.files.length; ++i) {
                                        File filet = new File(AutoPatcher2.this.files[i]);
                                        if (!filet.exists()) continue;
                                        AutoPatcher2.this.appendLog("A " + filet.getName() + " was found on " + filet.getParent() + ". Auto-Patcher needs the original DSDT from " + "BIOS to work correctly, please remove any patched files and reboot " + "before running Auto-Patcher. If you can't boot without a patched DSDT, " + "try the Windows or Linux version.");
                                        return;
                                    }
                                } else {
                                    AutoPatcher2.this.appendLog("Errors persisted all fixing tentatives, please post this log at olarila.com forums\n" + comp.getErrorsOnly());
                                }
                                ArrayList<CompilerError> erros = comp.getErrorsOnly();
                                for (int i = 0; i < erros.size(); ++i) {
                                    System.out.println(erros.get(i).toString());
                                }
                            }
                        }
                    }
                    if (!abort) {
                        AutoPatcher2.this.appendLog("Saving AML to disk (dsdt.aml)...");
                        comp = new AMLCompiler(null, 1);
                        comp.run();
                        AutoPatcher2.this.appendLog("Finished, dsdt.aml is in your desktop or home");
                    } else {
                        AutoPatcher2.this.appendLog("Aborted, compilation failed");
                    }
                    AutoPatcher2.this.endPatching();
                } else {
                    AutoPatcher2.this.appendLog("Failed, something went wrong.");
                    AutoPatcher2.this.endPatching();
                }
            }
        });
        nt.start();
    }

    private void dslSave(String buffer) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(new File("dsdt.dsl")));
            out.write(buffer);
            out.close();
        }
        catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private void dslSave(DSDTParser parser) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(new File("dsdt.dsl")));
            out.write(parser.getRoot().printEntry());
            out.close();
        }
        catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private void applyPatches(String script, ArrayList<DSDTItem> parser) {
        String[] temp = script.replaceAll("\r", "").split("\n");
        script = "";
        for (int i = 0; i < temp.length; ++i) {
            if (temp[i].startsWith("#")) continue;
            script = script + temp[i] + " ";
        }
        String[] lines = script.split(";");
        ActionParser ap = new ActionParser(parser);
        for (int i2 = 0; i2 < lines.length; ++i2) {
            if (this.VERBOSE) {
                System.out.print("# " + lines[i2] + "...........");
            }
            try {
                String[] tmp = ap.parse(lines[i2], true);
                if (!this.VERBOSE) continue;
                System.out.println("ok");
                continue;
            }
            catch (InvalidParameterException ex) {
                if (!this.VERBOSE) continue;
                System.out.println("failed, " + ex.getMessage());
            }
        }
    }

    @Action
    public void patchClicked() {
        HTTPPatchDownload download = new HTTPPatchDownload(this, this.listas.getVendor(this.jList1.getSelectedIndex()), (String)this.listas.getArrayList(this.jList1.getSelectedIndex()).get(this.jList2.getSelectedIndex()));
        Thread t = new Thread(download);
        t.start();
    }

    @Action
    public void toggleButton() {
        if (this.jRadioButton1.isSelected()) {
            this.dsdtfile = "dsdt.aml";
            this.appendLog("'Extract from system' set.");
            this.jLabel3.setText("none");
            this.jLabel3.setToolTipText("");
        } else if (this.jRadioButton2.isSelected()) {
            File file = DSDTParserView.promptForFile(this, false, new File(System.getProperty("user.home")));
            if (file != null) {
                this.dsdtfile = file.getAbsolutePath();
                this.appendLog("DSDT selected: " + file.getAbsolutePath());
                this.jLabel3.setText(file.getAbsolutePath());
                this.jLabel3.setToolTipText(file.getAbsolutePath());
            } else {
                this.jRadioButton1.setSelected(true);
                this.dsdtfile = "dsdt.aml";
                this.jLabel3.setText("none");
                this.jLabel3.setToolTipText("");
            }
        }
    }

    public void enableNotPresentButton() {
        this.jButton1.setEnabled(true);
        this.jList1.setEnabled(true);
        this.jList2.setEnabled(true);
    }

    public void appendLog(String message) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        this.jTextArea1.setText(this.jTextArea1.getText() + sdf.format(d) + ": " + message + "\n");
        this.jTextArea1.setCaretPosition(this.jTextArea1.getText().length());
    }

    public void selectedVendor(int index) {
        ArrayList al = this.listas.getArrayList(index);
        if (al != null) {
            DefaultListModel model = new DefaultListModel();
            for (int i = 0; i < al.size(); ++i) {
                model.addElement(al.get(i));
            }
            this.jList2.setModel(model);
        }
    }

    public void checkForAML() {
        String s = System.getProperty("os.name").toLowerCase();
        if (s.indexOf("mac") != -1) {
            for (int i = 0; i < this.files.length; ++i) {
                File filet = new File(this.files[i]);
                if (!filet.exists()) continue;
                this.showMessage("Warning", "A " + filet.getName() + " was found on " + filet.getParent() + ". Auto-Patcher needs the original DSDT from\n" + "BIOS to work correctly, please remove any patched files and reboot\n" + "before running Auto-Patcher. If you can't boot without a patched DSDT,\n" + "try the Windows or Linux version.", 1);
                return;
            }
        }
    }

    public void showMessage(String title, String msg, int type) {
        JOptionPane.showMessageDialog(this, msg, title, type);
    }

    public void setVendorList(String[] lista) {
        DefaultListModel<String> model = new DefaultListModel<String>();
        for (int i = 0; i < lista.length; ++i) {
            model.addElement(lista[i]);
        }
        this.jList1.setModel(model);
    }

    private void initComponents() {
        this.buttonGroup1 = new ButtonGroup();
        this.jLabel1 = new JLabel();
        this.jScrollPane1 = new JScrollPane();
        this.jList1 = new JList();
        this.jScrollPane2 = new JScrollPane();
        this.jList2 = new JList();
        this.jButton1 = new JButton();
        this.jLabel2 = new JLabel();
        this.jRadioButton1 = new JRadioButton();
        this.jRadioButton2 = new JRadioButton();
        this.jLabel3 = new JLabel();
        this.jButton2 = new JButton();
        this.jButton3 = new JButton();
        this.jScrollPane3 = new JScrollPane();
        this.jTextArea1 = new JTextArea();
        this.jLabel4 = new JLabel();
        this.jButton4 = new JButton();
        this.jLabel5 = new JLabel();
        this.setDefaultCloseOperation(3);
        ResourceMap resourceMap = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getResourceMap((Class)AutoPatcher2.class);
        this.setTitle(resourceMap.getString("Form.title", new Object[0]));
        this.setName("Form");
        this.setResizable(false);
        this.jLabel1.setText(resourceMap.getString("jLabel1.text", new Object[0]));
        this.jLabel1.setName("jLabel1");
        this.jScrollPane1.setName("jScrollPane1");
        this.jList1.setFont(resourceMap.getFont("jList1.font"));
        this.jList1.setSelectionMode(0);
        this.jList1.setEnabled(false);
        this.jList1.setName("jList1");
        this.jScrollPane1.setViewportView(this.jList1);
        this.jScrollPane2.setName("jScrollPane2");
        this.jList2.setFont(resourceMap.getFont("jList2.font"));
        this.jList2.setSelectionMode(0);
        this.jList2.setEnabled(false);
        this.jList2.setName("jList2");
        this.jScrollPane2.setViewportView(this.jList2);
        ApplicationActionMap actionMap = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getActionMap((Class)AutoPatcher2.class, (Object)this);
        this.jButton1.setAction(actionMap.get("openNotPresentWindow"));
        this.jButton1.setText(resourceMap.getString("jButton1.text", new Object[0]));
        this.jButton1.setName("jButton1");
        this.jLabel2.setText(resourceMap.getString("jLabel2.text", new Object[0]));
        this.jLabel2.setName("jLabel2");
        this.buttonGroup1.add(this.jRadioButton1);
        this.jRadioButton1.setSelected(true);
        this.jRadioButton1.setText(resourceMap.getString("jRadioButton1.text", new Object[0]));
        this.jRadioButton1.setName("jRadioButton1");
        this.buttonGroup1.add(this.jRadioButton2);
        this.jRadioButton2.setText(resourceMap.getString("jRadioButton2.text", new Object[0]));
        this.jRadioButton2.setName("jRadioButton2");
        this.jLabel3.setText(resourceMap.getString("jLabel3.text", new Object[0]));
        this.jLabel3.setName("jLabel3");
        this.jButton2.setAction(actionMap.get("quit"));
        this.jButton2.setText(resourceMap.getString("jButton2.text", new Object[0]));
        this.jButton2.setName("jButton2");
        this.jButton3.setAction(actionMap.get("patchClicked"));
        this.jButton3.setText(resourceMap.getString("jButton3.text", new Object[0]));
        this.jButton3.setName("jButton3");
        this.jScrollPane3.setName("jScrollPane3");
        this.jTextArea1.setColumns(20);
        this.jTextArea1.setEditable(false);
        this.jTextArea1.setFont(resourceMap.getFont("jTextArea1.font"));
        this.jTextArea1.setLineWrap(true);
        this.jTextArea1.setRows(5);
        this.jTextArea1.setName("jTextArea1");
        this.jScrollPane3.setViewportView(this.jTextArea1);
        this.jLabel4.setIcon(resourceMap.getIcon("jLabel4.icon"));
        this.jLabel4.setText(resourceMap.getString("jLabel4.text", new Object[0]));
        this.jLabel4.setCursor(new Cursor(12));
        this.jLabel4.setName("jLabel4");
        this.jLabel4.addMouseListener(new MouseAdapter(){

            public void mouseClicked(MouseEvent evt) {
                AutoPatcher2.this.jLabel4MouseClicked(evt);
            }
        });
        this.jButton4.setAction(actionMap.get("openAboutWindow"));
        this.jButton4.setText(resourceMap.getString("jButton4.text", new Object[0]));
        this.jButton4.setName("jButton4");
        this.jLabel5.setText(resourceMap.getString("jLabel5.text", new Object[0]));
        this.jLabel5.setName("jLabel5");
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout((LayoutManager)layout);
        layout.setHorizontalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jLabel2).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(2).add((Component)this.jLabel5).add((Component)this.jLabel4)).addPreferredGap(1).add((Component)this.jScrollPane3, -2, 318, -2)).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.jRadioButton2).addPreferredGap(0).add((Component)this.jLabel3, -2, 264, -2)).add((Component)this.jRadioButton1)).addPreferredGap(0, 21, 32767)).add(2, (GroupLayout.Group)layout.createSequentialGroup().add((Component)this.jButton4).addPreferredGap(0, 152, 32767).add((Component)this.jButton3).addPreferredGap(1).add((Component)this.jButton2).add(18, 18, 18)).add((GroupLayout.Group)layout.createParallelGroup(1, false).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.jLabel1).addPreferredGap(0, -1, 32767).add((Component)this.jButton1)).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.jScrollPane1, -2, 148, -2).addPreferredGap(0).add((Component)this.jScrollPane2, -1, 227, 32767)))).addContainerGap()));
        layout.setVerticalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel1).add((Component)this.jButton1)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(2).add((Component)this.jScrollPane2, -1, 137, 32767).add((Component)this.jScrollPane1, -2, -1, -2)).add(16, 16, 16).add((Component)this.jLabel2).addPreferredGap(0).add((Component)this.jRadioButton1).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jRadioButton2).add((Component)this.jLabel3)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jButton4).add((Component)this.jButton3).add((Component)this.jButton2)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1, false).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.jLabel5).addPreferredGap(0, -1, 32767).add((Component)this.jLabel4)).add((Component)this.jScrollPane3, -2, -1, -2)).addContainerGap()));
        this.pack();
    }

    private void jLabel4MouseClicked(MouseEvent evt) {
        try {
            Desktop.getDesktop().browse(new URI("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=ZV4PV797JEEU6"));
        }
        catch (Exception ex) {
            // empty catch block
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable(){

            public void run() {
                new AutoPatcher2().setVisible(true);
            }
        });
    }

}

