/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  jsyntaxpane.DefaultSyntaxKit
 *  org.jdesktop.application.Action
 *  org.jdesktop.application.Application
 *  org.jdesktop.application.ApplicationActionMap
 *  org.jdesktop.application.ApplicationContext
 *  org.jdesktop.application.FrameView
 *  org.jdesktop.application.LocalStorage
 *  org.jdesktop.application.ResourceMap
 *  org.jdesktop.application.SingleFrameApplication
 *  org.jdesktop.application.TaskMonitor
 *  org.jdesktop.application.View
 */
package dsdtparser;

import dsdtparser.parser.*;
import jsyntaxpane.DefaultSyntaxKit;
import org.jdesktop.application.Action;
import org.jdesktop.application.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.EditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import java.util.logging.*;

public class DSDTParserView extends FrameView {
    private DSDTParser parser;
    private boolean Blank;
    private File opened;
    private JPopupMenu loadPopup;
    private JPopupMenu dsdtPopup;
    private JPopupMenu treePopup;
    private JPopupMenu amlPopup;
    private Thread t;
    private Color defaultColor;
    private File defDir;
    private File defDirPatches;
    private boolean Modified;
    private boolean parsedOk;
    private SingleFrameApplication parent;
    private int scroll1;
    private int scroll2;
    private String[] lineBuffer = null;
    private String buffer = null;
    private JButton jButton1;
    private JEditorPane jEditorPane1;
    private JMenu jMenu1;
    private JMenu jMenu2;
    private JMenu jMenu3;
    private JMenu jMenu4;
    private JMenu jMenu5;
    private JMenuBar jMenuBar1;
    private JMenuItem jMenuItem1;
    private JMenuItem jMenuItem2;
    private JMenuItem jMenuItem3;
    private JMenuItem jMenuItem4;
    private JMenuItem jMenuItem5;
    private JMenuItem jMenuItem6;
    private JMenuItem jMenuItem7;
    private JMenuItem jMenuItem8;
    private JMenuItem jMenuItem9;
    private JRadioButtonMenuItem jRadioButtonMenuItem1;
    private JRadioButtonMenuItem jRadioButtonMenuItem2;
    private JRadioButtonMenuItem jRadioButtonMenuItem3;
    private JRadioButtonMenuItem jRadioButtonMenuItem4;
    private JRadioButtonMenuItem jRadioButtonMenuItem5;
    private JRadioButtonMenuItem jRadioButtonMenuItem6;
    private JScrollPane jScrollPane3;
    private JScrollPane jScrollPane4;
    private JSeparator jSeparator1;
    private JSeparator jSeparator3;
    private JSplitPane jSplitPane1;
    private JToolBar jToolBar1;
    private JTree jTree1;
    private JPanel mainPanel;
    private JProgressBar progressBar;
    private JLabel statusAnimationLabel;
    private JLabel statusMessageLabel;
    private JPanel statusPanel;
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;

