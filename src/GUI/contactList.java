package GUI;

import javax.swing.*;
import java.awt.*;

public class contactList extends JPanel {

    //Attributes
    DefaultListModel<String> listModel;
    JList<String> contacts;
    JScrollPane container;
    JPanel panel;

    //Constructor
    public contactList() {
        super();
        this.listModel = new DefaultListModel<>();
        listModel.addElement("Premier utilisateur");
        listModel.addElement("Deuxieme utilisateur");
        listModel.addElement("Troisieme utilisateur");
        listModel.addElement("Quatrieme utilisateur");

        this.contacts = new JList<>(listModel);
        contacts.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        contacts.setLayoutOrientation(JList.VERTICAL);
        contacts.setVisibleRowCount(4);

        container = new JScrollPane(contacts);
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
    public void addContact(String contact){
        this.listModel.addElement(contact);
    }
}
