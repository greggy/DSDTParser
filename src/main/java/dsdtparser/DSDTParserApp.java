/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.jdesktop.application.Application
 *  org.jdesktop.application.SingleFrameApplication
 *  org.jdesktop.application.View
 */
package dsdtparser;

import dsdtparser.AutoPatcherSimple;
import dsdtparser.DSDTParserView;
import java.awt.Window;
import java.io.File;
import javax.swing.JFrame;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.View;

import java.util.logging.*;

public class DSDTParserApp extends SingleFrameApplication {
    public static String filename = "";
    public DSDTParserView view;
    public AutoPatcherSimple ap;
    private boolean isAp = false;

    protected void startup() {
        if (this.isAp) {
            this.ap = new AutoPatcherSimple();
        } else {
            this.view = new DSDTParserView(this);
        }
        if (this.isAp) {
            DSDTParserApp.getApplication().show((JFrame)this.ap);
        } else {
            DSDTParserApp.getApplication().show((View)this.view);
        }
    }

    protected void configureWindow(Window root) {
    }

    public static DSDTParserApp getApplication() {
        return (DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            filename = args[0];
        }
//        System.setProperty("apple.laf.useScreenMenuBar", "true");
        File aml = new File("dsdt.aml");
        if (aml.exists()) {
            aml.delete();
        }
        DSDTParserApp.launch((Class)DSDTParserApp.class, (String[])args);
    }

/*    public static void main(String[] args) {
        Logger logger = Logger.getLogger(DSDTParserApp.class.getName());
        logger.log(Level.INFO, "Application started");
        System.out.println("I'm the main project");
        logger.log(Level.INFO, "Application finished");
    }*/
}

