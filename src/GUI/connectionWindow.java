package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class connectionWindow extends JFrame {

    //Attributes
    public JButton connectButton = new JButton("Connect");
    public JTextField userNameField = new JTextField("Type in your username");

    public connectionWindow (){
        super();
        userNameField.setSize(new Dimension(100, 50));
        userNameField.setPreferredSize(new Dimension(100, 20));
        connectButton.setPreferredSize(new Dimension(50,20));
        add(userNameField, BorderLayout.CENTER);
        add(connectButton, BorderLayout.SOUTH);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (userNameField.getText() != null){
                    mainWindow mainWindow = new mainWindow(userNameField.getText());
                    setVisible(false);
                    dispose();
                }
            }
        });
        setPreferredSize(new Dimension(200,200));
        pack();
        setVisible(true);

    }
}
