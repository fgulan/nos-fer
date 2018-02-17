package gui.tabs;

import crypto.AES;
import crypto.DigitalEnvelope;
import crypto.RSA;
import crypto.RSAKeysGenerator;

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
public class DigitalEnvelopeTab extends JPanel {

    private JTextField keysField = new JTextField(20);
    private JTextField symmetricKeyField = new JTextField(20);

    private JTextField inputFileField = new JTextField(20);
    private JTextField inputKeyField = new JTextField(20);
    private JTextField envelopeOutputField = new JTextField(20);
    private JTextField outputKeyField = new JTextField(20);
    private JTextField outputFileField = new JTextField(20);

    private Path outputKeysPath;
    private Path inputFilePath;
    private Path inputKeyPath;
    private Path envelopeOutputPath;
    private Path outputKeyPath;
    private Path outputFilePath;

    public DigitalEnvelopeTab() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        initGUI();
    }

    private void initGUI() {
        JPanel gridPanel = new JPanel(new GridLayout(0, 3));

        JPanel keysPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton chooseKeysButton = new JButton("Odaberi");
        chooseKeysButton.addActionListener(e -> {
            didSelectChooseKeysDestination();
        });
        JButton generateButton = new JButton("Generiraj");
        generateButton.addActionListener(e -> {
            didSelectGenerateKeys();
        });
        keysField.setEditable(false);
        keysPanel.add(new JLabel("Ključevi"));
        keysPanel.add(keysField);
        keysPanel.add(chooseKeysButton);
        keysPanel.add(generateButton);

        JPanel symPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        symPanel.add(new JLabel("Simetricni kljuc"));
        symPanel.add(symmetricKeyField);

        gridPanel.add(new JLabel("Ulazna datotoeka"));
        gridPanel.add(inputFileField);
        JButton inputFileButton = new JButton("Odaberi");
        inputFileButton.addActionListener(e -> {
            didSelectChooseInputFile();
        });
        gridPanel.add(inputFileButton);

        gridPanel.add(new JLabel("Javni kljuc primatelja"));
        gridPanel.add(inputKeyField);
        JButton inputKeyButton = new JButton("Odaberi");
        inputKeyButton.addActionListener(e -> {
            didSelectChooseInputKey();
        });
        gridPanel.add(inputKeyButton);

        gridPanel.add(new JLabel("Digitalna omotnica"));
        gridPanel.add(envelopeOutputField);
        JButton envelopeButton = new JButton("Odaberi");
        envelopeButton.addActionListener(e -> {
            didSelectChooseEnvelope();
        });
        gridPanel.add(envelopeButton);

        JButton generateEnvelopeButton = new JButton("Generiraj digitalnu omotnicu");
        generateEnvelopeButton.addActionListener(e -> {
            try {
                generateEnvelope();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        JPanel openPanel = new JPanel(new GridLayout(0, 3));
        openPanel.add(new JLabel("Tajni kljuc primatelja"));
        openPanel.add(outputKeyField);
        JButton choosePrivateButton = new JButton("Odaberi");
        choosePrivateButton.addActionListener(e -> {
            didSelectChooseOutputKey();
        });
        openPanel.add(choosePrivateButton);

        openPanel.add(new JLabel("Izlazna datoteka"));
        openPanel.add(outputFileField);
        JButton chooseOutputButton = new JButton("Odaberi");
        chooseOutputButton.addActionListener(e -> {
            didSelectChooseOutputFile();
        });
        openPanel.add(chooseOutputButton);

        JButton openEnvelopeButton = new JButton("Otvori digitalnu omotnicu");
        openEnvelopeButton.addActionListener(e -> {
            try {
                openEnvelope();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });

        add(keysPanel);
        add(symPanel);
        add(gridPanel);
        add(generateEnvelopeButton);
        add(openPanel);
        add(openEnvelopeButton);
    }

    private void didSelectChooseKeysDestination() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Odaberi direktorij za ključeve...");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            outputKeysPath = chooser.getSelectedFile().toPath();
            keysField.setText(chooser.getSelectedFile().toPath().toString());
        } else {
            keysField.setText("");
            outputKeysPath = null;
        }
    }

    private void didSelectGenerateKeys() {
        if (outputKeysPath == null) {
            return;
        }
        try {
            RSAKeysGenerator generator = new RSAKeysGenerator(2048);
            generator.createKeys();
            generator.storeKeysToFolder(outputKeysPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void didSelectChooseInputFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Odaberi ");
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
        chooser.setDialogTitle("Odaberi javni kljuc primatelja");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            inputKeyPath = chooser.getSelectedFile().toPath();
            inputKeyField.setText(chooser.getSelectedFile().toPath().toString());
        } else {
            inputKeyPath = null;
            inputKeyField.setText("");
        }
    }

    void didSelectChooseEnvelope() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Odaberi omotnicu");
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            envelopeOutputPath = chooser.getSelectedFile().toPath();
            envelopeOutputField.setText(chooser.getSelectedFile().toPath().toString());
        } else {
            envelopeOutputPath = null;
            envelopeOutputField.setText("");
        }
    }

    void didSelectChooseOutputKey() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Odaberi tajni kljuc primatelja");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            outputKeyPath = chooser.getSelectedFile().toPath();
            outputKeyField.setText(chooser.getSelectedFile().toPath().toString());
        } else {
            outputKeyPath = null;
            outputKeyField.setText("");
        }
    }

    void didSelectChooseOutputFile() {
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

    void generateEnvelope() throws Exception {
        if (inputKeyPath == null || inputFilePath == null || envelopeOutputPath == null || symmetricKeyField.getText() == null) {
            return;
        }
        byte[] input = Files.readAllBytes(inputFilePath);
        ArrayList<String> lines = DigitalEnvelope.generateEnvelope(symmetricKeyField.getText(), input, inputKeyPath);
        Files.write(envelopeOutputPath, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
    }

    void openEnvelope() throws Exception {
        if (outputKeyPath == null || outputFilePath == null || envelopeOutputPath == null) {
            return;
        }
        List<String> lines = Files.readAllLines(envelopeOutputPath);
        byte[] result = DigitalEnvelope.openEnvelope(lines.get(1), lines.get(3), outputKeyPath);
        Files.write(outputFilePath, result, StandardOpenOption.CREATE);
    }
}
