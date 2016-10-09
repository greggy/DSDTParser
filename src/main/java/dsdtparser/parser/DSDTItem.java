/*
 * Decompiled with CFR 0_115.
 */
package dsdtparser.parser;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DSDTItem {
    private int tipo;
    private int level;
    private DefaultMutableTreeNode node;
    private static final boolean DEBUG = false;
    private boolean deleted;
    public static final int DEFINITIONBLOCK = 0;
    public static final int SCOPE = 1;
    public static final int METHOD = 2;
    public static final int DEVICE = 3;
    public static final int PROCESSOR = 4;
    public static final int NAME = 5;
    public static final int THERMALZONE = 6;
    private ArrayList<DSDTItem> children = new ArrayList();
    private DSDTItem parent;
    private int rawLine;
    private String codeBlock;
    private String comment;
    private String[] parametros;
    private String rawAvanco;
    private String openB;
    private String closeB;

    public DSDTItem(int tipo, String[] parametros) {
        this(tipo, parametros, null);
    }

    public void delete() {
        this.deleted = true;
    }

    public DSDTItem(int tipo, String[] parametros, DSDTItem parent) {
        this.tipo = tipo;
        this.deleted = false;
        this.codeBlock = "";
        this.parametros = parametros;
        this.parent = parent;
        this.node = new DefaultMutableTreeNode(this);
        this.comment = "";
    }

    public void setRawAvanco(String novo) {
        this.rawAvanco = novo;
    }

    public String[] getParametro() {
        return this.parametros;
    }

    private String getParametros() {
        String ret = " (";
        for (int i = 0; i < this.parametros.length; ++i) {
            ret = ret + (i < this.parametros.length && i != 0 ? ", " : "") + this.parametros[i];
        }
        if (this.tipo != 5) {
            ret = ret + ")";
        }
        return ret;
    }

    public void setCodigo(String newcode) {
        if (this.openB == null || this.closeB == null) {
            this.getCodigo();
        }
        this.codeBlock = this.openB + "\n" + newcode;
        if (!newcode.endsWith("\n")) {
            this.codeBlock = this.codeBlock + "\n";
        }
        this.codeBlock = this.codeBlock + this.closeB + "\n";
    }

    public String getCodigo() {
        String[] tmp = this.codeBlock.split("\n");
        String retorno = "";
        try {
            this.openB = tmp[0];
            this.closeB = tmp[tmp.length - 1];
            for (int i = 1; i < tmp.length - 1; ++i) {
                retorno = retorno + tmp[i] + "\n";
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return retorno;
    }

    public void addChild(DSDTItem item) {
        this.children.add(item);
        this.node.add(item.getNode());
    }

    public int getChildCount() {
        return this.children.size();
    }

    public void insert(String code) {
        String[] tmp = this.codeBlock.split("\n");
        this.codeBlock = "";
        for (int i = 0; i < tmp.length - 1; ++i) {
            this.codeBlock = this.codeBlock + tmp[i] + "\n";
        }
        String avanco = this.getAvanco() + "    ";
        String[] tt = code.split("\n");
        for (int i2 = 0; i2 < tt.length; ++i2) {
            if (tt[i2].startsWith(" ")) {
                tt[i2] = tt[i2].substring(1);
            }
            this.codeBlock = this.codeBlock + avanco + tt[i2] + "\n";
        }
        this.codeBlock = this.codeBlock + tmp[tmp.length - 1];
    }

    public DSDTItem getChildAt(int pos) {
        return this.children.get(pos);
    }

    public String getTipo() {
        switch (this.tipo) {
            case 0: {
                return "DefinitionBlock";
            }
            case 1: {
                return "Scope";
            }
            case 2: {
                return "Method";
            }
            case 3: {
                return "Device";
            }
            case 4: {
                return "Processor";
            }
            case 5: {
                return "Name";
            }
            case 6: {
                return "ThermalZone";
            }
        }
        return null;
    }

    public int getTipoInt() {
        return this.tipo;
    }

    public String getShortString() {
        return "[" + this.getTipo() + this.getParametros() + "]";
    }

    public String printEditableEntry() {
        if (this.deleted) {
            return "";
        }
        String[] lines = this.codeBlock.split("\n");
        String lastLine = null;
        String root = "";
        root = root + lines[0] + "\n";
        int index = 0;
        for (int i = 1; i < lines.length - 1; ++i) {
            if (lines[i] != null && lines[i].matches("\\s*\\[child\\]\\s*") && index < this.getChildCount()) {
                root = root + "// reference to " + this.getChildAt(index++).getShortString() + " (do not edit this line)\n";
                continue;
            }
            if (lastLine == null || !lastLine.matches("^\\s*$") || !lines[i].matches("^\\s*$")) {
                root = root + lines[i] + "\n";
            }
            lastLine = lines[i];
        }
        root = root + lines[lines.length - 1] + "\n";
        return root;
    }

    public void setEditableString(String root) {
        this.codeBlock = "";
        String[] lines = root.split("\n");
        for (int i = 0; i < lines.length; ++i) {
            this.codeBlock = lines[i].startsWith("// reference to ") ? this.codeBlock + "[child]\n" : this.codeBlock + lines[i] + "\n";
        }
    }

    public String printEntry() {
        if (this.deleted) {
            return "";
        }
        if (this.codeBlock.length() > 0) {
            String[] lines = this.codeBlock.split("\n");
            String lastLine = null;
            String root = "";
            root = root + this.getHeader() + "\n";
            root = root + lines[0] + "\n";
            int index = 0;
            for (int i = 1; i < lines.length - 1; ++i) {
                if (lines[i] != null && lines[i].matches("\\s*\\[child\\]\\s*") && index < this.getChildCount()) {
                    root = root + this.getChildAt(index++).printEntry();
                    continue;
                }
                if (lastLine == null || !lastLine.matches("^\\s*$") || !lines[i].matches("^\\s*$")) {
                    root = root + lines[i] + "\n";
                }
                lastLine = lines[i];
            }
            root = root + lines[lines.length - 1] + "\n";
            return root;
        }
        return this.getHeader() + "\n";
    }

    public String toString() {
        return this.getTipo() + " " + this.parametros[0];
    }

    private String getAvanco() {
        String line;
        if (this.tipo == 5) {
            return this.rawAvanco;
        }
        String avanco = "";
        if (this.codeBlock.length() > 0 && (line = this.codeBlock.split("\n")[0]).contains("{")) {
            try {
                avanco = line.split("\\{")[0];
            }
            catch (ArrayIndexOutOfBoundsException e) {
                avanco = "";
            }
        }
        return avanco;
    }

    public int getCodeSize() {
        if (this.codeBlock != null) {
            return this.codeBlock.length();
        }
        return 0;
    }

    public void setRawCodigo(String codigo) {
        this.codeBlock = codigo;
    }

    public DSDTItem clone() {
        DSDTItem item = new DSDTItem(this.tipo, (String[])this.parametros.clone(), this.parent);
        item.setRawCodigo(new String(this.codeBlock.getBytes()));
        item.setChildren(this.children);
        return item;
    }

    public String getHeader() {
        return this.getAvanco() + this.getTipo() + this.getParametros() + this.comment;
    }

    public void appendLine(String line) {
        this.codeBlock = line.endsWith("\n") ? this.codeBlock + line : this.codeBlock + line + "\n";
    }

    public DSDTItem getParent() {
        return this.parent;
    }

    public void setParent(DSDTItem parent) {
        this.parent = parent;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public DefaultMutableTreeNode getNode() {
        return this.node;
    }

    public void setNode(DefaultMutableTreeNode node) {
        this.node = node;
    }

    public int getRawLine() {
        return this.rawLine;
    }

    public void setRawLine(int rawLine) {
        this.rawLine = rawLine;
    }

    public void setChildren(ArrayList<DSDTItem> children) {
        this.children = children;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

