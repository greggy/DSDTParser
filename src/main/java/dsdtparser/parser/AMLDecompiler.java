/*
 * Decompiled with CFR 0_115.
 */
package dsdtparser.parser;

import dsdtparser.DSDTParserView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AMLDecompiler
implements Runnable {
    private static final boolean DEBUG = false;
    private DSDTParserView parent;
    private File file;

    public AMLDecompiler(DSDTParserView parent, File file) {
        this.parent = parent;
        this.file = file;
    }

    public void run() {
        String line = null;
        try {
            File existe = new File("dsdt.dsl");
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
            Process p = Runtime.getRuntime().exec(new String[]{command, "-p", "dsdt", "-d", this.file.getAbsolutePath()});
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = input.readLine()) != null) {
            }
            input.close();
            p.waitFor();
            if (this.parent != null) {
                File f = new File("dsdt.dsl");
                if (f.exists()) {
                    this.parent.setFile(f);
                } else {
                    System.out.println("File not found: " + f.getAbsolutePath());
                }
            } else {
                System.out.println("Parent is null!");
            }
        }
        catch (InterruptedException ex) {
            Logger.getLogger(AMLDecompiler.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

