package gui.tabs;

import crypto.*;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;

/**
 * Created by filipgulan on 09/05/2017.
 */
public class DigitalStampTab extends JPanel {

    private JTextField publicKeyField = new JTextField(20);
    private JTextField privateKeyField = new JTextField(20);
    private JTextField inputFileField = new JTextField(20);
    private JTextField inputDigestField = new JTextField(20);
    private JTextField outputFileField = new JTextField(20);

    private Path publicKeyPath;
    private Path privateKeyPath;
    private Path inputFilePath;
    private Path inputDigestPath;
    private Path outputFilePath;

    public DigitalStampTab() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        initGUI();
    }

    private void processStamp() throws Exception {
        if (publicKeyPath == null || privateKeyPath == null || inputFilePath == null
                || inputDigestPath == null || outputFilePath == null) {
            return;
        }

        byte[] env = Files.readAllBytes(inputFilePath);
        List<String> lines = Files.readAllLines(inputFilePath);
        byte[] result = DigitalEnvelope.openEnvelope(lines.get(1), lines.get(3), privateKeyPath);

        List<String> digestLines = Files.readAllLines(inputDigestPath);
        byte[] digestData = Base64.getDecoder().decode(digestLines.get(0));
        RSA rsa = new RSA();
        RSAKeysGenerator rsaGenerator = new RSAKeysGenerator(2048);
        byte[] decryptedData = rsa.decrypt(digestData, rsaGenerator.loadPublicKey(publicKeyPath));
        String digest = new String(decryptedData, StandardCharsets.UTF_8);

        if (SHA1My.digest(env).equals(digest)) {
            JOptionPane.showMessageDialog(null, "Sažetak uspješno provjeren!");
        } else {
            JOptionPane.showMessageDialog(null, "Dobiveni sažetak ne odgovara izračunatom!!!");
        }
        Files.write(outputFilePath, result, StandardOpenOption.CREATE);
    }

    private void initGUI() {
        JPanel gridPanel = new JPanel(new GridLayout(0, 3));

        gridPanel.add(new JLabel("Omotnica"));
        gridPanel.add(inputFileField);
        JButton inputFileButton = new JButton("Odaberi");
        inputFileButton.addActionListener(e -> {
            didSelectChooseInputFile();
        });
        gridPanel.add(inputFileButton);

        gridPanel.add(new JLabel("Potpis"));
        gridPanel.add(inputDigestField);
        JButton digestFileButton = new JButton("Odaberi");
        digestFileButton.addActionListener(e -> {
            didSelectChooseDigestFile();
        });
        gridPanel.add(digestFileButton);

        gridPanel.add(new JLabel("Javni kljuc posiljatelja"));
        gridPanel.add(publicKeyField);
        JButton inputKeyButton = new JButton("Odaberi");
        inputKeyButton.addActionListener(e -> {
            didSelectChoosePublicKey();
        });
        gridPanel.add(inputKeyButton);

        gridPanel.add(new JLabel("Tajni kljuc primatelja"));
        gridPanel.add(privateKeyField);
        JButton privateKeyButton = new JButton("Odaberi");
        privateKeyButton.addActionListener(e -> {
            didSelectChoosePrivateKey();
        });
        gridPanel.add(privateKeyButton);

        gridPanel.add(new JLabel("Izlazna datoteka"));
        gridPanel.add(outputFileField);
        JButton outputFileButton = new JButton("Odaberi");
        outputFileButton.addActionListener(e -> {
            didSelectChooseOutputFile();
        });
        gridPanel.add(outputFileButton);

        JButton openDigitalStamp = new JButton("Otvori digitalni pecat");
        openDigitalStamp.addActionListener(e -> {
            try {
                processStamp();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        
        add(gridPanel);
        add(openDigitalStamp);
    }

    private void didSelectChooseOutputFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Odaberi izlaznu datoteku");
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            outputFilePath = chooser.getSelectedFile().toPath();
            outputFileField.setText(chooser.getSelectedFile().toPath().toString());
        } else {
            outputFilePath = null;
            outputFileField.setText("");
        }
    }

    private void didSelectChooseDigestFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Odaberi tajni kljuc primatelja");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            inputDigestPath = chooser.getSelectedFile().toPath();
            inputDigestField.setText(chooser.getSelectedFile().toPath().toString());
        } else {
            inputDigestPath = null;
            inputDigestField.setText("");
        }
    }

    private void didSelectChoosePrivateKey() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Odaberi tajni kljuc primatelja");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            privateKeyPath = chooser.getSelectedFile().toPath();
            privateKeyField.setText(chooser.getSelectedFile().toPath().toString());
        } else {
            privateKeyPath = null;
            privateKeyField.setText("");
        }
    }

    private void didSelectChoosePublicKey() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Odaberi javni kljuc posiljatelja");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            publicKeyPath = chooser.getSelectedFile().toPath();
            publicKeyField.setText(chooser.getSelectedFile().toPath().toString());
        } else {
            publicKeyPath = null;
            publicKeyField.setText("");
        }
    }

    private void didSelectChooseInputFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Odaberi ulaznu datoteku");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            inputFilePath = chooser.getSelectedFile().toPath();
            inputFileField.setText(chooser.getSelectedFile().toPath().toString());
        } else {
            inputFilePath = null;
            inputFileField.setText("");
        }
    }
}
