package GUI;

import javax.swing.*;
import java.awt.*;

import Software.userData;
import Software.userList;
public class contactList extends JPanel {

    //Attributes
    private DefaultListModel<String> listModel;
    JList<String> contacts;

    //Constructor
    contactList(userList userList) {
        super();
        this.listModel = new DefaultListModel<>();
        listModel = initContactList(userList);

        this.contacts = new JList<>(listModel);
        contacts.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        contacts.setLayoutOrientation(JList.VERTICAL);
        contacts.setVisibleRowCount(4);

        JScrollPane container = new JScrollPane(contacts);
        /*container.setLayout(new BoxLayout(container, BoxLayout.LINE_AXIS));
        container.setBackground(Color.red);
        container.setPreferredSize(new Dimension(100,200));*/
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.add(container);

        this.setBackground(Color.gray);
        this.setPreferredSize(new Dimension(200,200));

    }

    //Methods
    //Getters
    public DefaultListModel getListElements (){
        return this.listModel;
    }

    //Setters
    private DefaultListModel initContactList(userList userList){
        for (int i = 0; i < userList.getLength(); i++){
            listModel.addElement(userList.getUser(i).getUsername());
        }
        userList.setGUIcontactList(this);
        return listModel;
    }

    public void deleteContact(userData userData) { listModel.removeElement(userData.getUsername()); }
    public void addContact(userData userData){
        listModel.addElement(userData.getUsername());
    }

    public DefaultListModel updateList(userList userList){
        for (int i = 0; i < userList.getLength(); i++){
            listModel.addElement(userList.getUser(i).getUsername());
        }

        return listModel;
    }
}
