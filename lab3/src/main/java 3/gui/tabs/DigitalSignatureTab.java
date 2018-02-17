package gui.tabs;

import crypto.RSA;
import crypto.RSAKeysGenerator;
import crypto.SHA1Internal;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;

/**
 * Created by filipgulan on 06/05/2017.
 */
public class DigitalSignatureTab extends JPanel {

    private JTextField inputFileField = new JTextField(20);
    private JTextField inputKeyField = new JTextField(20);
    private JTextField signatureOutputField = new JTextField(20);
    private JTextField outputKeyField = new JTextField(20);

    private Path inputFilePath;
    private Path inputKeyPath;
    private Path signatureOutputPath;
    private Path outputKeyPath;

    private String currentDigest;

    public DigitalSignatureTab() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        initGUI();
    }

    private void initGUI() {
        JPanel gridPanel = new JPanel(new GridLayout(0, 3));

        gridPanel.add(new JLabel("Ulazna datotoeka"));
        gridPanel.add(inputFileField);
        JButton inputFileButton = new JButton("Odaberi");
        inputFileButton.addActionListener(e -> {
            didSelectChooseInputFile();
        });
        gridPanel.add(inputFileButton);

        gridPanel.add(new JLabel("Tajni kljuc posiljatelja"));
        gridPanel.add(inputKeyField);
        JButton inputKeyButton = new JButton("Odaberi");
        inputKeyButton.addActionListener(e -> {
            didSelectChooseInputKey();
        });
        gridPanel.add(inputKeyButton);

        gridPanel.add(new JLabel("Digitalni potpis"));
        gridPanel.add(signatureOutputField);
        JButton envelopeButton = new JButton("Odaberi");
        envelopeButton.addActionListener(e -> {
            didSelectChooseSignature();
        });
        gridPanel.add(envelopeButton);

        JButton generateSignatureButton = new JButton("Generiraj digitalni potpis");
        generateSignatureButton.addActionListener(e -> {
            try {
                generateSignature();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        JPanel openPanel = new JPanel(new GridLayout(0, 3));
        openPanel.add(new JLabel("Javni kljuc posiljatelja"));
        openPanel.add(outputKeyField);
        JButton choosePublicButton = new JButton("Odaberi");
        choosePublicButton.addActionListener(e -> {
            didSelectChooseOutputKey();
        });
        openPanel.add(choosePublicButton);


        JButton checkSignatureButton = new JButton("Provjeri digitalni potpis");
        checkSignatureButton.addActionListener(e -> {
            try {
                checkSignature();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        add(gridPanel);
        add(generateSignatureButton);
        add(openPanel);
        add(checkSignatureButton);
    }

    private void checkSignature() throws Exception {
        if (currentDigest == null || outputKeyPath == null || signatureOutputPath == null) {
            return;
        }
        List<String> lines = Files.readAllLines(signatureOutputPath);
        byte[] data = Base64.getDecoder().decode(lines.get(0));
        RSA rsa = new RSA();
        RSAKeysGenerator rsaGenerator = new RSAKeysGenerator(2048);
        byte[] decryptedData = rsa.decrypt(data, rsaGenerator.loadPublicKey(outputKeyPath));
        String digest = new String(decryptedData, StandardCharsets.UTF_8);

        if (currentDigest.equals(digest)) {
            JOptionPane.showMessageDialog(null, "Sažetak uspješno provjeren!");
        } else {
            JOptionPane.showMessageDialog(null, "Dobiveni sažetak ne odgovara izračunatom!!!");
        }
    }

    private void generateSignature() throws Exception {
        if (inputFilePath == null || inputKeyPath == null || signatureOutputPath == null) {
            return;
        }

        byte[] input = Files.readAllBytes(inputFilePath);
        String digest = SHA1Internal.digest(input);
        currentDigest = digest;

        RSA rsa = new RSA();
        RSAKeysGenerator rsaGenerator = new RSAKeysGenerator(2048);
        byte[] byteDigest = digest.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedDigest = rsa.encrypt(byteDigest, rsaGenerator.loadPrivateKey(inputKeyPath));
        String base64Digest = new String(Base64.getEncoder().encode(encryptedDigest));
        ArrayList<String> lines = new ArrayList<>();
        lines.add(base64Digest);
        Files.write(signatureOutputPath, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
    }

    void didSelectChooseInputFile() {
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

    void didSelectChooseInputKey() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Odaberi tajni kljuc posiljatelja");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            inputKeyPath = chooser.getSelectedFile().toPath();
            inputKeyField.setText(chooser.getSelectedFile().toPath().toString());
        } else {
            inputKeyPath = null;
            inputKeyField.setText("");
        }
    }

    void didSelectChooseSignature() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Odaberi datoteku za potpis");
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            signatureOutputPath = chooser.getSelectedFile().toPath();
            signatureOutputField.setText(chooser.getSelectedFile().toPath().toString());
        } else {
            signatureOutputPath = null;
            signatureOutputField.setText("");
        }
    }

    void didSelectChooseOutputKey() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Odaberi javni kljuc posiljatelja");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            outputKeyPath = chooser.getSelectedFile().toPath();
            outputKeyField.setText(chooser.getSelectedFile().toPath().toString());
        } else {
            outputKeyPath = null;
            outputKeyField.setText("");
        }
    }
}
