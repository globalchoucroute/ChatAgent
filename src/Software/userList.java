package Software;


import GUI.contactList;

import java.util.ArrayList;
import java.util.Collections;

//General class for the user list. It will be instantiated once at the beginning of the program, and will then be modified
public class userList {

    public ArrayList list;
    public GUI.contactList GUIcontactList;

    public userList(){
        list = new ArrayList();
        GUIcontactList = null;
    }

    public int getLength(){
        return this.list.size();
    }

    public userData getUserByName(String name) {
        for (int i = 0; i < this.getLength(); i++) {
            if (this.getUser(i).username == name) {
                return getUser(i);
            }
        }
        return null;
    }

    public userData getUserByMac(String mac){
        for (int i = 0; i < this.getLength(); i++) {
            if (this.getUser(i).macAddress == mac) {
                return getUser(i);
            }
        }
        return null;
    }

    public userData getUser(int index){
        return (userData) this.list.get(index);
    }

    //Add an element to the list
    public void addElement(userData userData){
        list.add(userData);
        System.out.println("Element added in the user list : " + userData.getUsername());
        //GUIcontactList.addContact(userData);
        //Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
    }

    //Delete an element from the list
    public void deleteElement(userData userData){
        if (list.contains(userData)){
            list.remove(list.indexOf(userData));
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
    public void modifyUsername(String mac, String name){
        userData user = this.getUserByMac(mac);
        int index = list.indexOf(user);

        list.set(index, new userData(name, mac, user.getIPAddress()));
    }

    public void setGUIcontactList(contactList contactList){
        GUIcontactList = contactList;
    }
}
