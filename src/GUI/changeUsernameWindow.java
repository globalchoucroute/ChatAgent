package GUI;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class changeUsernameWindow extends JFrame {

    private JTextField userNameField = new JTextField();

    changeUsernameWindow(mainWindow mainWindow){
        super();

        setLocationRelativeTo(null);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        userNameField.setToolTipText("Type in here your desired new username");

        userNameField.setSize(new Dimension(100, 50));
        userNameField.setPreferredSize(new Dimension(100, 20));

        Border border = BorderFactory.createLineBorder(Color.black);
        JPanel username = new JPanel();
        username.setAlignmentY(Component.CENTER_ALIGNMENT);
        username.setBorder(border);
        username.setPreferredSize(new Dimension(380, 20));
        JLabel enter = new JLabel("<html><p style = 'font-size:110%;font-family:Calibri;'>Please enter your new username : </p></html>");
        username.add(enter, BorderLayout.WEST);
        username.add(userNameField, BorderLayout.EAST);
        //Attributes
        JButton connectButton = new JButton("Change username");
        connectButton.setPreferredSize(new Dimension(50,20));
        add(username);
        add(connectButton);
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
                    if (userNameField.getText() != null && !userNameField.getText().equals("")){
                        mainWindow.userActions.mainWindowActions.changeUsername(userNameField.getText());
                        mainWindow.setUsername(userNameField.getText());
                        setVisible(false);
                        dispose();
                    }
                }
            }
        });
        setPreferredSize(new Dimension(200,150));
        pack();
        setVisible(true);

    }
}
