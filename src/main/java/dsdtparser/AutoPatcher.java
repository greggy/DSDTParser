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

import dsdtparser.fixes.AutoFix;
import dsdtparser.parser.*;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AutoPatcher
extends JFrame {
    private final boolean VERBOSE = true;
    private final String PATCHES = "packs";
    private String patchfile;
    private String dsdtfile;
    private JButton jButton1;
    private JButton jButton10;
    private JButton jButton11;
    private JButton jButton2;
    private JButton jButton3;
    private JButton jButton4;
    private JButton jButton7;
    private JButton jButton9;
    private JCheckBox jCheckBox1;
    private JComboBox jComboBox1;
    private JComboBox jComboBox2;
    private JLabel jLabel1;
    private JLabel jLabel3;
    private JProgressBar jProgressBar1;
    private JScrollPane jScrollPane1;
    private JTextArea jTextArea1;

    public AutoPatcher() {
        this.initComponents();
        this.jLabel3.setVisible(false);
        this.dsdtfile = "dsdt.aml";
        this.load1stCombo("packs");
        this.jCheckBox1.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent arg0) {
                AutoPatcher.this.toggleButton();
            }
        });
        this.jComboBox1.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent arg0) {
                AutoPatcher.this.load2ndCombo((String)AutoPatcher.this.jComboBox1.getSelectedItem());
            }
        });
        this.jComboBox2.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent arg0) {
                AutoPatcher.this.jTextArea1.setText(AutoPatcher.this.loadDescFile("packs" + System.getProperty("file.separator") + (String)AutoPatcher.this.jComboBox1.getSelectedItem() + System.getProperty("file.separator") + (String)AutoPatcher.this.jComboBox2.getSelectedItem()));
                AutoPatcher.this.jTextArea1.setCaretPosition(0);
                AutoPatcher.this.patchfile = "packs" + System.getProperty("file.separator") + (String)AutoPatcher.this.jComboBox1.getSelectedItem() + System.getProperty("file.separator") + (String)AutoPatcher.this.jComboBox2.getSelectedItem() + System.getProperty("file.separator") + "patch.txt";
                System.out.println(AutoPatcher.this.patchfile);
                AutoPatcher.this.jButton1.setEnabled(true);
            }
        });
        this.jTextArea1.setText("Select a pack above.");
        this.jTextArea1.setCaretPosition(0);
    }

    @Action
    public void toggleButton() {
        if (this.jCheckBox1.isSelected()) {
            this.dsdtfile = "dsdt.aml";
            this.jLabel3.setText("From system.");
            this.jLabel3.setVisible(false);
        } else {
            File file = DSDTParserView.promptForFile(this, false, new File(System.getProperty("user.home")));
            if (file != null) {
                this.dsdtfile = file.getAbsolutePath();
                this.jLabel3.setVisible(true);
                this.jLabel3.setText(file.getAbsolutePath());
            } else {
                this.jCheckBox1.setSelected(true);
                this.dsdtfile = "dsdt.aml";
                this.jLabel3.setText("From system.");
                this.jLabel3.setVisible(false);
            }
        }
    }

    private void load1stCombo(String directory) {
        File dir = new File(directory);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Missing directory: " + directory);
            System.exit(0);
        }
        File[] files = dir.listFiles();
        DefaultComboBoxModel<String> dcm = new DefaultComboBoxModel<String>();
        for (int i = 0; i < files.length; ++i) {
            if (files[i].getName().startsWith(".")) continue;
            dcm.addElement(files[i].getName());
        }
        this.jComboBox1.setModel(dcm);
        this.jComboBox1.setSelectedIndex(-1);
        this.jComboBox1.setEnabled(true);
    }

    private void load2ndCombo(String directory) {
        this.jComboBox2.setEnabled(false);
        File dir = new File("packs" + System.getProperty("file.separator") + directory);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Missing directory: " + directory);
            System.exit(0);
        }
        File[] files = dir.listFiles();
        DefaultComboBoxModel<String> dcm = new DefaultComboBoxModel<String>();
        for (int i = 0; i < files.length; ++i) {
            if (files[i].getName().startsWith(".")) continue;
            dcm.addElement(files[i].getName());
        }
        this.jComboBox2.setModel(dcm);
        this.jComboBox2.setSelectedIndex(0);
        this.jComboBox2.setEnabled(true);
    }

    @Action
    public void patch() {
        Thread nt = new Thread(new Runnable(){

            public void run() {
                Thread t;
                File f;
                AutoPatcher.this.jButton1.setEnabled(false);
                AutoPatcher.this.jProgressBar1.setIndeterminate(true);
                if (AutoPatcher.this.jCheckBox1.isSelected()) {
                    File deleta = new File("dsdt.aml");
                    if (deleta.exists()) {
                        deleta.delete();
                    }
                    AutoPatcher.this.jLabel1.setText("Status: Extracting system DSDT...");
                    DSDTExtractor extractor = new DSDTExtractor(null);
                    Thread tr = new Thread(extractor);
                    tr.run();
                }
                System.out.println(AutoPatcher.this.dsdtfile);
                boolean isDSL = true;
                boolean proceed = true;
                if (AutoPatcher.this.dsdtfile.matches(".*\\.aml")) {
                    isDSL = false;
                    System.out.println("DSDT file is an AML, deleting dsdt.dsl if present...");
                    File deleta = new File("dsdt.dsl");
                    if (deleta.exists()) {
                        deleta.delete();
                    }
                    AutoPatcher.this.jLabel1.setText("Status: Decompiling DSDT...");
                    f = new File(AutoPatcher.this.dsdtfile);
                    if (f.exists()) {
                        t = new Thread(new AMLDecompiler(null, f));
                        t.run();
                    } else {
                        System.out.println("AML file not found");
                        proceed = false;
                    }
                }
                DSDTParser parser = null;
                AutoPatcher.this.jLabel1.setText("Status: Parsing DSL code...");
                f = new File(isDSL ? AutoPatcher.this.dsdtfile : "dsdt.dsl");
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
                    AutoPatcher.this.jLabel1.setText("Status: Loading patch file...");
                    String script = AutoPatcher.this.loadPatchFile();
                    AutoPatcher.this.jLabel1.setText("Status: Applying patches...");
                    ArrayList<DSDTItem> nodes = ActionForm.generateItems(parser.getRoot());
                    System.out.println("Generated nodes: " + nodes.size());
                    AutoPatcher.this.applyPatches(script, nodes);
                    AutoPatcher.this.jLabel1.setText("Status: Saving the new DSL file...");
                    AutoPatcher.this.dslSave(parser);
                    AutoFix.setBuffer(parser.getRoot().printEntry());
                    AutoPatcher.this.jLabel1.setText("Status: Compiling and checking compiler errors...");
                    AMLCompiler comp = new AMLCompiler(null, 2);
                    comp.run();
                    System.out.println("First compile errors: " + comp.getErrorsOnly().size());
                    boolean abort = false;
                    if (comp.getErrorsOnly().size() > 0) {
                        AutoPatcher.this.jLabel1.setText("Status: Fixing errors...");
                        AutoPatcher.this.dslSave(AutoFix.getBuffer());
                        comp = new AMLCompiler(null, 2);
                        comp.run();
                        System.out.println("Second compile errors: " + comp.getErrorsOnly().size());
                        if (comp.getErrorsOnly().size() > 0) {
                            AutoPatcher.this.jLabel1.setText("Status: Compiling again...");
                            AutoPatcher.this.dslSave(AutoFix.getBuffer());
                            comp = new AMLCompiler(null, 2);
                            comp.run();
                            System.out.println("Third compile errors: " + comp.getErrorsOnly().size());
                            if (comp.getErrorsOnly().size() > 0) {
                                abort = true;
                                AutoPatcherCompilerError report = new AutoPatcherCompilerError();
                                report.setMessage(comp.getErrorsOnly());
                                report.setVisible(true);
                                ArrayList<CompilerError> erros = comp.getErrorsOnly();
                                for (int i = 0; i < erros.size(); ++i) {
                                    System.out.println(erros.get(i).toString());
                                }
                            }
                        }
                    }
                    if (!abort) {
                        AutoPatcher.this.jLabel1.setText("Status: Saving AML to disk (dsdt.aml)...");
                        comp = new AMLCompiler(null, 1);
                        comp.run();
                        AutoPatcher.this.jLabel1.setText("Status: Finished, dsdt.aml is in your desktop or home");
                    } else {
                        AutoPatcher.this.jLabel1.setText("Status: Aborted, compilation failed");
                    }
                    AutoPatcher.this.jProgressBar1.setIndeterminate(false);
                    AutoPatcher.this.jButton1.setEnabled(true);
                } else {
                    AutoPatcher.this.jLabel1.setText("Status: Failed, something went wrong.");
                    AutoPatcher.this.jProgressBar1.setIndeterminate(false);
                    AutoPatcher.this.jButton1.setEnabled(true);
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

    private String loadDescFile(String directory) {
        Object in = null;
        try {
            int nRead;
            String buffer = "";
            BufferedInputStream f = new BufferedInputStream(new FileInputStream(new File(directory + System.getProperty("file.separator") + "description.txt")));
            byte[] barray = new byte[1024];
            while ((nRead = f.read(barray, 0, 1024)) != -1) {
                buffer = buffer + new String(barray).substring(0, nRead);
            }
            return buffer.replace('\r', '\n');
        }
        catch (FileNotFoundException ex) {
            System.out.println("Description file not found.");
        }
        catch (IOException ex) {
            // empty catch block
        }
        return "";
    }

    private String loadPatchFile() {
        Object in = null;
        try {
            int nRead;
            String buffer = "";
            BufferedInputStream f = new BufferedInputStream(new FileInputStream(new File(this.patchfile)));
            byte[] barray = new byte[1024];
            while ((nRead = f.read(barray, 0, 1024)) != -1) {
                buffer = buffer + new String(barray).substring(0, nRead);
            }
            return buffer;
        }
        catch (FileNotFoundException ex) {
            System.out.println("Patch file not found.");
        }
        catch (IOException ex) {
            // empty catch block
        }
        return "";
    }

    @Action
    public void openAbout() {
        AutoPatcherAbout about = new AutoPatcherAbout();
        about.setVisible(true);
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
            System.out.print("# " + lines[i2] + "...........");
            try {
                String[] tmp = ap.parse(lines[i2], true);
                System.out.println("ok");
                continue;
            }
            catch (InvalidParameterException ex) {
                System.out.println("failed, " + ex.getMessage());
            }
        }
    }

    private void initComponents() {
        this.jProgressBar1 = new JProgressBar();
        this.jButton1 = new JButton();
        this.jLabel1 = new JLabel();
        this.jScrollPane1 = new JScrollPane();
        this.jTextArea1 = new JTextArea();
        this.jComboBox1 = new JComboBox();
        this.jComboBox2 = new JComboBox();
        this.jLabel3 = new JLabel();
        this.jCheckBox1 = new JCheckBox();
        this.jButton2 = new JButton();
        this.jButton3 = new JButton();
        this.jButton4 = new JButton();
        this.jButton7 = new JButton();
        this.jButton9 = new JButton();
        this.jButton10 = new JButton();
        this.jButton11 = new JButton();
        this.setDefaultCloseOperation(3);
        this.setTitle("Auto-Patcher");
        this.setMinimumSize(new Dimension(435, 255));
        this.setName("Form");
        this.jProgressBar1.setName("jProgressBar1");
        ApplicationActionMap actionMap = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getActionMap((Class)AutoPatcher.class, (Object)this);
        this.jButton1.setAction(actionMap.get("patch"));
        this.jButton1.setIcon(new ImageIcon(this.getClass().getResource("/dsdtparser/resources/play.png")));
        this.jButton1.setText("");
        this.jButton1.setName("jButton1");
        this.jButton1.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent evt) {
                AutoPatcher.this.jButton1ActionPerformed(evt);
            }
        });
        this.jLabel1.setText("Status: Waiting...");
        this.jLabel1.setName("jLabel1");
        this.jScrollPane1.setName("jScrollPane1");
        this.jTextArea1.setColumns(20);
        this.jTextArea1.setEditable(false);
        this.jTextArea1.setRows(5);
        this.jTextArea1.setName("jTextArea1");
        this.jScrollPane1.setViewportView(this.jTextArea1);
        this.jComboBox1.setModel(new DefaultComboBoxModel<String>(new String[]{"Loading"}));
        this.jComboBox1.setEnabled(false);
        this.jComboBox1.setName("jComboBox1");
        this.jComboBox2.setEnabled(false);
        this.jComboBox2.setName("jComboBox2");
        this.jLabel3.setText("From system.");
        this.jLabel3.setName("jLabel3");
        this.jCheckBox1.setSelected(true);
        this.jCheckBox1.setText("Extract DSDT from system.");
        this.jCheckBox1.setName("jCheckBox1");
        this.jButton2.setAction(actionMap.get("openAbout"));
        this.jButton2.setIcon(new ImageIcon(this.getClass().getResource("/dsdtparser/resources/help.png")));
        this.jButton2.setText("");
        this.jButton2.setName("jButton2");
        this.jButton3.setIcon(new ImageIcon(this.getClass().getResource("/dsdtparser/resources/save_dsdt.png")));
        this.jButton3.setName("jButton3");
        this.jButton4.setIcon(new ImageIcon(this.getClass().getResource("/dsdtparser/resources/exit.png")));
        this.jButton4.setName("jButton4");
        this.jButton7.setIcon(new ImageIcon(this.getClass().getResource("/dsdtparser/resources/refresh.png")));
        this.jButton7.setName("jButton7");
        this.jButton9.setIcon(new ImageIcon(this.getClass().getResource("/dsdtparser/resources/edit.png")));
        this.jButton9.setName("jButton9");
        this.jButton10.setIcon(new ImageIcon(this.getClass().getResource("/dsdtparser/resources/donate.png")));
        this.jButton10.setName("jButton10");
        this.jButton11.setIcon(new ImageIcon(this.getClass().getResource("/dsdtparser/resources/contribute_2.png")));
        this.jButton11.setName("jButton11");
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jButton7).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jCheckBox1).addGroup(layout.createSequentialGroup().addGap(29, 29, 29).addComponent(this.jLabel3, -2, 370, -2)))).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jLabel1, -1, 219, 32767).addGroup(layout.createSequentialGroup().addComponent(this.jButton10).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.jButton11).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jButton2))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.jProgressBar1, -2, 171, -2).addGroup(layout.createSequentialGroup().addComponent(this.jButton9).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jButton1).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jButton3).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jButton4)))).addComponent(this.jScrollPane1, -1, 430, 32767).addGroup(layout.createSequentialGroup().addComponent(this.jComboBox1, -2, 148, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jComboBox2, 0, 280, 32767))).addGap(40, 40, 40))).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jCheckBox1).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jLabel3)).addComponent(this.jButton7)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(this.jComboBox1, -2, -1, -2).addComponent(this.jComboBox2, -2, -1, -2)).addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addComponent(this.jScrollPane1, -2, 117, -2).addGap(16, 16, 16).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(this.jLabel1).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(this.jButton2, -1, -1, 32767).addComponent(this.jButton11, -1, -1, 32767).addComponent(this.jButton10))).addGroup(layout.createSequentialGroup().addComponent(this.jProgressBar1, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false).addComponent(this.jButton3, -1, -1, 32767).addComponent(this.jButton4, -1, -1, 32767).addComponent(this.jButton1)).addComponent(this.jButton9)))).addContainerGap()));
        this.pack();
    }

    private void jButton1ActionPerformed(ActionEvent evt) {
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable(){

            public void run() {
                new AutoPatcher().setVisible(true);
            }
        });
    }

}

