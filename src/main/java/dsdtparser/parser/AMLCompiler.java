/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.jdesktop.application.Application
 *  org.jdesktop.application.ApplicationContext
 *  org.jdesktop.application.LocalStorage
 */
package dsdtparser.parser;

import dsdtparser.DSDTParserApp;
import dsdtparser.DSDTParserView;
import dsdtparser.LineErrorsWindow;
import dsdtparser.fixes.AutoFix;
import org.jdesktop.application.Application;
import org.jdesktop.application.LocalStorage;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AMLCompiler
implements Runnable {
    public static final int COMPILE = 0;
    public static final int SAVE = 1;
    public static final int SIMPLE_COMPILE_AND_FIX = 2;
    public static final int SAVE_TO_DESTINY = 3;
    private DSDTParserView parent;
    private ArrayList<CompilerError> errors;
    private int mode;
    Pattern errorLine = Pattern.compile("dsdt\\.dsl\\s+(\\d+):");
    Pattern secondLine = Pattern.compile("^([^ ]+)\\s+\\d+ -\\s+([^ ].*)$");
    Pattern endLine = Pattern.compile("^Compilation complete. (.*)$");
    String label = "";
    private static final boolean DEBUG = true;
    private String destino = "";
    Pattern spaces = Pattern.compile("\\s\\s");

    public AMLCompiler(DSDTParserView parent, int mode) {
        this.parent = parent;
        this.mode = mode;
    }

    public String getLabel() {
        return this.label;
    }

    private String arrangeLine(String original) {
        String copy = original.replace('^', ' ');
        Matcher m = this.spaces.matcher(copy);
        while (m.find()) {
            copy = m.replaceAll(" ");
            m = this.spaces.matcher(copy);
        }
        return copy;
    }

    private void saveAMLToDisk() {
        this.saveAMLToDisk("");
    }

    private void saveAMLToDisk(String file) {
        File g = new File("dsdt.aml");
        if (!g.exists() && this.parent != null) {
            DSDTExtractor.showMessage("Error", "The file wasn't compiled successfully.", 0);
            return;
        }
        File f = null;
        f = this.parent != null ? DSDTParserView.promptForFile(this.parent.getFrame(), true, this.parent.getDefDir()) : ((f = new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop")).exists() && f.isDirectory() ? new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop" + System.getProperty("file.separator") + "dsdt.aml") : new File(System.getProperty("user.home") + System.getProperty("file.separator") + "dsdt.aml"));
        if (f != null) {
            if (this.parent != null) {
                this.parent.setDefDir(f.getParentFile());
                try {
                    LocalStorage ls = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getLocalStorage();
                    ls.save((Object)this.parent.getDefDir().getAbsolutePath(), "defdir");
                }
                catch (IOException e) {
                    // empty catch block
                }
            }
            Object line = null;
            if (file.length() > 0) {
                AMLCompiler.fileCopy(g.getAbsolutePath(), file);
            } else {
                AMLCompiler.fileCopy(g.getAbsolutePath(), f.getAbsolutePath());
            }
        }
    }

    public static void fileCopy(String srFile, String dtFile) {
        try {
            int len;
            File f1 = new File(srFile);
            File f2 = new File(dtFile);
            FileInputStream in = new FileInputStream(f1);
            FileOutputStream out = new FileOutputStream(f2);
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            System.out.println("File copied.");
        }
        catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + " in the specified directory.");
            System.exit(0);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public int compile() {
        String line = null;
        try {
            Matcher m;
            File existe = new File("dsdt.aml");
            if (existe.exists()) {
                existe.delete();
            }
            String command = "";
            String s = System.getProperty("os.name").toLowerCase();
            if (s.indexOf("windows") != -1) {
                command = "iasl.exe";
            } else if (s.indexOf("mac") != -1) {
                command = "./iasl";
            } else if (s.indexOf("linux") != -1) {
                command = "./iasl-linux";
            }
            File file = new File("dsdt.dsl");
            Process p = Runtime.getRuntime().exec(new String[]{command, "-p", "dsdt", file.getAbsolutePath()});
            this.errors = new ArrayList();
            int number = 0;
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = input.readLine()) != null) {
                System.out.println(line);
                m = this.errorLine.matcher(line);
                if (m.find()) {
                    number = Integer.parseInt(m.group(1));
                    continue;
                }
                m = this.secondLine.matcher(line);
                if (!m.matches()) continue;
                CompilerError ce = new CompilerError(number, m.group(1), this.arrangeLine(m.group(2)));
                this.getErrors().add(ce);
            }
            input.close();
            input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                System.out.println(line);
                m = this.endLine.matcher(line);
                if (!m.matches()) continue;
                this.label = m.group(1);
            }
            input.close();
            p.waitFor();
            ArrayList<CompilerError> temp = this.getErrorsOnly();
            if (temp.size() == 0 && this.parent != null) {
                this.parent.toggleSaveAML(true);
            } else if (this.parent != null) {
                this.parent.toggleSaveAML(false);
            }
            return p.exitValue();
        }
        catch (InterruptedException ex) {
            Logger.getLogger(AMLDecompiler.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    @Override
    public void run() {
        switch (this.mode) {
            case 1: {
                this.saveAMLToDisk();
                break;
            }
            case 0: {
                int ret = this.compile();
                LineErrorsWindow lew = new LineErrorsWindow(this.parent, this.getErrors());
                lew.setLabelText(this.label);
                lew.setVisible(true);
                break;
            }
            case 2: {
                this.compile();
                if (this.errors.size() == 0) {
                    System.out.println("DSDT compiled with no errors");
                }
                for (int i = this.errors.size() - 1; i >= 0; --i) {
                    CompilerError ce = this.errors.get(i);
                    if (!AutoFix.isKnownError(ce.getMensagem())) continue;
                    AutoFix.fix(this.parent, ce.getMensagem(), ce.getLinha());
                }
                break;
            }
            case 3: {
                this.saveAMLToDisk(this.destino);
            }
        }
    }

    public ArrayList<CompilerError> getErrors() {
        return this.errors;
    }

    public ArrayList<CompilerError> getErrorsOnly() {
        ArrayList<CompilerError> soerros = new ArrayList<CompilerError>();
        for (int i = 0; i < this.errors.size(); ++i) {
            if (!this.errors.get(i).getTipo().equalsIgnoreCase("error")) continue;
            soerros.add(this.errors.get(i));
        }
        return soerros;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }
}