    public DSDTParserView(SingleFrameApplication app) {
        super((Application)app);
        File f;
        LocalStorage ls;
        Object ob;
        this.parent = app;
        this.Blank = true;
        this.Modified = false;
        this.parsedOk = false;
        this.opened = null;
        this.initComponents();
        this.jButton1.setEnabled(false);
        this.jMenuItem5.setEnabled(false);
        this.createPopup();
        this.jTree1.getSelectionModel().setSelectionMode(1);
        this.jTree1.addTreeSelectionListener(new TreeSelectionListener(){

            public void valueChanged(TreeSelectionEvent arg0) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)DSDTParserView.this.jTree1.getLastSelectedPathComponent();
                if (node == null) {
                    return;
                }
                Object nodeInfo = node.getUserObject();
                DSDTItem dsdt = (DSDTItem)nodeInfo;
                DSDTParserView.this.jScrollPane4.getVerticalScrollBar().setValue(DSDTParserView.this.jScrollPane4.getVerticalScrollBar().getMaximum());
                DSDTParserView.this.jEditorPane1.setCaretPosition(dsdt.getRawLine() > 0 ? dsdt.getRawLine() : 0);
            }
        });
        this.jTree1.setCellRenderer(new TreeNodeRenderer(DSDTParserView.createImageIcon("device.png"), DSDTParserView.createImageIcon("folder-tree.png"), DSDTParserView.createImageIcon("resources/folder.png"), DSDTParserView.createImageIcon("resources/folder-open.png"), DSDTParserView.createImageIcon("resources/code.png"), DSDTParserView.createImageIcon("resources/processor.png"), DSDTParserView.createImageIcon("resources/fire.png")));
        this.jTree1.setModel(new DefaultTreeModel(null));
        DefaultSyntaxKit.initKit();
        this.jEditorPane1.setContentType("text/dsdt");
        this.jEditorPane1.setFont(new Font("Monaco", 0, 12));
        try {
            ls = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getLocalStorage();
            ob = ls.load("prefs.xml");
            Color color = (Color)ob;
            this.jEditorPane1.setBackground(color);
            System.out.println("Loaded preferences");
        }
        catch (IOException e) {
            // empty catch block
        }
        try {
            ls = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getLocalStorage();
            ob = ls.load("defdir");
            this.defDir = new File((String)ob);
        }
        catch (Exception e) {
            this.defDir = new File("a").getParentFile();
        }
        try {
            ls = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getLocalStorage();
            ob = ls.load("defdirpatches");
            this.defDirPatches = new File((String)ob);
        }
        catch (Exception e) {
            this.defDirPatches = new File("a").getParentFile();
        }
        this.defaultColor = this.jEditorPane1.getBackground();
        this.jToolBar1.removeAll();
        EditorKit kit = this.jEditorPane1.getEditorKit();
        if (kit instanceof DefaultSyntaxKit) {
            DefaultSyntaxKit defaultSyntaxKit = (DefaultSyntaxKit)kit;
            defaultSyntaxKit.addToolBarActions(this.jEditorPane1, this.jToolBar1);
        }
        this.jToolBar1.validate();
        JButton btn = (JButton)this.jToolBar1.getComponent(9);
        InputMap inputMap = btn.getInputMap(2);
        KeyStroke enter = KeyStroke.getKeyStroke(70, 4);
        inputMap.put(enter, "ENTER");
        btn.getActionMap().put("ENTER", new ClickAction(btn));
        btn = (JButton)this.jToolBar1.getComponent(7);
        inputMap = btn.getInputMap(2);
        enter = KeyStroke.getKeyStroke(90, 5);
        inputMap.put(enter, "ENTER");
        btn.getActionMap().put("ENTER", new ClickAction(btn));
        btn = (JButton)this.jToolBar1.getComponent(6);
        inputMap = btn.getInputMap(2);
        enter = KeyStroke.getKeyStroke(90, 4);
        inputMap.put(enter, "ENTER");
        btn.getActionMap().put("ENTER", new ClickAction(btn));
        btn = (JButton)this.jToolBar1.getComponent(10);
        inputMap = btn.getInputMap(2);
        enter = KeyStroke.getKeyStroke(71, 4);
        inputMap.put(enter, "ENTER");
        btn.getActionMap().put("ENTER", new ClickAction(btn));
        btn = (JButton)this.jToolBar1.getComponent(11);
        inputMap = btn.getInputMap(2);
        enter = KeyStroke.getKeyStroke(76, 4);
        inputMap.put(enter, "ENTER");
        btn.getActionMap().put("ENTER", new ClickAction(btn));
        btn = (JButton)this.jToolBar1.getComponent(14);
        inputMap = btn.getInputMap(2);
        enter = KeyStroke.getKeyStroke(32, 2);
        inputMap.put(enter, "ENTER");
        btn.getActionMap().put("ENTER", new ClickAction(btn));
        this.jToolBar1.remove(17);
        this.jToolBar1.remove(15);
        ResourceMap resourceMap = this.getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        this.messageTimer = new Timer(messageTimeout, new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                DSDTParserView.this.statusMessageLabel.setText("");
            }
        });
        this.messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < this.busyIcons.length; ++i) {
            this.busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        this.busyIconTimer = new Timer(busyAnimationRate, new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                DSDTParserView.this.busyIconIndex = (DSDTParserView.this.busyIconIndex + 1) % DSDTParserView.this.busyIcons.length;
                DSDTParserView.this.statusAnimationLabel.setIcon(DSDTParserView.this.busyIcons[DSDTParserView.this.busyIconIndex]);
            }
        });
        this.idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        this.statusAnimationLabel.setIcon(this.idleIcon);
        this.progressBar.setVisible(false);
        TaskMonitor taskMonitor = new TaskMonitor(this.getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new PropertyChangeListener(){

            public void propertyChange(PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!DSDTParserView.this.busyIconTimer.isRunning()) {
                        DSDTParserView.this.statusAnimationLabel.setIcon(DSDTParserView.this.busyIcons[0]);
                        DSDTParserView.this.busyIconIndex = 0;
                        DSDTParserView.this.busyIconTimer.start();
                    }
                    DSDTParserView.this.progressBar.setVisible(true);
                    DSDTParserView.this.progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    DSDTParserView.this.busyIconTimer.stop();
                    DSDTParserView.this.statusAnimationLabel.setIcon(DSDTParserView.this.idleIcon);
                    DSDTParserView.this.progressBar.setVisible(false);
                    DSDTParserView.this.progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text;
                    DSDTParserView.this.statusMessageLabel.setText((text = (String)evt.getNewValue()) == null ? "" : text);
                    DSDTParserView.this.messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)evt.getNewValue();
                    DSDTParserView.this.progressBar.setVisible(true);
                    DSDTParserView.this.progressBar.setIndeterminate(false);
                    DSDTParserView.this.progressBar.setValue(value);
                }
            }
        });
        if (DSDTParserApp.filename.length() > 0 && (f = new File(DSDTParserApp.filename)).exists()) {
            this.setFile(f);
        }
        this.jEditorPane1.getDocument().addDocumentListener(new MyDocumentListener());
    }

    public void close() {
    }

    public void loadBuffer() {
        this.buffer = this.jEditorPane1.getText();
    }

    public void scrollToEnd() {
        this.jScrollPane4.getVerticalScrollBar().setValue(this.jScrollPane4.getVerticalScrollBar().getMaximum());
    }

    public void commitBuffer() {
        this.parser = new DSDTParser(this.buffer);
        this.parser.setParent(this);
        this.parser.setPane(this.jEditorPane1);
        this.parser.setTree(this.jTree1);
        this.parser.run();
    }

    public void loadLineBuffer() {
        this.lineBuffer = this.jEditorPane1.getText().split("\n");
    }

    public String getLineAt(int number) {
        --number;
        if (this.lineBuffer == null) {
            this.loadLineBuffer();
        }
        return this.lineBuffer[number];
    }

    public void performReplace(String oldVal, String newVal) {
        this.buffer = this.buffer.replace(oldVal, newVal);
    }

    public void setCaretAtLine(int number) {
        this.jEditorPane1.setEditable(true);
        int pos = 0;
        String[] buffer = this.jEditorPane1.getText().split("\n");
        for (int i = 0; i < --number && i < buffer.length; ++i) {
            pos += buffer[i].length() + 1;
        }
        this.jScrollPane4.getVerticalScrollBar().setValue(this.jScrollPane4.getVerticalScrollBar().getMaximum());
        this.jEditorPane1.setCaretPosition(pos);
    }

    public void saveScrollPositions() {
        this.scroll1 = this.jScrollPane3.getVerticalScrollBar().getValue();
        this.scroll2 = this.jScrollPane4.getVerticalScrollBar().getValue();
    }

    public void loadScrollPositions() {
        this.jScrollPane3.getVerticalScrollBar().setValue(this.scroll1);
        this.jScrollPane4.getVerticalScrollBar().setValue(this.scroll2);
    }

    public File getDefDir() {
        return this.defDir;
    }

    public void setDefDir(File defDir) {
        this.defDir = defDir;
    }

    public boolean isBlank() {
        return this.Blank;
    }

    public void startLoading() {
        if (!this.busyIconTimer.isRunning()) {
            this.statusAnimationLabel.setIcon(this.busyIcons[0]);
            this.busyIconIndex = 0;
            this.busyIconTimer.start();
        }
        this.progressBar.setVisible(true);
        this.progressBar.setIndeterminate(true);
    }

    public void stopLoading() {
        this.busyIconTimer.stop();
        this.statusAnimationLabel.setIcon(this.idleIcon);
        this.progressBar.setVisible(false);
        this.progressBar.setValue(0);
    }

    public void setFile(File file) {
        if (this.opened == null) {
            this.opened = file;
        }
        this.jButton1.setEnabled(false);
        this.Blank = false;
        this.jTree1.setEnabled(true);
        this.parser = new DSDTParser(file);
        this.parser.setParent(this);
        this.parser.setPane(this.jEditorPane1);
        this.parser.setTree(this.jTree1);
        this.t = new Thread(this.parser);
        this.t.start();
    }

    public void disableButton() {
        this.jButton1.setEnabled(false);
        this.jTree1.setEnabled(true);
        this.Modified = false;
    }

    public void simpleUpdate() {
        int pos = this.jEditorPane1.getCaretPosition();
        this.jEditorPane1.setText(this.parser.getRoot().printEntry());
        this.jEditorPane1.setCaretPosition(pos);
    }

    public void fullUpdate() {
        String newfile = this.parser.getRoot().printEntry();
        this.parser = new DSDTParser(newfile);
        this.parser.setParent(this);
        this.parser.setPane(this.jEditorPane1);
        this.parser.setTree(this.jTree1);
        this.t = new Thread(this.parser);
        this.t.start();
    }

    public void fullUpdateWaitFor() {
    }

    public Color getBackgroundColor() {
        return this.jEditorPane1.getBackground();
    }

    private void createPopup() {
        this.loadPopup = new JPopupMenu();
        DSDTParserView parent = this;
        ActionListener menuListener = new ActionListener(){

            public void actionPerformed(ActionEvent event) {
                if ("Load from file...".equals(event.getActionCommand())) {
                    DSDTParserView.this.loadFromFile();
                } else if ("Extract from system".equals(event.getActionCommand())) {
                    DSDTParserView.this.extractDSDT();
                } else if ("Save to file...".equalsIgnoreCase(event.getActionCommand())) {
                    DSDTParserView.this.dslSave();
                }
            }
        };
        JMenuItem item = new JMenuItem("Load from file...");
        this.loadPopup.add(item);
        item.setHorizontalTextPosition(0);
        item.addActionListener(menuListener);
        item = new JMenuItem("Save to file...");
        this.loadPopup.add(item);
        item.setHorizontalTextPosition(0);
        item.addActionListener(menuListener);
        this.loadPopup.addSeparator();
        item = new JMenuItem("Extract from system");
        this.loadPopup.add(item);
        item.setHorizontalTextPosition(0);
        item.addActionListener(menuListener);
        this.amlPopup = new JPopupMenu();
        menuListener = new ActionListener(){

            public void actionPerformed(ActionEvent event) {
                if ("Load from file...".equals(event.getActionCommand())) {
                    DSDTParserView.this.loadFromFile();
                } else if ("Compile".equals(event.getActionCommand())) {
                    DSDTParserView.this.compile();
                } else if ("Save to file...".equalsIgnoreCase(event.getActionCommand())) {
                    DSDTParserView.this.amlSave();
                }
            }
        };
        item = new JMenuItem("Load from file...");
        this.amlPopup.add(item);
        item.setHorizontalTextPosition(0);
        item.addActionListener(menuListener);
        item = new JMenuItem("Save to file...");
        this.amlPopup.add(item);
        item.setHorizontalTextPosition(0);
        item.addActionListener(menuListener);
        this.amlPopup.addSeparator();
        item = new JMenuItem("Compile");
        this.amlPopup.add(item);
        item.setHorizontalTextPosition(0);
        item.addActionListener(menuListener);
        this.dsdtPopup = new JPopupMenu();
        menuListener = new ActionListener(){

            public void actionPerformed(ActionEvent event) {
                if ("Load from file...".equals(event.getActionCommand())) {
                    DSDTParserView.this.patchLoadFromFile();
                } else if ("New patch".equals(event.getActionCommand())) {
                    DSDTParserView.this.patchNewPatch();
                }
            }
        };
        item = new JMenuItem("Load from file...");
        this.dsdtPopup.add(item);
        item.setHorizontalTextPosition(0);
        item.addActionListener(menuListener);
        item = new JMenuItem("New patch");
        this.dsdtPopup.add(item);
        item.setHorizontalTextPosition(0);
        item.addActionListener(menuListener);
    }

    @Action
    public void openAutoPatcher() {
        AutoPatcher apw = new AutoPatcher();
        apw.setDefaultCloseOperation(2);
        apw.setVisible(true);
    }

    @Action
    public void setDefaultColor() {
        try {
            LocalStorage ls = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getLocalStorage();
            ls.save((Object)this.defaultColor, "prefs.xml");
            System.out.println("Saved preferences");
        }
        catch (IOException e) {
            // empty catch block
        }
        this.jEditorPane1.setBackground(this.defaultColor);
        this.jRadioButtonMenuItem1.setSelected(true);
        this.jRadioButtonMenuItem2.setSelected(false);
        this.jRadioButtonMenuItem3.setSelected(false);
        this.jRadioButtonMenuItem4.setSelected(false);
        this.jRadioButtonMenuItem5.setSelected(false);
        this.jRadioButtonMenuItem6.setSelected(false);
    }

    @Action
    public void setColorWhite() {
        Color yellow = new Color(255, 255, 255);
        try {
            LocalStorage ls = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getLocalStorage();
            ls.save((Object)yellow, "prefs.xml");
            System.out.println("Saved preferences");
        }
        catch (IOException e) {
            // empty catch block
        }
        this.jEditorPane1.setBackground(yellow);
        this.jRadioButtonMenuItem1.setSelected(false);
        this.jRadioButtonMenuItem2.setSelected(false);
        this.jRadioButtonMenuItem3.setSelected(false);
        this.jRadioButtonMenuItem4.setSelected(false);
        this.jRadioButtonMenuItem5.setSelected(false);
        this.jRadioButtonMenuItem6.setSelected(true);
    }

    @Action
    public void setColorYellow() {
        Color yellow = new Color(252, 253, 149);
        try {
            LocalStorage ls = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getLocalStorage();
            ls.save((Object)yellow, "prefs.xml");
            System.out.println("Saved preferences");
        }
        catch (IOException e) {
            // empty catch block
        }
        this.jEditorPane1.setBackground(yellow);
        this.jRadioButtonMenuItem1.setSelected(false);
        this.jRadioButtonMenuItem2.setSelected(true);
        this.jRadioButtonMenuItem3.setSelected(false);
        this.jRadioButtonMenuItem4.setSelected(false);
        this.jRadioButtonMenuItem5.setSelected(false);
        this.jRadioButtonMenuItem6.setSelected(false);
    }

    @Action
    public void setColorGreen() {
        Color yellow = new Color(204, 255, 204);
        try {
            LocalStorage ls = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getLocalStorage();
            ls.save((Object)yellow, "prefs.xml");
            System.out.println("Saved preferences");
        }
        catch (IOException e) {
            // empty catch block
        }
        this.jEditorPane1.setBackground(yellow);
        this.jRadioButtonMenuItem1.setSelected(false);
        this.jRadioButtonMenuItem2.setSelected(false);
        this.jRadioButtonMenuItem3.setSelected(false);
        this.jRadioButtonMenuItem4.setSelected(false);
        this.jRadioButtonMenuItem5.setSelected(false);
        this.jRadioButtonMenuItem6.setSelected(false);
    }

    @Action
    public void setColorGray() {
        Color yellow = new Color(230, 230, 230);
        try {
            LocalStorage ls = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getLocalStorage();
            ls.save((Object)yellow, "prefs.xml");
            System.out.println("Saved preferences");
        }
        catch (IOException e) {
            // empty catch block
        }
        this.jEditorPane1.setBackground(yellow);
        this.jRadioButtonMenuItem1.setSelected(false);
        this.jRadioButtonMenuItem2.setSelected(false);
        this.jRadioButtonMenuItem3.setSelected(true);
        this.jRadioButtonMenuItem4.setSelected(false);
        this.jRadioButtonMenuItem5.setSelected(false);
        this.jRadioButtonMenuItem6.setSelected(false);
    }

    @Action
    public void customColor() {
        Color newColor = JColorChooser.showDialog(this.getComponent(), "Choose background color", this.jEditorPane1.getBackground());
        if (newColor != null) {
            try {
                LocalStorage ls = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getLocalStorage();
                ls.save((Object)newColor, "prefs.xml");
                System.out.println("Saved preferences");
            }
            catch (IOException e) {
                // empty catch block
            }
            this.jEditorPane1.setBackground(newColor);
            this.jRadioButtonMenuItem1.setSelected(false);
            this.jRadioButtonMenuItem2.setSelected(false);
            this.jRadioButtonMenuItem3.setSelected(false);
            this.jRadioButtonMenuItem4.setSelected(false);
            this.jRadioButtonMenuItem5.setSelected(true);
            this.jRadioButtonMenuItem6.setSelected(false);
        }
    }

    @Action
    public void newWindow() {
        DSDTParserView view = new DSDTParserView(this.parent);
        DSDTParserApp.getApplication().show((View)view);
    }

    @Action
    public void dslSave() {
        File temp = new File("dsdt.dsl");
        if (this.opened == null || this.opened.getAbsolutePath().toLowerCase().endsWith(".aml") || temp.getAbsolutePath().equals(this.opened.getAbsolutePath())) {
            this.opened = DSDTParserView.promptForFile(this.getFrame(), true, this.getDefDir());
        }
        if (this.opened != null) {
            try {
                this.defDir = this.opened.getParentFile();
                try {
                    LocalStorage ls = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getLocalStorage();
                    ls.save((Object)this.defDir.getAbsolutePath(), "defdir");
                }
                catch (Exception e) {
                    // empty catch block
                }
                BufferedWriter out = new BufferedWriter(new FileWriter(this.opened));
                out.write(this.jEditorPane1.getText());
                out.close();
            }
            catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
    }

    @Action
    public void dslSaveAs() {
        File f = DSDTParserView.promptForFile(this.getFrame(), true, this.getDefDir());
        if (f != null) {
            try {
                this.defDir = f.getParentFile();
                try {
                    LocalStorage ls = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getLocalStorage();
                    ls.save((Object)this.defDir.getAbsolutePath(), "defdir");
                }
                catch (Exception e) {
                    // empty catch block
                }
                BufferedWriter out = new BufferedWriter(new FileWriter(f));
                out.write(this.jEditorPane1.getText());
                out.close();
            }
            catch (IOException e) {
                System.out.println(e.getLocalizedMessage());
            }
        }
    }

    public void toggleSaveAML(boolean bol) {
        this.jMenuItem5.setEnabled(bol);
    }

    @Action
    public void amlSave() {
        AMLCompiler comp = new AMLCompiler(this, 1);
        Thread tr = new Thread(comp);
        tr.start();
    }

    @Action
    public void patchNewPatch() {
        if (this.parser != null && this.parser.getRoot() != null) {
            if (this.Modified || !this.parsedOk) {
                this.parser = new DSDTParser(this.jEditorPane1.getText());
                this.parser.setParent(this);
                this.parser.setPostAction(1);
                this.parser.setPane(this.jEditorPane1);
                this.parser.setTree(this.jTree1);
                this.t = new Thread(this.parser);
                this.t.start();
            } else {
                ActionForm af = new ActionForm(this, this.parser.getRoot(), true);
                af.setVisible(true);
            }
        } else {
            this.showMessage("Error", "Cannot create a patch for null DSDT", 1);
        }
    }

    public void setParsedOk(boolean a) {
        this.parsedOk = a;
    }

    public void callerNewPatch() {
        if (this.parser != null && this.parser.getRoot() != null) {
            ActionForm af = new ActionForm(this, this.parser.getRoot(), true);
            af.setVisible(true);
        } else {
            this.showMessage("Error", "Cannot create a patch for null DSDT", 1);
        }
    }

    @Action
    public void patchLoadFromFile() {
        if (this.parser != null && this.parser.getRoot() != null) {
            if (this.Modified || !this.parsedOk) {
                this.parser = new DSDTParser(this.jEditorPane1.getText());
                this.parser.setParent(this);
                this.parser.setPostAction(2);
                this.parser.setPane(this.jEditorPane1);
                this.parser.setTree(this.jTree1);
                this.t = new Thread(this.parser);
                this.t.start();
            } else {
                File f = DSDTParserView.promptForFile(this.getFrame(), false, this.defDirPatches);
                if (f != null) {
                    ActionForm af = new ActionForm(this, this.parser.getRoot(), true);
                    this.defDirPatches = f.getParentFile();
                    System.out.println(this.defDirPatches.getAbsolutePath());
                    try {
                        LocalStorage ls = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getLocalStorage();
                        ls.save((Object)this.defDirPatches.getAbsolutePath(), "defdirpatches");
                    }
                    catch (IOException e) {
                        // empty catch block
                    }
                    af.setContent(f);
                    af.setVisible(true);
                }
            }
        } else {
            this.showMessage("Error", "Cannot open a patch for a null DSDT", 1);
        }
    }

    public void callerOpenPatch() {
        if (this.parser != null && this.parser.getRoot() != null) {
            File f = DSDTParserView.promptForFile(this.getFrame(), false, this.defDirPatches);
            if (f != null) {
                ActionForm af = new ActionForm(this, this.parser.getRoot(), true);
                this.defDirPatches = f.getParentFile();
                System.out.println(this.defDirPatches.getAbsolutePath());
                try {
                    LocalStorage ls = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getLocalStorage();
                    ls.save((Object)this.defDirPatches.getAbsolutePath(), "defdirpatches");
                }
                catch (IOException e) {
                    // empty catch block
                }
                af.setContent(f);
                af.setVisible(true);
            }
        } else {
            this.showMessage("Error", "Cannot open a patch for a null DSDT", 1);
        }
    }

    @Action
    public void extractDSDT() {
        DSDTExtractor extractor = new DSDTExtractor(this);
        Thread tr = new Thread(extractor);
        tr.start();
    }

    @Action
    public void compile() {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("dsdt.dsl"));
            out.write(this.jEditorPane1.getText());
            out.close();
            AMLCompiler comp = new AMLCompiler(this, 0);
            Thread tr = new Thread(comp);
            tr.start();
        }
        catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public void saveDsdtDsl() {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("dsdt.dsl"));
            out.write(this.jEditorPane1.getText());
            out.close();
        }
        catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    @Action
    public void loadFromFile() {
        File f = DSDTParserView.promptForFile(this.getFrame(), false, this.getDefDir());
        if (f != null) {
            this.defDir = f.getParentFile();
            System.out.println(this.defDir.getAbsolutePath());
            try {
                LocalStorage ls = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getLocalStorage();
                ls.save((Object)this.defDir.getAbsolutePath(), "defdir");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            this.opened = f;
            if (f.getAbsolutePath().toLowerCase().endsWith(".aml")) {
                Thread t = new Thread(new AMLDecompiler(this, f));
                t.start();
            } else {
                this.setFile(f);
            }
        }
    }

    public void showMessage(String title, String msg, int type) {
        JOptionPane.showMessageDialog(this.getFrame(), msg, title, type);
    }

    public static File promptForFile(JFrame frame, boolean save, File defaultDir) {
        FileDialog fc = new FileDialog(frame, "Select file", save ? 1 : 0);
        if (defaultDir != null) {
            fc.setDirectory(defaultDir.getAbsolutePath());
        }
        fc.setVisible(true);
        if (fc.getFile() != null) {
            File file = new File(fc.getDirectory() + fc.getFile());
            return file;
        }
        return null;
    }

    protected static ImageIcon createImageIcon(String path) {
        URL imgURL = DSDTParserView.class.getResource(path);
        Logger logger = Logger.getLogger(DSDTParserView.class.getName());
        logger.log(Level.INFO, "ImagesURL: " + path + " " + imgURL);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        }
        System.err.println("Couldn't find file: " + path);
        return null;
    }

    public void enableTreeButton() {
        this.jButton1.setEnabled(true);
    }

    @Action
    public void updateTree() {
        this.parser = new DSDTParser(this.jEditorPane1.getText());
        this.parser.setParent(this);
        this.parser.setPane(this.jEditorPane1);
        this.parser.setTree(this.jTree1);
        this.t = new Thread(this.parser);
        this.t.start();
        this.Modified = false;
        this.jButton1.setEnabled(false);
        this.jTree1.setEnabled(true);
    }

    @Action
    public void showAboutBox() {
        if (this.aboutBox == null) {
            JFrame mainFrame = DSDTParserApp.getApplication().getMainFrame();
            this.aboutBox = new DSDTParserAboutBox(mainFrame);
            this.aboutBox.setLocationRelativeTo(mainFrame);
        }
        DSDTParserApp.getApplication().show(this.aboutBox);
    }

    private void initComponents() {
        this.mainPanel = new JPanel();
        this.jSplitPane1 = new JSplitPane();
        this.jScrollPane3 = new JScrollPane();
        this.jTree1 = new JTree();
        this.jScrollPane4 = new JScrollPane();
        this.jEditorPane1 = new JEditorPane();
        this.jToolBar1 = new JToolBar();
        this.statusPanel = new JPanel();
        JSeparator statusPanelSeparator = new JSeparator();
        this.statusMessageLabel = new JLabel();
        this.statusAnimationLabel = new JLabel();
        this.progressBar = new JProgressBar();
        this.jButton1 = new JButton();
        this.jMenuBar1 = new JMenuBar();
        this.jMenu1 = new JMenu();
        this.jMenuItem9 = new JMenuItem();
        this.jMenuItem1 = new JMenuItem();
        this.jMenuItem2 = new JMenuItem();
        this.jMenuItem4 = new JMenuItem();
        this.jSeparator1 = new JSeparator();
        this.jMenuItem3 = new JMenuItem();
        this.jMenu3 = new JMenu();
        this.jMenuItem8 = new JMenuItem();
        this.jMenuItem7 = new JMenuItem();
        this.jMenu2 = new JMenu();
        this.jMenuItem6 = new JMenuItem();
        this.jMenuItem5 = new JMenuItem();
        this.jMenu4 = new JMenu();
        this.jMenu5 = new JMenu();
        this.jRadioButtonMenuItem1 = new JRadioButtonMenuItem();
        this.jRadioButtonMenuItem6 = new JRadioButtonMenuItem();
        this.jRadioButtonMenuItem2 = new JRadioButtonMenuItem();
        this.jRadioButtonMenuItem3 = new JRadioButtonMenuItem();
        this.jRadioButtonMenuItem4 = new JRadioButtonMenuItem();
        this.jSeparator3 = new JSeparator();
        this.jRadioButtonMenuItem5 = new JRadioButtonMenuItem();
        this.mainPanel.setName("mainPanel");
        this.mainPanel.setPreferredSize(new Dimension(800, 600));
        this.jSplitPane1.setBorder(null);
        this.jSplitPane1.setDividerLocation(240);
        this.jSplitPane1.setDividerSize(5);
        this.jSplitPane1.setName("jSplitPane1");
        this.jScrollPane3.setName("jScrollPane3");
        this.jTree1.setName("jTree1");
        this.jScrollPane3.setViewportView(this.jTree1);
        this.jSplitPane1.setLeftComponent(this.jScrollPane3);
        this.jScrollPane4.setName("jScrollPane4");
        this.jEditorPane1.setFont(new Font("Monaco", 0, 12));
        this.jScrollPane4.setViewportView(this.jEditorPane1);
        this.jSplitPane1.setRightComponent(this.jScrollPane4);
        this.jToolBar1.setRollover(true);
        this.jToolBar1.setName("jToolBar1");
        GroupLayout mainPanelLayout = new GroupLayout(this.mainPanel);
        this.mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(this.jToolBar1, -1, 769, 32767).addComponent(this.jSplitPane1, -1, 769, 32767));
        mainPanelLayout.setVerticalGroup(mainPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(mainPanelLayout.createSequentialGroup().addComponent(this.jToolBar1, -2, 25, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.jSplitPane1, -1, 330, 32767)));
        this.statusPanel.setName("statusPanel");
        this.statusPanel.setPreferredSize(new Dimension(769, 40));
        statusPanelSeparator.setName("statusPanelSeparator");
        this.statusMessageLabel.setName("statusMessageLabel");
        this.statusAnimationLabel.setHorizontalAlignment(2);
        this.statusAnimationLabel.setName("statusAnimationLabel");
        this.progressBar.setName("progressBar");
        ApplicationActionMap actionMap = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getActionMap((Class)DSDTParserView.class, (Object)this);
        this.jButton1.setAction(actionMap.get("updateTree"));
        this.jButton1.setName("jButton1");
        GroupLayout statusPanelLayout = new GroupLayout(this.statusPanel);
        this.statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(statusPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(statusPanelLayout.createSequentialGroup().addGroup(statusPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(statusPanelLayout.createSequentialGroup().addContainerGap().addComponent(this.statusMessageLabel)).addGroup(statusPanelLayout.createSequentialGroup().addGap(8, 8, 8).addComponent(this.jButton1))).addGap(320, 320, 320).addGroup(statusPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(statusPanelSeparator, -1, 325, 32767).addGroup(GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup().addComponent(this.progressBar, -2, -1, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(this.statusAnimationLabel, -2, 24, -2).addGap(9, 9, 9)))));
        statusPanelLayout.setVerticalGroup(statusPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(statusPanelLayout.createSequentialGroup().addComponent(statusPanelSeparator, -2, 2, -2).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(statusPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(this.statusAnimationLabel, GroupLayout.Alignment.TRAILING, -2, 21, -2).addGroup(statusPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(this.jButton1, -2, 22, 32767).addComponent(this.progressBar, GroupLayout.Alignment.LEADING, -2, -1, -2))).addGap(5, 5, 5).addComponent(this.statusMessageLabel).addGap(16, 16, 16)));
        this.jMenuBar1.setName("jMenuBar1");
        this.jMenu1.setText("File");
        this.jMenu1.setName("jMenu1");
        this.jMenuItem9.setAction(actionMap.get("newWindow"));
        this.jMenuItem9.setName("jMenuItem9");
        this.jMenu1.add(this.jMenuItem9);
        this.jMenuItem1.setAction(actionMap.get("loadFromFile"));
        this.jMenuItem1.setName("jMenuItem1");
        this.jMenu1.add(this.jMenuItem1);
        this.jMenuItem2.setAction(actionMap.get("dslSave"));
        this.jMenuItem2.setName("jMenuItem2");
        this.jMenu1.add(this.jMenuItem2);
        this.jMenuItem4.setAction(actionMap.get("dslSaveAs"));
        this.jMenuItem4.setName("jMenuItem4");
        this.jMenu1.add(this.jMenuItem4);
        this.jSeparator1.setName("jSeparator1");
        this.jMenu1.add(this.jSeparator1);
        this.jMenuItem3.setAction(actionMap.get("extractDSDT"));
        this.jMenuItem3.setName("jMenuItem3");
        this.jMenu1.add(this.jMenuItem3);
        this.jMenuBar1.add(this.jMenu1);
        this.jMenu3.setAction(actionMap.get("patchLoadFromFile"));
        this.jMenu3.setName("jMenu3");
        this.jMenuItem8.setAction(actionMap.get("patchNewPatch"));
        this.jMenuItem8.setName("jMenuItem8");
        this.jMenu3.add(this.jMenuItem8);
        this.jMenuItem7.setAction(actionMap.get("patchLoadFromFile"));
        ResourceBundle bundle = ResourceBundle.getBundle("DSDTParserView", Locale.ENGLISH);
        this.jMenuItem7.setText(bundle.getString("openPatch.Action.text"));
        //this.jMenuItem7.setText("openPatch");
        this.jMenuItem7.setName("jMenuItem7");
        this.jMenu3.add(this.jMenuItem7);
        this.jMenuBar1.add(this.jMenu3);
        this.jMenu2.setText("IASL");
        this.jMenu2.setName("jMenu2");
        this.jMenuItem6.setAction(actionMap.get("compile"));
        this.jMenuItem6.setName("jMenuItem6");
        this.jMenu2.add(this.jMenuItem6);
        this.jMenuItem5.setAction(actionMap.get("amlSave"));
        this.jMenuItem5.setName("jMenuItem5");
        this.jMenu2.add(this.jMenuItem5);
        this.jMenuBar1.add(this.jMenu2);
        this.jMenu4.setText("Options");
        this.jMenu4.setName("jMenu4");
        this.jMenu5.setText("Background color");
        this.jMenu5.setName("jMenu5");
        this.jRadioButtonMenuItem1.setAction(actionMap.get("setDefaultColor"));
        this.jRadioButtonMenuItem1.setSelected(true);
        this.jRadioButtonMenuItem1.setName("jRadioButtonMenuItem1");
        this.jMenu5.add(this.jRadioButtonMenuItem1);
        this.jRadioButtonMenuItem6.setAction(actionMap.get("setColorWhite"));
        this.jRadioButtonMenuItem6.setName("jRadioButtonMenuItem6");
        this.jMenu5.add(this.jRadioButtonMenuItem6);
        this.jRadioButtonMenuItem2.setAction(actionMap.get("setColorYellow"));
        this.jRadioButtonMenuItem2.setName("jRadioButtonMenuItem2");
        this.jMenu5.add(this.jRadioButtonMenuItem2);
        this.jRadioButtonMenuItem3.setAction(actionMap.get("setColorGray"));
        this.jRadioButtonMenuItem3.setName("jRadioButtonMenuItem3");
        this.jMenu5.add(this.jRadioButtonMenuItem3);
        this.jRadioButtonMenuItem4.setAction(actionMap.get("setColorGreen"));
        this.jRadioButtonMenuItem4.setName("jRadioButtonMenuItem4");
        this.jMenu5.add(this.jRadioButtonMenuItem4);
        this.jSeparator3.setName("jSeparator3");
        this.jMenu5.add(this.jSeparator3);
        this.jRadioButtonMenuItem5.setAction(actionMap.get("customColor"));
        this.jRadioButtonMenuItem5.setName("jRadioButtonMenuItem5");
        this.jMenu5.add(this.jRadioButtonMenuItem5);
        this.jMenu4.add(this.jMenu5);
        this.jMenuBar1.add(this.jMenu4);
        this.setComponent((JComponent)this.mainPanel);
        this.setMenuBar(this.jMenuBar1);
        this.setStatusBar((JComponent)this.statusPanel);
    }

    public DSDTParser getParser() {
        return this.parser;
    }

    class ClickAction extends AbstractAction {
        private JButton button;

        public ClickAction(JButton button) {
            this.button = button;
        }

        public void actionPerformed(ActionEvent e) {
            this.button.doClick();
        }
    }

    protected class MyDocumentListener
    implements DocumentListener {
        protected MyDocumentListener() {
        }

        public void insertUpdate(DocumentEvent e) {
            this.displayEditInfo(e);
        }

        public void removeUpdate(DocumentEvent e) {
            this.displayEditInfo(e);
        }

        public void changedUpdate(DocumentEvent e) {
            this.displayEditInfo(e);
        }

        private void displayEditInfo(DocumentEvent e) {
            DSDTParserView.this.Modified = true;
            DSDTParserView.this.jButton1.setEnabled(true);
        }
    }

}

