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

public class IORegParser
implements Runnable {
    public static String IOREG_COMMAND = "ioreg -l -w0 -p IODeviceTree";
    private String data = "";
    private int datalevel;
    private AutoPatcherNotPresent parent;
    private Pattern bundle = Pattern.compile("\"CFBundleIdentifier\" = \"([^\"]+)\"");
    private Pattern device = Pattern.compile("\"device-id\" = <(....)0000>");
    private Pattern vendor = Pattern.compile("\"vendor-id\" = <(....)0000>");
    private Pattern pciclass = Pattern.compile("\"pciclass,(....)..\"");
    private Pattern iopciclassmatch = Pattern.compile("\"IOPCIClassMatch\" = \"0x(....)[^\"]+\"");
    private JTable tableKexts;
    private JTable tableDevices;

    public IORegParser(AutoPatcherNotPresent kv) {
        this.parent = kv;
    }

    public void run() {
        this.parseKextDevice();
        this.parent.loadFinished();
    }

    public void parseKextDevice() {
        String block = "";
        try {
            String line;
            Pattern startBlock = Pattern.compile("^\\s*(\\s*\\|\\s*)*\\}\\s*$");
            Pattern endBlock = Pattern.compile("^\\s*(\\s*\\|\\s*)*\\{\\s*$");
            Process p = Runtime.getRuntime().exec(IOREG_COMMAND);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                Matcher mat = startBlock.matcher(line);
                if (mat.find() && block.length() > 0) {
                    if (this.data.length() > 0 && this.getLevel(block) < this.datalevel) {
                        this.data = "";
                        this.datalevel = 0;
                    }
                    this.parseBlock(block);
                    continue;
                }
                mat = endBlock.matcher(line);
                if (mat.find()) {
                    block = "";
                    continue;
                }
                block = block.concat(line.concat("\n"));
            }
            input.close();
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

    private void parseBlock(String block) {
        Matcher m = this.device.matcher(block);
        if (m.find()) {
            String[] buf = new String[3];
            buf[0] = this.inverte(m.group(1));
            buf[2] = "0000";
            buf[1] = "0000";
            m = this.vendor.matcher(block);
            if (m.find()) {
                buf[1] = this.inverte(m.group(1));
            }
            if ((m = this.pciclass.matcher(block)).find()) {
                buf[2] = m.group(1);
            }
            this.parent.appendLine(buf[0] + "/" + buf[1] + "/" + buf[2]);
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

    private int getLevel(String block) {
        int count = 0;
        try {
            String line = block.split("\n")[0];
            for (int i = 0; i < line.length(); ++i) {
                if (line.charAt(i) == ' ' || line.charAt(i) == '|') {
                    ++count;
                    continue;
                }
                return count;
            }
        }
        catch (Exception e) {
            // empty catch block
        }
        return count;
    }

    private String inverte(String id) {
        Pattern p = Pattern.compile("^(..)(..)$");
        Matcher m = p.matcher(id);
        if (m.find()) {
            return m.group(2) + m.group(1);
        }
        return id;
    }
}

