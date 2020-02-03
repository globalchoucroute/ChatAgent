package GUI;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import java.awt.Color;
import java.awt.Dimension;
import Software.userList;
import Software.userData;
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
    //Setters

    /**
     * Initializes the contact list after the connection process.
     * @param userList is the list generated during the connection process.
     * @return a DefaultListModel containing the username of every connected user.
     */
    private DefaultListModel initContactList(userList userList){
        for (int i = 0; i < userList.getLength(); i++){
            listModel.addElement(userList.getUser(i).getUsername());
        }
        userList.setGUIcontactList(this);
        return listModel;
    }

    /**
     * Deletes a contact from the contacts display.
     * @param userData is the user we want to delete from the list.
     */
    public void deleteContact(userData userData) { listModel.removeElement(userData.getUsername()); }

    /**
     * Adds a new contact to the display.
     * @param userData is the user we want to add to the list.
     */
    public void addContact(userData userData){
        listModel.addElement(userData.getUsername());
    }

}
