package GUI;

import Software.sessionTable;
import Software.systemMessage;
import Software.systemMessageSender;
import Software.userData;
import Software.userList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class mainWindow extends JFrame {

    //Attributes

    userActions userActions;
    public String username;
    //Constructor
    mainWindow(userData myself, userList usersList, sessionTable sessionTable){
        super("Chat Agent");
        username = myself.getUsername();
        GUI.contactList contactList = new contactList(usersList);
        this.userActions = new userActions(contactList.contacts, myself, this, usersList, sessionTable);
        userActions.setAlignmentX(Component.CENTER_ALIGNMENT);
        ImageIcon icon = new ImageIcon("images/icon.png");
        setIconImage(icon.getImage());

        add(contactList, BorderLayout.WEST);
        add(userActions, BorderLayout.CENTER);
        setPreferredSize(new Dimension(400,200));
        setMinimumSize(new Dimension(200,200));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        System.out.println("My user data : \nUsername : " + myself.getUsername() + "\nIP address : " + myself.getIPAddress());
        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                        null, "Are you sure you want to quit ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0){
                    try {
                        systemMessageSender systemMessageSender = new systemMessageSender();
                        systemMessageSender.sendSystemMessage(new systemMessage("bye", myself, 0), InetAddress.getByName("255.255.255.255"), true, 3000);
                    } catch (UnknownHostException uhe) {
                        uhe.printStackTrace();
                    }
                    System.exit(0);
                }
            }
        };
        addWindowListener(exitListener);

        pack();
        setVisible(true);
    }


    //Methods
    //Getters

    //Setters
    public void setUsername(String name){
        username = name;
        userActions.changeUsername(name);
    }
}
