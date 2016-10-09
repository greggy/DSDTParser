/*
 * Decompiled with CFR 0_115.
 */
package comm;

import dsdtparser.AutoPatcher2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTTPListRequest
implements Runnable {
    private AutoPatcher2 parent;
    private ArrayList[] listas;
    private String[] vendors;

    public HTTPListRequest(AutoPatcher2 parent) {
        this.parent = parent;
    }

    public String getVendor(int index) {
        return this.vendors[index];
    }

    public ArrayList getArrayList(int i) {
        try {
            return this.listas[i];
        }
        catch (Exception e) {
            return null;
        }
    }

    public void run() {
        Socket socket = null;
        OutputStreamWriter out = null;
        BufferedReader in = null;
        Pattern data = Pattern.compile("^(.*) :: (.*)$");
        this.parent.appendLog("Trying to download available patches...");
        try {
            String line;
            String request = this.getFormattedGetRequest("/autopatcher/listmobo.pl", "olarila.com", null, "Mozilla/5.0 (Windows; U; Windows NT 5.1; pt-BR; rv:1.8.1.1) Gecko/20061204 Firefox/2.0.0.1");
            socket = new Socket("olarila.com", 80);
            out = new OutputStreamWriter(socket.getOutputStream());
            String[] tmp = request.split("\n");
            out.write(request);
            out.flush();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            boolean dataCreated = false;
            int vendors = 0;
            String lastVendor = ".";
            int index = -1;
            while ((line = in.readLine()) != null) {
                Matcher m = data.matcher(line);
                if (!m.matches()) continue;
                if (m.group(1).equals(".")) {
                    ++vendors;
                }
                if (!dataCreated && !m.group(1).equals(".")) {
                    this.vendors = new String[vendors];
                    this.listas = new ArrayList[vendors];
                    dataCreated = true;
                }
                if (dataCreated) {
                    if (lastVendor.equals(m.group(1))) {
                        this.vendors[index] = m.group(1);
                        if (this.listas[index] == null) {
                            this.listas[index] = new ArrayList();
                        }
                        this.listas[index].add(m.group(2));
                    } else {
                        this.vendors[++index] = m.group(1);
                        if (this.listas[index] == null) {
                            this.listas[index] = new ArrayList();
                        }
                        this.listas[index].add(m.group(2));
                    }
                }
                lastVendor = m.group(1);
            }
            this.parent.setVendorList(this.vendors);
            this.parent.appendLog("Data successfully downloaded.");
            this.parent.enableNotPresentButton();
        }
        catch (Exception e) {
            e.printStackTrace();
            this.parent.showMessage("Error", "Cannot access server to retrieve list datas.\nCheck your internet connection and reopen the app.", 1);
            this.parent.appendLog("Failed to download data.");
        }
    }

    private String getFormattedGetRequest(String URI, String host, String referer, String agent) {
        return "GET " + URI + " HTTP/1.0\n" + "Host: " + host + "\n" + "User-Agent: " + agent + "\n" + "Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5\n" + "Accept-Language: pt-br,pt;q=0.8,en-us;q=0.5,en;q=0.3\n" + "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\n" + (referer != null ? new StringBuilder().append("Referer: ").append(referer).append("\n").toString() : "") + "Connection: close\n" + "\n\n";
    }

    public static void main(String[] args) {
        HTTPListRequest h = new HTTPListRequest(null);
        h.run();
    }
}

