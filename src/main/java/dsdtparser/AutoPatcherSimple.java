/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.jdesktop.application.Action
 *  org.jdesktop.application.Application
 *  org.jdesktop.application.ApplicationActionMap
 *  org.jdesktop.application.ApplicationContext
 *  org.jdesktop.layout.GroupLayout
 *  org.jdesktop.layout.GroupLayout$Group
 *  org.jdesktop.layout.GroupLayout$ParallelGroup
 *  org.jdesktop.layout.GroupLayout$SequentialGroup
 */
package dsdtparser;

import dsdtparser.fixes.AutoFix;
import dsdtparser.parser.*;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.layout.GroupLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AutoPatcherSimple
extends JFrame {
    private static boolean VERBOSE = true;
    private String dsdtfile = "";
    private String patch = "";
    private JButton jButton1;
    private JButton jButton2;
    private JButton jButton3;
    private JButton jButton4;
    private JButton jButton5;
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JScrollPane jScrollPane1;
    private JTextArea jTextArea1;
    private JTextField jTextField1;
    private JTextField jTextField2;

    public AutoPatcherSimple() {
        this.initComponents();
    }

    public void startPatching() {
        this.jButton5.setEnabled(false);
        this.jButton1.setEnabled(false);
        this.jButton2.setEnabled(false);
    }

    public void endPatching() {
        this.jButton5.setEnabled(true);
        this.jButton1.setEnabled(true);
        this.jButton2.setEnabled(true);
    }

    @Action
    public void openAboutWindow() {
        AutoPatcherAbout about = new AutoPatcherAbout();
        about.setVisible(true);
    }

    private void updateUI() {
        if (this.dsdtfile.length() > 0 && this.patch.length() > 0) {
            this.jButton5.setEnabled(true);
        } else {
            this.jButton5.setEnabled(false);
        }
    }

    @Action
    public void askForFile() {
        File file = DSDTParserView.promptForFile(this, false, new File(System.getProperty("user.home")));
        if (file != null) {
            this.dsdtfile = file.getAbsolutePath();
            this.appendLog("DSDT selected: " + file.getAbsolutePath());
            this.jTextField1.setText(file.getAbsolutePath());
            this.jTextField1.setToolTipText(file.getAbsolutePath());
        } else {
            this.dsdtfile = "";
            this.jTextField1.setText("");
            this.jTextField1.setToolTipText("");
        }
        this.updateUI();
    }

    private String getDestino() {
        File file = DSDTParserView.promptForFile(this, true, new File(System.getProperty("user.home")));
        if (file != null) {
            return file.getAbsolutePath();
        }
        return "";
    }

    @Action
    public void askForScript() {
        File file = DSDTParserView.promptForFile(this, false, new File(System.getProperty("user.home")));
        if (file != null) {
            this.patch = file.getAbsolutePath();
            this.appendLog("Patch selected: " + file.getAbsolutePath());
            this.jTextField2.setText(file.getAbsolutePath());
            this.jTextField2.setToolTipText(file.getAbsolutePath());
        } else {
            this.patch = "";
            this.jTextField2.setText("");
            this.jTextField2.setToolTipText("");
        }
        this.updateUI();
    }

    private String loadPatchFile() {
        Object in = null;
        if (this.patch.length() > 0) {
            try {
                int nRead;
                String buffer = "";
                BufferedInputStream f = new BufferedInputStream(new FileInputStream(new File(this.patch)));
                byte[] barray = new byte[1024];
                while ((nRead = f.read(barray, 0, 1024)) != -1) {
                    buffer = buffer + new String(barray).substring(0, nRead);
                }
                return buffer;
            }
            catch (FileNotFoundException ex) {
                this.appendLog("Patch file not found.");
            }
            catch (IOException ex) {
                // empty catch block
            }
        }
        return "";
    }

    @Action
    public void exit() {
        System.exit(0);
    }

    @Action
    public void patch() {
        Thread nt = new Thread(new Runnable(){

            public void run() {
                File f;
                Thread t;
                AutoPatcherSimple.this.startPatching();
                boolean isDSL = true;
                boolean proceed = true;
                String script = AutoPatcherSimple.this.loadPatchFile();
                if (AutoPatcherSimple.this.dsdtfile.matches(".*\\.aml")) {
                    isDSL = false;
                    System.out.println("DSDT file is an AML, deleting dsdt.dsl if present...");
                    File deleta = new File("dsdt.dsl");
                    if (deleta.exists()) {
                        deleta.delete();
                    }
                    AutoPatcherSimple.this.appendLog("Decompiling DSDT...");
                    f = new File(AutoPatcherSimple.this.dsdtfile);
                    if (f.exists()) {
                        t = new Thread(new AMLDecompiler(null, f));
                        t.run();
                    } else {
                        System.out.println("AML file not found");
                        proceed = false;
                    }
                }
                DSDTParser parser = null;
                AutoPatcherSimple.this.appendLog("Parsing DSL code...");
                f = new File(isDSL ? AutoPatcherSimple.this.dsdtfile : "dsdt.dsl");
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
                    AutoPatcherSimple.this.appendLog("Applying patches...");
                    ArrayList<DSDTItem> nodes = ActionForm.generateItems(parser.getRoot());
                    System.out.println("Generated nodes: " + nodes.size());
                    AutoPatcherSimple.this.applyPatches(script, nodes);
                    AutoPatcherSimple.this.appendLog("Saving the new DSL file...");
                    AutoPatcherSimple.this.dslSave(parser);
                    boolean abort = false;
                    if (parser.getRoot() == null) {
                        abort = true;
                        AutoPatcherSimple.this.appendLog("Invalid DSL, aborting.");
                    } else {
                        AutoFix.setBuffer(parser.getRoot().printEntry());
                        AutoPatcherSimple.this.appendLog("Compiling and checking compiler errors...");
                        AMLCompiler comp = new AMLCompiler(null, 2);
                        comp.run();
                        AutoPatcherSimple.this.appendLog("First compile errors: " + comp.getErrorsOnly().size());
                        if (comp.getErrorsOnly().size() > 0) {
                            AutoPatcherSimple.this.appendLog("Fixing errors...");
                            AutoPatcherSimple.this.dslSave(AutoFix.getBuffer());
                            comp = new AMLCompiler(null, 2);
                            comp.run();
                            AutoPatcherSimple.this.appendLog("Second compile errors: " + comp.getErrorsOnly().size());
                            if (comp.getErrorsOnly().size() > 0) {
                                AutoPatcherSimple.this.appendLog("Compiling again...");
                                AutoPatcherSimple.this.dslSave(AutoFix.getBuffer());
                                comp = new AMLCompiler(null, 2);
                                comp.run();
                                AutoPatcherSimple.this.appendLog("Third compile errors: " + comp.getErrorsOnly().size());
                                if (comp.getErrorsOnly().size() > 0) {
                                    abort = true;
                                    AutoPatcherSimple.this.appendLog("Errors persisted all fixing tentatives, please post this log at olarila.com forums\n" + comp.getErrorsOnly());
                                    ArrayList<CompilerError> erros = comp.getErrorsOnly();
                                    for (int i = 0; i < erros.size(); ++i) {
                                        System.out.println(erros.get(i).toString());
                                    }
                                }
                            }
                        }
                    }
                    if (!abort) {
                        AutoPatcherSimple.this.appendLog("Saving AML to disk...");
                        String destino = AutoPatcherSimple.this.getDestino();
                        if (destino.length() > 0) {
                            AMLCompiler comp = new AMLCompiler(null, 3);
                            comp.setDestino(destino);
                            comp.run();
                            AutoPatcherSimple.this.appendLog("Finished, saved aml to " + destino);
                        }
                    } else {
                        AutoPatcherSimple.this.appendLog("Aborted, compilation failed");
                    }
                    AutoPatcherSimple.this.endPatching();
                } else {
                    AutoPatcherSimple.this.appendLog("Failed, something went wrong.");
                    AutoPatcherSimple.this.endPatching();
                }
            }
        });
        nt.start();
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
            if (VERBOSE) {
                System.out.print("# " + lines[i2] + "...........");
            }
            try {
                String[] tmp = ap.parse(lines[i2], true);
                if (!VERBOSE) continue;
                System.out.println("ok");
                continue;
            }
            catch (InvalidParameterException ex) {
                if (!VERBOSE) continue;
                System.out.println("failed, " + ex.getMessage());
            }
        }
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
        catch (NullPointerException e) {
            this.appendLog("Invalid DSL (no root node found).");
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
        catch (NullPointerException e) {
            this.appendLog("Invalid DSL (no root node found).");
        }
    }

    public void appendLog(String message) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        this.jTextArea1.setText(this.jTextArea1.getText() + sdf.format(d) + ": " + message + "\n");
        this.jTextArea1.setCaretPosition(this.jTextArea1.getText().length());
    }

    private void initComponents() {
        this.jLabel1 = new JLabel();
        this.jTextField1 = new JTextField();
        this.jButton1 = new JButton();
        this.jLabel2 = new JLabel();
        this.jTextField2 = new JTextField();
        this.jButton2 = new JButton();
        this.jButton3 = new JButton();
        this.jButton4 = new JButton();
        this.jButton5 = new JButton();
        this.jLabel3 = new JLabel();
        this.jScrollPane1 = new JScrollPane();
        this.jTextArea1 = new JTextArea();
        this.jLabel4 = new JLabel();
        this.setDefaultCloseOperation(3);
        this.setTitle("DSDT Auto-Patcher");
        this.setMinimumSize(new Dimension(500, 300));
        this.setName("Form");
        this.setResizable(false);
        this.jLabel1.setText("DSDT");
        this.jLabel1.setName("jLabel1");
        this.jTextField1.setEditable(false);
        this.jTextField1.setName("jTextField1");
        ApplicationActionMap actionMap = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getActionMap((Class)AutoPatcherSimple.class, (Object)this);
        this.jButton1.setAction(actionMap.get("askForFile"));
        this.jButton1.setText("Open");
        this.jButton1.setName("jButton1");
        this.jLabel2.setText("Patch");
        this.jLabel2.setName("jLabel2");
        this.jTextField2.setEditable(false);
        this.jTextField2.setName("jTextField2");
        this.jButton2.setAction(actionMap.get("askForScript"));
        this.jButton2.setText("Open");
        this.jButton2.setName("jButton2");
        this.jButton3.setAction(actionMap.get("openAboutWindow"));
        this.jButton3.setText("About");
        this.jButton3.setName("jButton3");
        this.jButton4.setAction(actionMap.get("exit"));
        this.jButton4.setText("Exit");
        this.jButton4.setName("jButton4");
        this.jButton5.setAction(actionMap.get("patch"));
        this.jButton5.setText("Apply");
        this.jButton5.setEnabled(false);
        this.jButton5.setName("jButton5");
        this.jLabel3.setIcon(new ImageIcon(this.getClass().getResource("/dsdtparser/resources/donate.png")));
        this.jLabel3.setCursor(new Cursor(12));
        this.jLabel3.setName("jLabel3");
        this.jLabel3.addMouseListener(new MouseAdapter(){

            public void mouseClicked(MouseEvent evt) {
                AutoPatcherSimple.this.jLabel3MouseClicked(evt);
            }
        });
        this.jScrollPane1.setName("jScrollPane1");
        this.jTextArea1.setColumns(20);
        this.jTextArea1.setEditable(false);
        this.jTextArea1.setFont(new Font("Monaco", 0, 10));
        this.jTextArea1.setRows(5);
        this.jTextArea1.setName("jTextArea1");
        this.jTextArea1.setPreferredSize(null);
        this.jScrollPane1.setViewportView(this.jTextArea1);
        this.jLabel4.setText("Log");
        this.jLabel4.setName("jLabel4");
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout((LayoutManager)layout);
        layout.setHorizontalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.jLabel1).addPreferredGap(0).add((Component)this.jTextField1, -1, 329, 32767)).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.jLabel2).addPreferredGap(0).add((Component)this.jTextField2, -1, 330, 32767))).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(1).add((Component)this.jButton2).add((Component)this.jButton1))).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.jButton3).addPreferredGap(0, 217, 32767).add((Component)this.jButton5).addPreferredGap(0).add((Component)this.jButton4)).add((GroupLayout.Group)layout.createSequentialGroup().add((GroupLayout.Group)layout.createParallelGroup(2).add((Component)this.jLabel3).add((Component)this.jLabel4)).add(18, 18, 18).add((Component)this.jScrollPane1, -1, 391, 32767))).addContainerGap()));
        layout.setVerticalGroup((GroupLayout.Group)layout.createParallelGroup(1).add((GroupLayout.Group)layout.createSequentialGroup().addContainerGap().add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel1).add((Component)this.jTextField1, -2, -1, -2).add((Component)this.jButton1)).addPreferredGap(0).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jLabel2).add((Component)this.jTextField2, -2, -1, -2).add((Component)this.jButton2)).add(18, 18, 18).add((GroupLayout.Group)layout.createParallelGroup(3).add((Component)this.jButton3).add((Component)this.jButton4).add((Component)this.jButton5)).add(18, 18, 18).add((GroupLayout.Group)layout.createParallelGroup(2).add((GroupLayout.Group)layout.createSequentialGroup().add((Component)this.jLabel4).addPreferredGap(0, 78, 32767).add((Component)this.jLabel3)).add((Component)this.jScrollPane1, -1, 126, 32767)).addContainerGap()));
        this.pack();
    }

    private void jLabel3MouseClicked(MouseEvent evt) {
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
                new AutoPatcherSimple().setVisible(true);
            }
        });
    }

}

