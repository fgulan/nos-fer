package gui;

import gui.tabs.DigitalEnvelopeTab;
import gui.tabs.DigitalSignatureTab;
import gui.tabs.DigitalStampTab;
import gui.tabs.SymmetricChiperTab;

import javax.swing.*;
import java.awt.*;

/**
 * Created by filipgulan on 06/05/2017.
 */
public class NOSTabbedPane extends JPanel {

    public NOSTabbedPane() {
        super(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Simetrično kriptiranje", new SymmetricChiperTab());
        tabbedPane.addTab("Digitalna omotnica", new DigitalEnvelopeTab());
        tabbedPane.addTab("Digitalni potpis", new DigitalSignatureTab());
        tabbedPane.addTab("Digitalni pečat", new DigitalStampTab());

        add(tabbedPane);
    }
}
