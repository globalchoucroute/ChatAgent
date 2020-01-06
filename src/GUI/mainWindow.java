package GUI;

import Software.sessionTable;
import Software.userList;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.*;

public class mainWindow extends JFrame {

    //Attributes
    JFrame window;
    contactList contactList;
    textArea textArea;
    userActions userActions;
    public String username;
    //Constructor
    public mainWindow(String userName, userList usersList, sessionTable sessionTable){
        super("Chat Agent");
        username = userName;
        this.contactList = new contactList(usersList);
        this.userActions = new userActions(contactList.contacts, username, this, usersList, sessionTable);

        add(contactList, BorderLayout.WEST);
        add(userActions, BorderLayout.CENTER);
        setPreferredSize(new Dimension(600,400));
        setMinimumSize(new Dimension(200,200));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                        null, "Are you sure you want to quit ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0){
                    try {
                        DatagramSocket dcSocket = new DatagramSocket(10000);
                        String msg = username + " bye";
                        DatagramPacket outPacket = new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName("10.1.255.255"), 3000);
                        dcSocket.setBroadcast(true);
                        dcSocket.send(outPacket);
                        System.out.println("Bye message sent : " + msg);
                        dcSocket.close();
                        System.exit(0);
                    } catch (SocketException se){
                        System.out.println("Error while setting up the dcSocket");
                        se.printStackTrace();
                    } catch (UnknownHostException he){
                        System.out.println("Unknown host for the disconnect message");
                        he.printStackTrace();
                    } catch (IOException ie){
                        System.out.println("Error while sending the message");
                        ie.printStackTrace();
                    }
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
