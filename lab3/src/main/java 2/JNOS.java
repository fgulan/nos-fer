import crypto.AES;
import crypto.SHA1Internal;
import crypto.SHA1My;
import gui.NOSTabbedPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by filipgulan on 06/05/2017.
 */
public class JNOS extends JFrame {


    public JNOS() {
        initGUI();
    }

    public static void main(String [] args) {
        SwingUtilities.invokeLater(() -> {
            final JFrame frame = new JNOS();
            frame.setVisible(true);
        });
    }

    private void initGUI() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(570, 350);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        getContentPane().add(new NOSTabbedPane(), BorderLayout.CENTER);
    }
}
