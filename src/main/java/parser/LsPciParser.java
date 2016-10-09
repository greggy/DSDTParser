/*
 * Decompiled with CFR 0_115.
 */
package parser;

import dsdtparser.AutoPatcherNotPresent;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LsPciParser
implements Runnable {
    public static String LSPCI_COMMAND = "lspci -mmnn";
    private JTable tableKexts;
    private JTable tableDevices;
    private Pattern entryPattern = Pattern.compile("\\[([0-9A-Fa-f]{4})\\]\".*\\[([0-9A-Fa-f]{4})\\]\".*\\[([0-9A-Fa-f]{4})\\]\".*\\[[0-9A-Fa-f]{4}\\]\".*\\[[0-9A-Fa-f]{4}\\]\"");
    private AutoPatcherNotPresent parent;

    public LsPciParser(AutoPatcherNotPresent kv) {
        this.parent = kv;
    }

    private LsPciParser() {
    }

    public void run() {
        this.parseRegistry();
        this.parent.loadFinished();
    }

    public void parseRegistry() {
        String block = "";
        try {
            String line;
            Process p = Runtime.getRuntime().exec(LSPCI_COMMAND);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                Matcher mat = this.entryPattern.matcher(line);
                if (!mat.find()) continue;
                this.parent.appendLine(mat.group(3) + "/" + mat.group(2) + "/" + mat.group(1));
            }
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }
}

