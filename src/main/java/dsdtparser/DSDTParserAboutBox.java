/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  org.jdesktop.application.Action
 *  org.jdesktop.application.Application
 *  org.jdesktop.application.ApplicationActionMap
 *  org.jdesktop.application.ApplicationContext
 *  org.jdesktop.application.ResourceMap
 */
package dsdtparser;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.awt.*;

public class DSDTParserAboutBox
extends JDialog {
    private JButton closeButton;

    public DSDTParserAboutBox(Frame parent) {
        super(parent);
        this.initComponents();
        this.getRootPane().setDefaultButton(this.closeButton);
    }

    @Action
    public void closeAboutBox() {
        this.dispose();
    }

    private void initComponents() {
        this.closeButton = new JButton();
        JLabel appTitleLabel = new JLabel();
        JLabel versionLabel = new JLabel();
        JLabel appVersionLabel = new JLabel();
        JLabel vendorLabel = new JLabel();
        JLabel appVendorLabel = new JLabel();
        JLabel homepageLabel = new JLabel();
        JLabel appHomepageLabel = new JLabel();
        JLabel appDescLabel = new JLabel();
        this.setDefaultCloseOperation(2);
        ResourceMap resourceMap = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getResourceMap((Class)DSDTParserAboutBox.class);
        this.setTitle(resourceMap.getString("title", new Object[0]));
        this.setModal(true);
        this.setName("aboutBox");
        this.setResizable(false);
        ApplicationActionMap actionMap = ((DSDTParserApp)Application.getInstance((Class)DSDTParserApp.class)).getContext().getActionMap((Class)DSDTParserAboutBox.class, (Object)this);
        this.closeButton.setAction(actionMap.get("closeAboutBox"));
        this.closeButton.setName("closeButton");
        appTitleLabel.setFont(appTitleLabel.getFont().deriveFont(appTitleLabel.getFont().getStyle() | 1, appTitleLabel.getFont().getSize() + 4));
        appTitleLabel.setText(resourceMap.getString("Application.title", new Object[0]));
        appTitleLabel.setName("appTitleLabel");
        versionLabel.setFont(versionLabel.getFont().deriveFont(versionLabel.getFont().getStyle() | 1));
        versionLabel.setText(resourceMap.getString("versionLabel.text", new Object[0]));
        versionLabel.setName("versionLabel");
        appVersionLabel.setText(resourceMap.getString("Application.version", new Object[0]));
        appVersionLabel.setName("appVersionLabel");
        vendorLabel.setFont(vendorLabel.getFont().deriveFont(vendorLabel.getFont().getStyle() | 1));
        vendorLabel.setText(resourceMap.getString("vendorLabel.text", new Object[0]));
        vendorLabel.setName("vendorLabel");
        appVendorLabel.setText(resourceMap.getString("Application.vendor", new Object[0]));
        appVendorLabel.setName("appVendorLabel");
        homepageLabel.setFont(homepageLabel.getFont().deriveFont(homepageLabel.getFont().getStyle() | 1));
        homepageLabel.setText(resourceMap.getString("homepageLabel.text", new Object[0]));
        homepageLabel.setName("homepageLabel");
        appHomepageLabel.setText(resourceMap.getString("Application.homepage", new Object[0]));
        appHomepageLabel.setName("appHomepageLabel");
        appDescLabel.setText(resourceMap.getString("appDescLabel.text", new Object[0]));
        appDescLabel.setName("appDescLabel");
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(versionLabel).addComponent(vendorLabel).addComponent(homepageLabel)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(appVersionLabel).addComponent(appVendorLabel).addComponent(appHomepageLabel))).addComponent(appTitleLabel, GroupLayout.Alignment.LEADING).addComponent(appDescLabel, GroupLayout.Alignment.LEADING, -1, 439, 32767).addComponent(this.closeButton)).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(appTitleLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(appDescLabel).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(versionLabel).addComponent(appVersionLabel)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(vendorLabel).addComponent(appVendorLabel)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(homepageLabel).addComponent(appHomepageLabel)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767).addComponent(this.closeButton).addContainerGap()));
        this.pack();
    }
}

