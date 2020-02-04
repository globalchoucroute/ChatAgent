package Software;


import GUI.contactList;
import java.util.ArrayList;

//General class for the user list. It will be instantiated once at the beginning of the program, and will then be modified
public class userList {

    private ArrayList list;
    private GUI.contactList GUIcontactList;

    userList(){
        list = new ArrayList();
        GUIcontactList = null;
    }

    public int getLength(){
        return this.list.size();
    }

    public userData getUserByName(String name) {
        for (int i = 0; i < this.getLength(); i++) {
            System.out.println("Checking current username : " + getUser(i).getUsername());
            if (getUser(i).getUsername().equals(name)) {
                System.out.println("User found in the getUserByName : " + getUser(i).getUsername());
                return getUser(i);
            }
        }
        return null;
    }

    public userData getUserByStatus(String status){
        for (int i = 0; i < this.getLength(); i++) {
            System.out.println("Checking current username : " + getUser(i).getUsername());
            String current = "["+getUser(i).getStatus()+"] "+ getUser(i).getUsername();
            if (current.equals(status)) {
                System.out.println("User found in the getUserByName : " + getUser(i).getUsername());
                return getUser(i);
            }
        }
        return null;
    }
    private userData getUserByMac(String mac){
        for (int i = 0; i < list.size(); i++) {
            if (getUser(i).getMacAddress().equals(mac)) {
                return getUser(i);
            }
        }
        return null;
    }

    public userData getUser(int index){
        return (userData) list.get(index);
    }

    boolean isAvailable(String name) {
        boolean control = true;
        for (int i = 0; i < this.getLength(); i++) {
            if (getUser(i).getUsername().equals(name)) {
                control = false;
            }
        }
        return control;
    }

    //add an element to the list
    void addElement(userData userData){
        list.add(userData);
        System.out.println("Element added in the user list : " + userData.getUsername());
        GUIcontactList.addContact(userData);
    }

    //Delete an element from the list
    void deleteElement(userData userData){
        if (list.contains(userData)){
            list.remove(userData);
            GUIcontactList.deleteContact(userData);
        }
    }

    //Check if the element exists given a Mac address
    public boolean exists(String mac){
        if (list.contains(this.getUserByMac(mac))){
            return true;
        }
        return false;
    }

    //Modify a username referenced from its mac address
    public void modifyUsername(String mac, String name) {
        userData user = this.getUserByMac(mac);
        if (user != null) {
            GUIcontactList.deleteContact(user);
            int index = list.indexOf(user);
            userData updatedUser = new userData(name, mac, user.getIPAddress(), user.getStatus());
            if (index != -1) list.set(index, updatedUser);
        }
    }

    public void modifyStatus(String mac, String status){
        userData user = this.getUserByMac(mac);
        if (user != null) {
            GUIcontactList.modifyStatus(user, status);
            int index = list.indexOf(user);
            userData updatedUser = new userData(user.getUsername(), mac, user.getIPAddress(), status);
            if (index != -1) list.set(index, updatedUser);
        }
    }

    public void setGUIcontactList(contactList contactList){
        GUIcontactList = contactList;
    }
}
