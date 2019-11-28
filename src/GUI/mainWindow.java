package GUI;

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
    //Constructor
    public mainWindow(String userName){
        super("Chat Agent");

        this.contactList = new contactList();
        this.userActions = new userActions(contactList.contacts, userName, this);

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
}
