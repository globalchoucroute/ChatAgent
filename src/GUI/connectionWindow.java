package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import Software.connection;
import Software.userList;

public class connectionWindow extends JFrame {

    //Attributes
    public JButton connectButton = new JButton("Connect");
    public JTextField userNameField = new JTextField("Type in your username");
    public JFrame parent;

    //Constructor
    public connectionWindow (){
        super();
        parent = this;
        userNameField.setSize(new Dimension(100, 50));
        userNameField.setPreferredSize(new Dimension(100, 20));
        connectButton.setPreferredSize(new Dimension(50,20));
        add(userNameField, BorderLayout.CENTER);
        add(connectButton, BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        connection connection = new connection();

        userNameField.addKeyListener(new KeyAdapter() {
             public void keyPressed(KeyEvent e) {
                 String username = userNameField.getText();
                 int key = e.getKeyCode();
                 if (key == KeyEvent.VK_ENTER) {
                     if (username != null){
                         if(connection.checkUsername(username)) {
                             userList userList = connection.sendHello(username);
                             mainWindow mainWindow = new mainWindow(username, userList);
                             setVisible(false);
                             dispose();
                         }
                         else {
                             JOptionPane.showMessageDialog(parent, "This username is already taken !");
                             connection.setControlTrue();
                         }
                     }
                 }
             }
         });

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userNameField.getText();
                if (username != null){
                    if(connection.checkUsername(username)){
                        userList userList = connection.sendHello(username);
                        mainWindow mainWindow = new mainWindow(username, userList);
                        setVisible(false);
                        dispose();
                    }
                    else {
                        JOptionPane.showMessageDialog(parent, "This username is already taken !");
                        connection.setControlTrue();
                    }
                }
            }
        });

        setPreferredSize(new Dimension(200,200));
        pack();
        setVisible(true);

    }
}
