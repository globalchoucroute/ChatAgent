package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import Software.connection;

public class connectionWindow extends JFrame {

    //Attributes
    public JButton connectButton = new JButton("Connect");
    public JTextField userNameField = new JTextField("Type in your username");
    public connection connection = new connection();
    public String[][] userList;

    public connectionWindow (){
        super();
        userNameField.setSize(new Dimension(100, 50));
        userNameField.setPreferredSize(new Dimension(100, 20));
        connectButton.setPreferredSize(new Dimension(50,20));
        add(userNameField, BorderLayout.CENTER);
        add(connectButton, BorderLayout.SOUTH);


        userNameField.addKeyListener(new KeyAdapter() {
             public void keyPressed(KeyEvent e) {
                 String username = userNameField.getText();
                 int key = e.getKeyCode();
                 if (key == KeyEvent.VK_ENTER) {
                     if (username != null){
                         if(connection.checkUsername(username)) {
                             userList = connection.sendHello(username);
                             mainWindow mainWindow = new mainWindow(username, userList);
                             setVisible(false);
                             dispose();
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
                        connection.sendHello(username);
                        mainWindow mainWindow = new mainWindow(username,connection.activeList);
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
