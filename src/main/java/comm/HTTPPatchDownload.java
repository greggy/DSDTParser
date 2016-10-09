/*
 * Decompiled with CFR 0_115.
 */
package comm;

import dsdtparser.AutoPatcher2;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URLEncoder;

public class HTTPPatchDownload
implements Runnable {
    private AutoPatcher2 parent;
    private String patch;
    private String vendor;
    private String model;

    public HTTPPatchDownload(AutoPatcher2 parent, String vendor, String model) {
        this.parent = parent;
        this.vendor = URLEncoder.encode(vendor).replaceAll("\\+", "%20");
        this.model = URLEncoder.encode(model).replaceAll("\\+", "%20");
        this.patch = "";
    }

    public void run() {
        Socket socket = null;
        OutputStreamWriter out = null;
        BufferedReader in = null;
        this.parent.appendLog("Trying to download patch...");
        this.parent.startPatching();
        try {
            String line;
            String request = this.getFormattedGetRequest("/autopatcher/" + this.vendor + "/" + this.model + "/patch.txt", "olarila.com", null, "Mozilla/5.0 (Windows; U; Windows NT 5.1; pt-BR; rv:1.8.1.1) Gecko/20061204 Firefox/2.0.0.1");
            socket = new Socket("olarila.com", 80);
            out = new OutputStreamWriter(socket.getOutputStream());
            String[] tmp = request.split("\n");
            out.write(request);
            out.flush();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String script = "";
            while ((line = in.readLine()) != null && line.length() > 0) {
            }
            while ((line = in.readLine()) != null) {
                script = script + line + "\n";
            }
            this.parent.appendLog("Patch file successfully downloaded.");
            this.parent.enableNotPresentButton();
            this.parent.patch(script);
        }
        catch (Exception e) {
            e.printStackTrace();
            this.parent.showMessage("Error", "Cannot download patch file.", 1);
            this.parent.appendLog("Failed to download patch file.");
            this.parent.endPatching();
        }
    }

    private String getFormattedGetRequest(String URI, String host, String referer, String agent) {
        return "GET " + URI + " HTTP/1.1\n" + "Host: " + host + "\n" + "User-Agent: " + agent + "\n" + "Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5\n" + "Accept-Language: pt-br,pt;q=0.8,en-us;q=0.5,en;q=0.3\n" + "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\n" + (referer != null ? new StringBuilder().append("Referer: ").append(referer).append("\n").toString() : "") + "Connection: close\n" + "Cache-Control: max-age=0\n" + "\n\n";
    }

    public static void main(String[] args) {
        HTTPListRequest h = new HTTPListRequest(null);
        h.run();
    }
}

