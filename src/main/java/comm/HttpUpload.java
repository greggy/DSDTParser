/*
 * Decompiled with CFR 0_115.
 */
package comm;

import dsdtparser.AutoPatcherNotPresent;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class HttpUpload
implements Runnable {
    private AutoPatcherNotPresent parent;
    private String data;

    public HttpUpload(AutoPatcherNotPresent parent, String data) {
        this.parent = parent;
        this.data = data;
    }

    public void run() {
        Socket socket = null;
        OutputStreamWriter out = null;
        BufferedReader in = null;
        try {
            String line;
            this.parent.disableSendButton();
            String request = this.getFormattedPostRequest("/cgi-bin/autopatcher.cgi", "olarila.com", this.data, null, "Mozilla/5.0 (Windows; U; Windows NT 5.1; pt-BR; rv:1.8.1.1) Gecko/20061204 Firefox/2.0.0.1");
            socket = new Socket("olarila.com", 80);
            out = new OutputStreamWriter(socket.getOutputStream());
            String[] tmp = request.split("\n");
            for (int i = 0; i < tmp.length; ++i) {
                out.write(tmp[i] + "\n");
                out.flush();
            }
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while ((line = in.readLine()) != null && line.length() > 0) {
            }
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                if (!line.equalsIgnoreCase("saved")) continue;
                JOptionPane.showMessageDialog(this.parent, "Data sent. Thank you\nfor contributing.");
            }
        }
        catch (Exception e) {
            // empty catch block
        }
    }

    private String getFormattedPostRequest(String URI, String host, String encodedData, String referer, String agent) {
        return "POST " + URI + " HTTP/1.1\n" + "Host: " + host + "\n" + "User-Agent: " + agent + "\n" + "Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5\n" + "Accept-Language: pt-br,pt;q=0.8,en-us;q=0.5,en;q=0.3\n" + "Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\n" + (referer != null ? new StringBuilder().append("Referer: ").append(referer).append("\n").toString() : "") + "Connection: close\n" + "Cache-Control: max-age=0\n" + "Content-Type: application/x-www-form-urlencoded\n" + "Content-Length: " + encodedData.length() + "\n\n" + encodedData + "\n\n";
    }

    public static void main(String[] args) {
        HttpUpload h = new HttpUpload(null, "some\ntext\n");
        h.run();
    }
}

