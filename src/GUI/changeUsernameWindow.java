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


class changeUsernameWindow extends JFrame {

    private JTextField userNameField = new JTextField("Type in your new username");

    changeUsernameWindow(mainWindow mainWindow){
        super();
        userNameField.setSize(new Dimension(100, 50));
        userNameField.setPreferredSize(new Dimension(100, 20));
        //Attributes
        JButton connectButton = new JButton("Change username");
        connectButton.setPreferredSize(new Dimension(50,20));
        add(userNameField, BorderLayout.CENTER);
        add(connectButton, BorderLayout.SOUTH);
        connectButton.addActionListener(e -> {
            if (userNameField.getText() != null){
                mainWindow.userActions.mainWindowActions.changeUsername(userNameField.getText());
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
                        mainWindow.userActions.mainWindowActions.changeUsername(userNameField.getText());
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
