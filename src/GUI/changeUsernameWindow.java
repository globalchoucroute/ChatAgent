package GUI;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.InetAddress;


public class changeUsernameWindow extends JFrame {

    //Attributes
    public JButton connectButton = new JButton("Change username");
    public JTextField userNameField = new JTextField("Type in your new username");

    public changeUsernameWindow(mainWindow mainWindow){
        super();
        userNameField.setSize(new Dimension(100, 50));
        userNameField.setPreferredSize(new Dimension(100, 20));
        connectButton.setPreferredSize(new Dimension(50,20));
        add(userNameField, BorderLayout.CENTER);
        add(connectButton, BorderLayout.SOUTH);
        connectButton.addActionListener(e -> {
            if (userNameField.getText() != null){
                mainWindow.setUsername(userNameField.getText());
                setVisible(false);
                dispose();
            }
        });

        userNameField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    if (userNameField.getText() != null){
                        mainWindow.setUsername(userNameField.getText());
                        setVisible(false);
                        dispose();
                    }
                }
            }
        });
        setPreferredSize(new Dimension(200,200));
        pack();
        setVisible(true);

    }
}
