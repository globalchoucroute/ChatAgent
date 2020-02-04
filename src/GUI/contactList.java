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
        container.setBackground(Color.gray);
        /*container.setLayout(new BoxLayout(container, BoxLayout.LINE_AXIS));
        container.setBackground(Color.red);
        container.setPreferredSize(new Dimension(100,200));*/
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.add(container);

        this.setBackground(Color.gray);
        this.setPreferredSize(new Dimension(100,200));

    }

    /**
     * Initializes the contact list after the connection process.
     * @param userList is the list generated during the connection process.
     * @return a DefaultListModel containing the username of every connected user.
     */
    private DefaultListModel initContactList(userList userList){
        for (int i = 0; i < userList.getLength(); i++){
            String Status = userList.getUser(i).getStatus();
            String current = userList.getUser(i).getUsername();
            switch (Status) {
                case "Available":
                    current = "[Available] " + current;
                    break;
                case "Away":
                    current = "[Away] " + current;
                    break;
                case "Busy":
                    current = "[Busy] " + current;
                    break;
                default:
                    break;
            }
            listModel.addElement(current);
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
        listModel.addElement("["+userData.getStatus()+"] " + userData.getUsername());
    }

    public void modifyStatus(userData userData, String status) {
        int index = listModel.indexOf("["+userData.getStatus()+"] " + userData.getUsername());
        listModel.set(index, "["+status+"] "+userData.getUsername());
    }
}
