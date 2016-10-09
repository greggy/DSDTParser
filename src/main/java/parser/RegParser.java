/*
 * Decompiled with CFR 0_115.
 */
package parser;

import dsdtparser.AutoPatcherNotPresent;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegParser
implements Runnable {
    public static String IOREG_COMMAND = "reg query hklm\\system\\currentcontrolset\\enum\\pci /s";
    private JTable tableKexts;
    private JTable tableDevices;
    private Pattern entryPattern = Pattern.compile("^\\s*HardwareID\\s*.*VEN_(....)&DEV_(....)&CC_(....)(?:\\\\0\\\\0)?$");
    private AutoPatcherNotPresent parent;

    public RegParser(AutoPatcherNotPresent kv) {
        this.parent = kv;
    }

    private RegParser() {
    }

    public void run() {
        this.parseRegistry();
        this.parent.loadFinished();
    }

    public void parseRegistry() {
        String block = "";
        try {
            String line;
            URL lURL = this.getClass().getResource("/search.png");
            Process p = Runtime.getRuntime().exec(IOREG_COMMAND);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                Matcher mat = this.entryPattern.matcher(line);
                if (!mat.matches()) continue;
                this.parent.appendLine(mat.group(2) + "/" + mat.group(1) + "/" + mat.group(3));
            }
            input.close();
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    private String join(String divisor, String[] array) {
        if (array.length == 1) {
            return array[0];
        }
        String retorno = "";
        for (int i = 0; i < array.length - 1; ++i) {
            retorno = retorno.concat(array[i] + divisor);
        }
        retorno = retorno.concat(array[array.length - 1]);
        return retorno;
    }

    private String inverte(String id) {
        Pattern p = Pattern.compile("^(..)(..)$");
        Matcher m = p.matcher(id);
        if (m.find()) {
            return m.group(2) + m.group(1);
        }
        return id;
    }

    public static void main(String[] args) {
        RegParser iop = new RegParser();
        iop.parseRegistry();
    }

    public void setKextsTable(JTable table) {
        this.tableKexts = table;
    }

    public void setDevicesTable(JTable table) {
        this.tableDevices = table;
    }
}

