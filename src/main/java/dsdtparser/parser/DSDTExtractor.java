/*
 * Decompiled with CFR 0_115.
 */
package dsdtparser.parser;

import dsdtparser.DSDTParserView;

import javax.swing.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DSDTExtractor
implements Runnable {
    private static final boolean DEBUG = true;
    private DSDTParserView parent;

    public DSDTExtractor(DSDTParserView parent) {
        this.parent = parent;
    }

    public void run() {
        String s;
        File ex = new File("dsdt.dsl");
        if (ex.exists()) {
            ex.delete();
        }
        if ((s = System.getProperty("os.name").toLowerCase()).indexOf("windows") != -1) {
            this.winDSDT();
        } else if (s.indexOf("mac") != -1) {
            this.macDSDT();
        } else if (s.indexOf("linux") != -1) {
            this.linuxDSDT();
        }
    }

    private void winDSDT() {
        String line = null;
        try {
            File f;
            Process p = Runtime.getRuntime().exec("reg query HKLM\\HARDWARE\\ACPI\\DSDT /s");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            FileOutputStream out = new FileOutputStream("dsdt.aml");
            Pattern pat = Pattern.compile("REG_BINARY\\s*([^ ]+)$");
            while ((line = input.readLine()) != null) {
                Matcher m = pat.matcher(line);
                if (!m.find()) continue;
                String buffer = m.group(1);
                System.out.println("DSDT found. Size=" + buffer.length());
                byte[] binary = new byte[buffer.length() / 2];
                for (int i = 0; i < buffer.length(); i += 2) {
                    binary[i / 2] = (byte)((Character.digit(buffer.charAt(i), 16) << 4) + Character.digit(buffer.charAt(i + 1), 16));
                }
                out.write(binary);
                out.close();
            }
            input.close();
            p.waitFor();
            if (this.parent != null && (f = new File("dsdt.aml")).exists()) {
                Thread t = new Thread(new AMLDecompiler(this.parent, f));
                t.start();
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void macDSDT() {
        String line = null;
        try {
            File f;
            String buffer = "";
            Process p = Runtime.getRuntime().exec("ioreg -lw0");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            Pattern pat = Pattern.compile("\"DSDT\\.?\\d?\"=<([^>]*)>");
            FileOutputStream out = new FileOutputStream("dsdt.aml");
            while ((line = input.readLine()) != null) {
                Matcher m = pat.matcher(line);
                if (!m.find()) continue;
                String buff = m.group(1);
                System.out.println("DSDT found. Size=" + buff.length());
                byte[] binary = new byte[buff.length() / 2];
                for (int i = 0; i < buff.length(); i += 2) {
                    binary[i / 2] = (byte)((Character.digit(buff.charAt(i), 16) << 4) + Character.digit(buff.charAt(i + 1), 16));
                }
                out.write(binary);
                out.close();
            }
            input.close();
            p.waitFor();
            if (this.parent != null && (f = new File("dsdt.aml")).exists()) {
                Thread t = new Thread(new AMLDecompiler(this.parent, f));
                t.start();
            }
        }
        catch (InterruptedException ex) {
            Logger.getLogger(DSDTExtractor.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void linuxDSDT() {
        Object line = null;
        try {
            File fl;
            String fff = "/proc/acpi/dsdt";
            File f = new File(fff);
            if (!f.exists()) {
                fff = "/sys/firmware/acpi/tables/DSDT";
                f = new File(fff);
                if (!f.exists()) {
                    DSDTExtractor.showMessage("Error", "The dsdt file does not seem to exist on /proc or /sys.", 0);
                    return;
                }
                if (!f.canRead()) {
                    DSDTExtractor.showMessage("Error", "You must be running with root privileges in order to read the dsdt file.", 0);
                    return;
                }
            } else if (!f.canRead()) {
                DSDTExtractor.showMessage("Error", "You must be running with root privileges in order to read the dsdt file.", 0);
                return;
            }
            AMLCompiler.fileCopy(fff, "./dsdt.aml");
            if (this.parent != null && (fl = new File("dsdt.aml")).exists()) {
                System.out.println("Openening extracted DSDT: " + fl.getAbsolutePath());
                Thread t = new Thread(new AMLDecompiler(this.parent, fl));
                t.start();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void showMessage(String title, String msg, int type) {
        JOptionPane.showMessageDialog(null, msg, title, type);
    }

    public static void main(String[] args) {
        DSDTExtractor a = new DSDTExtractor(null);
        a.run();
    }
}

