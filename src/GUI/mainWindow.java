package GUI;

import Software.userList;

import javax.swing.JFrame;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class mainWindow extends JFrame {

    //Attributes
    JFrame window;
    contactList contactList;
    textArea textArea;
    userActions userActions;
    public String username;
    //Constructor
    public mainWindow(String userName, userList usersList){
        super("Chat Agent");
        username = userName;
        this.contactList = new contactList(usersList);
        this.userActions = new userActions(contactList.contacts, username, this, usersList);

        add(contactList, BorderLayout.WEST);
        add(userActions, BorderLayout.CENTER);
        setPreferredSize(new Dimension(600,400));
        setMinimumSize(new Dimension(200,200));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
