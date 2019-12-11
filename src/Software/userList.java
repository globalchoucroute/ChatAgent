package Software;


import java.util.ArrayList;
import java.util.Collections;

//General class for the user list. It will be instantiated once at the beginning of the program, and will then be modified
public class userList {

    public ArrayList list;

    public userList(){
        list = new ArrayList();
    }

    public int getLength(){
        return this.list.size();
    }

    public userData getUser(int index){
        return (userData) this.list.get(index);
    }
    //Add an element to the list
    public void addElement(userData userData){
        list.add(userData);
        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
    }

    //Delete an element from the list
    public void deleteElement(userData userData){
        if (list.contains(userData)){
            list.remove(list.indexOf(userData));
        }
    }
}
