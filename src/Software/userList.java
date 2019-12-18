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
            System.out.println("Checking current username : " + getUser(i).getUsername());
            if (getUser(i).getUsername().equals(name)) {
                System.out.println("User found in the getUserByName : " + getUser(i).getUsername());
                return getUser(i);

            }
        }
        return null;
    }

    public userData getUserByMac(String mac){
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



    //Initiate the list for the first connection
    public void addElementInit(userData userData){
        list.add(userData);
        System.out.println("Element added in the user list : " + userData.getUsername());
        //Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
    }

    //add an element to the list
    public void addElement(userData userData){
        list.add(userData);
        System.out.println("Element added in the user list : " + userData.getUsername());
        GUIcontactList.addContact(userData);
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
