package Software;

import java.util.ArrayList;

public class sessionTable {
    //Attributes
    public ArrayList list;

    public sessionTable (){
        list = new ArrayList();
    }

    public void addSession(session session){
        list.add(session);
    }

    public void getSessionByName(String username){
        
    }
}
