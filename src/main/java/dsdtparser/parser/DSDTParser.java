/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  jsyntaxpane.SyntaxDocument
 */
package dsdtparser.parser;

import dsdtparser.DSDTParserView;
import jsyntaxpane.SyntaxDocument;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DSDTParser
implements Runnable {
    private static final boolean DEBUG = true;
    private String bigBuff;
    private ArrayList<DSDTItem> scopes;
    private DSDTItem root;
    private DSDTParserView parent = null;
    private JTree tree;
    private JEditorPane pane;
    private File file;
    private int postAction;
    public static final int COMPILE = 0;
    public static final int NEW_PATCH = 1;
    public static final int OPEN_PATCH = 2;

    public DSDTParser(String bigBuff) {
        this.postAction = -1;
        this.bigBuff = bigBuff.replace('\r', '\n');
        this.scopes = new ArrayList();
    }

    public void setPostAction(int action) {
        this.postAction = action;
    }

    public DSDTParser(File file) {
        this.file = file;
        this.scopes = new ArrayList();
        this.bigBuff = null;
    }

    private DSDTItem getScope(String scope) {
        for (int i = 0; i < this.scopes.size(); ++i) {
            if (!this.scopes.get(i).getParametro()[0].equalsIgnoreCase(scope)) continue;
            return this.scopes.get(i);
        }
        return null;
    }

    public void dumpData() {
        System.out.println(this.getRoot().printEntry());
    }

    public int espacos(String line) {
        int i;
        for (i = 0; i < line.length() && line.charAt(i) == ' '; ++i) {
        }
        return i / 4;
    }

    public int parse() {
        try {
            Object in = null;
            Pattern db = Pattern.compile("\\s*DefinitionBlock ?\\((\"[^\"]+\"), (\"[^\"]+\"), (?:0x)?([0-9A-F]+), (\"[^\"]*\"), (\"[^\"]*\"), (0x[0-9A-F]+)\\)(\\s*.*)$");
            Pattern scope = Pattern.compile("\\s*Scope ?\\((.*)\\)(\\s*.*)$");
            Pattern method = Pattern.compile("\\s*Method ?\\((.*), (\\d+), (.*)\\)(\\s*.*)$");
            Pattern device = Pattern.compile("\\s*Device ?\\((.*)\\)(\\s*.*)$");
            Pattern processor = Pattern.compile("^(\\s*)Processor ?\\(((?:\\\\_PR\\.)?[CPU0-9A-F]+), (0x[0-9A-Fa-f]+), (0x[0-9A-Fa-f]+), (0x[0-9A-Fa-f]+)\\)\\s*(?:\\{\\})?(\\s*.*)$");
            Pattern name = Pattern.compile("^(\\s*)Name ?\\(([^ ,]+) ?, ?(.*$)");
            Pattern thermal = Pattern.compile("\\s*ThermalZone ?\\((.*)\\)(\\s*.*)$");
            Pattern open = Pattern.compile("^(\\s*\\{\\s*)(\\/.*)?\\s*$");
            Pattern close = Pattern.compile("^\\s*\\}");
            int level = 0;
            int namelevel = 0;
            DSDTItem parent = null;
            int i = 0;
            String[] tmp = this.bigBuff.split("\n");
            for (int k = 0; k < tmp.length; ++k) {
                String line = tmp[k];
                if (namelevel != 0) {
                    if (!line.matches("^\\s*$")) {
                        parent.appendLine(line);
                        if ((namelevel += this.checkBrackets(line)) == 0) {
                            parent = parent.getParent();
                        }
                    }
                } else {
                    Matcher m = open.matcher(line);
                    if (m.matches()) {
                        parent.appendLine(m.group(1) + "\n");
                        if (m.group(2) != null) {
                            parent.appendLine(m.group(2) + "\n");
                        }
                        ++level;
                    } else {
                        m = close.matcher(line);
                        if (m.find()) {
                            parent.appendLine(line);
                            if (--level == parent.getLevel()) {
                                parent = parent.getParent();
                            }
                        } else {
                            m = db.matcher(line);
                            if (m.find()) {
                                parent = new DSDTItem(0, new String[]{m.group(1), m.group(2), m.group(3), m.group(4), m.group(5), m.group(6)});
                                parent.setComment(m.group(7));
                                Pattern semAspas = Pattern.compile("^\"(.*)\"$");
                                Matcher sem = semAspas.matcher(parent.getParametro()[0]);
                                if (sem.find()) {
                                    File f = new File(sem.group(1));
                                    parent.getParametro()[0] = "\"" + f.getName() + "\"";
                                }
                                parent.setRawLine(i -= parent.getHeader().length() - m.group(0).length());
                                this.root = parent;
                                parent.setLevel(level);
                            } else if (parent != null) {
                                DSDTItem item;
                                m = scope.matcher(line);
                                if (m.matches()) {
                                    item = new DSDTItem(1, new String[]{m.group(1)}, parent);
                                    item.setComment(m.group(2));
                                    item.setRawLine(i);
                                    item.setLevel(level);
                                    this.scopes.add(item);
                                    parent.appendLine("[child]");
                                    parent.addChild(item);
                                    parent = item;
                                } else {
                                    m = method.matcher(line);
                                    if (m.matches()) {
                                        item = new DSDTItem(2, new String[]{m.group(1), m.group(2), m.group(3)}, parent);
                                        item.setComment(m.group(4));
                                        item.setRawLine(i);
                                        item.setLevel(level);
                                        parent.appendLine("[child]");
                                        parent.addChild(item);
                                        parent = item;
                                    } else {
                                        m = device.matcher(line);
                                        if (m.matches()) {
                                            item = new DSDTItem(3, new String[]{m.group(1)}, parent);
                                            item.setComment(m.group(2));
                                            item.setRawLine(i);
                                            item.setLevel(level);
                                            parent.appendLine("[child]");
                                            parent.addChild(item);
                                            parent = item;
                                        } else {
                                            m = processor.matcher(line);
                                            if (m.matches()) {
                                                item = new DSDTItem(4, new String[]{m.group(2), m.group(3), m.group(4), m.group(5)}, parent);
                                                item.setComment(m.group(6));
                                                item.setRawLine(i);
                                                item.setLevel(level);
                                                parent.appendLine("[child]");
                                                parent.addChild(item);
                                                if (!line.endsWith("{}")) {
                                                    parent = item;
                                                } else {
                                                    item.appendLine(m.group(1) + "{");
                                                    item.appendLine(m.group(1) + "}");
                                                    i += m.group(1).length() * 2;
                                                }
                                            } else {
                                                m = thermal.matcher(line);
                                                if (m.matches()) {
                                                    item = new DSDTItem(6, new String[]{m.group(1)}, parent);
                                                    item.setComment(m.group(2));
                                                    item.setRawLine(i);
                                                    item.setLevel(level);
                                                    this.scopes.add(item);
                                                    parent.appendLine("[child]");
                                                    parent.addChild(item);
                                                    parent = item;
                                                } else if (!line.matches("^\\s*$")) {
                                                    parent.appendLine(line);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (parent == null || line.matches("^\\s*$")) continue;
                i += line.length() + 1;
            }
            return level;
        }
        catch (Exception ex) {
            return -1;
        }
    }

    private int checkBrackets(String line) {
        char[] buf = line.toCharArray();
        int close = 0;
        int open = 0;
        for (int i = 0; i < buf.length; ++i) {
            if (buf[i] == '(') {
                ++open;
                continue;
            }
            if (buf[i] != ')') continue;
            ++close;
        }
        return open - close;
    }

    public DSDTItem getRoot() {
        return this.root;
    }

    public DSDTParserView getParent() {
        return this.parent;
    }

    public void setParent(DSDTParserView parent) {
        this.parent = parent;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void run() {
        if (this.parent != null) {
            this.parent.startLoading();
            this.parent.setParsedOk(false);
        }
        if (this.bigBuff == null) {
            Object in = null;
            String buffer = "";
            try {
                int nRead;
                BufferedReader f = new BufferedReader(new FileReader(this.file));
                char[] barray = new char[1024];
                while ((nRead = f.read(barray, 0, 1024)) != -1) {
                    buffer = buffer + new String(barray).substring(0, nRead);
                }
                this.bigBuff = buffer.replace('\r', '\n');
                this.parse();
                this.bigBuff = this.root.printEntry();
                this.root = null;
                this.scopes = new ArrayList();
            }
            catch (FileNotFoundException ex) {
                Logger.getLogger(ActionParser.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (IOException ex) {
                Logger.getLogger(ActionParser.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (Exception e) {
                this.bigBuff = buffer;
                this.root = null;
                this.scopes = new ArrayList();
            }
        }
        int level = this.parse();
        if (this.parent != null) {
            this.parent.saveScrollPositions();
        }
        if (this.tree != null && this.root != null) {
            if (level == 0) {
                this.tree.setModel(new DefaultTreeModel(this.root.getNode()));
                DefaultMutableTreeNode root = (DefaultMutableTreeNode)this.tree.getModel().getRoot();
                for (int i = root.getChildCount() - 1; i >= 0; --i) {
                    if (root.getChildAt(i).isLeaf()) continue;
                    this.tree.scrollPathToVisible(new TreePath(((DefaultMutableTreeNode)root.getChildAt(i).getChildAt(0)).getPath()));
                }
            }
        } else if (this.tree != null && level == 0) {
            this.tree.setModel(new DefaultTreeModel(null));
        }
        if (this.pane != null) {
            try {
                int previous = this.pane.getCaretPosition();
                if (this.root != null && level == 0) {
                    this.pane.setText(this.root.printEntry());
                } else if (this.parent != null) {
                    this.parent.showMessage("Error", "Pairs of brackets don't match.\nTree not updated.", 0);
                    this.pane.setText(this.bigBuff);
                }
                ((SyntaxDocument)this.pane.getDocument()).clearUndos();
                this.parent.scrollToEnd();
                this.pane.setCaretPosition(previous);
            }
            catch (Exception e) {
                this.pane.setCaretPosition(0);
            }
        }
        if (this.parent == null) return;
        this.parent.loadScrollPositions();
        this.parent.stopLoading();
        this.parent.disableButton();
        if (level == 0) {
            this.parent.setParsedOk(true);
            switch (this.postAction) {
                case 0: {
                    return;
                }
                case 1: {
                    if (level != 0) return;
                    this.parent.callerNewPatch();
                    return;
                }
                case 2: {
                    if (level != 0) return;
                    this.parent.callerOpenPatch();
                    return;
                }
                default: {
                    return;
                }
            }
        } else {
            this.parent.enableTreeButton();
        }
    }

    public void setTree(JTree tree) {
        this.tree = tree;
    }

    public void setPane(JEditorPane pane) {
        this.pane = pane;
    }
}

