package gui.tabs;
import crypto.AES;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Created by filipgulan on 06/05/2017.
 */
public class SymmetricChiperTab extends JPanel {

    private JTextField inputField;
    private JTextField outputField;
    private JTextField keyField;
    private JRadioButton encryptRadio = new JRadioButton("Kriptiraj");
    private JRadioButton decryptRadio = new JRadioButton("Dekriptiraj");
    private Path inputPath;
    private Path outputPath;

    public SymmetricChiperTab() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        initGUI();
    }

    private void initGUI() {
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Ulazna datoteka"));
        inputField = new JTextField();
        inputField.setColumns(19);
        inputField.setEditable(false);
        inputPanel.add(inputField);
        JButton inputButton = new JButton("Odaberi");
        inputButton.addActionListener(e -> {
            didSelectChooseSource();
        });
        inputPanel.add(inputButton);

        JPanel outputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        outputPanel.add(new JLabel("Izlazna datoteka"));
        outputField = new JTextField();
        outputField.setColumns(19);
        outputField.setEditable(false);
        outputPanel.add(outputField);
        JButton outputButton = new JButton("Odaberi");
        outputButton.addActionListener(e -> {
            didSelectChooseDestination();
        });
        outputPanel.add(outputButton);

        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        ButtonGroup group = new ButtonGroup();
        group.add(encryptRadio);
        group.add(decryptRadio);
        encryptRadio.setSelected(true);
        switchPanel.add(encryptRadio);
        switchPanel.add(decryptRadio);

        JPanel processPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton processButton = new JButton("Obradi");
        processButton.addActionListener(e -> {
            didSelectProcess();
        });
        processPanel.add(processButton);

        JPanel keyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        keyPanel.add(new JLabel("Ključ"));
        keyField = new JTextField();
        keyField.setColumns(22);
        keyPanel.add(keyField);

        add(keyPanel);
        add(inputPanel);
        add(outputPanel);
        add(switchPanel);
        add(processPanel);
    }

    private void didSelectChooseSource() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Odaberi ulaznu datoteku...");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            inputPath = chooser.getSelectedFile().toPath();
            inputField.setText(chooser.getSelectedFile().toPath().toString());
        } else {
            inputField.setText("");
            inputPath = null;
        }
    }

    private void didSelectChooseDestination() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Odaberi izlaznu datoteku...");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            outputPath = chooser.getSelectedFile().toPath();
            outputField.setText(outputPath.toString());
        } else {
            outputField.setText("");
            outputPath = null;
        }
    }

    private void didSelectProcess() {
        if (keyField.getText() == null || keyField.getText().length() <= 0 ||
                inputPath == null || outputPath == null) {
            return;
        }

        AES aes = new AES(keyField.getText());
        try {
            if (encryptRadio.isSelected()) {
                byte[] output = aes.encrypt(Files.readAllBytes(inputPath));
                Files.write(outputPath, output, StandardOpenOption.CREATE);
            } else {
                byte[] output = aes.decrypt(Files.readAllBytes(inputPath));
                Files.write(outputPath, output, StandardOpenOption.CREATE);
            }
        } catch (IOException e) {
            System.out.println("Error");
        }
    }
}
