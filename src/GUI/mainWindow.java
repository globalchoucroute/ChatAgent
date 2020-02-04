package GUI;

import Session.session;
import Software.sessionTable;
import Software.systemMessage;
import Software.systemMessageSender;
import Software.userData;
import Software.userList;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import GUI.userActions;

public class mainWindow extends JFrame {

    //Attributes

    userActions userActions;
    public String username;
    //Constructor
    mainWindow(userData myself, userList usersList, sessionTable sessionTable){
        super("Chat Agent - " + myself.getUsername());
        setBackground(Color.yellow);
        username = myself.getUsername();
        GUI.contactList contactList = new contactList(usersList);
        this.userActions = new userActions(contactList.contacts, myself, this, usersList, sessionTable);
        userActions.setAlignmentX(Component.LEFT_ALIGNMENT);
        ImageIcon icon = new ImageIcon("images/icon.png");
        setIconImage(icon.getImage());
        //setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
        add(contactList, BorderLayout.WEST);
        add(userActions, BorderLayout.CENTER);
        setPreferredSize(new Dimension(400,200));
        setMinimumSize(new Dimension(385, 200));
        setMaximumSize(new Dimension(500, 350));
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
                        if (!sessionTable.isEmpty()){
                            for (int i = 0; i <sessionTable.length(); i++){
                                session currentSession = sessionTable.element(i);
                                String ip = currentSession.getOtherUserData().getIPAddress();
                                systemMessageSender.sendSystemMessage(new systemMessage("disconnect", myself, 0), InetAddress.getByName(ip), false, 3000);
                                currentSession.closeSession();
                            }
                        }
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

    /**
     * Changes the username on the display when the user decides to change their username.
     * @param name is the new name to be displayed.
     */
    public void setUsername(String name){
        username = name;
        setTitle("Chat Agent - " + name);
        userActions.changeUsername(name);
    }
}
