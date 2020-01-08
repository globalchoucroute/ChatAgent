package Software;

import java.util.ArrayList;
import Session.session;
public class sessionTable {
    //Attributes
    public ArrayList list;

    public sessionTable (){
        list = new ArrayList();
    }

    public void addSession(session session){
        list.add(session);
    }

    public session getSessionByName(String username){
        for (int i = 0; i<list.size(); i++){
            System.out.println("Currently checking the session with user " + ((session)list.get(i)).getOtherUserData().getUsername());
            System.out.println("Do we have " + username + " = " + ((session)list.get(i)).getOtherUserData().getUsername() + " ?");
            if (((session)list.get(i)).getOtherUserData().getUsername().equals(username)){
                System.out.println("Yes we do !");
                return ((session) list.get(i));
            }
        }
        return null;
    }

    public void closeSession(String username){
        for (int i = 0; i<list.size(); i++){
            System.out.println("Currently checking the session with user " + ((session)list.get(i)).getOtherUserData().getUsername());
            System.out.println("Do we have " + username + " = " + ((session)list.get(i)).getOtherUserData().getUsername() + " ?");
            if (((session)list.get(i)).getOtherUserData().getUsername().equals(username)){
                System.out.println("Yes we do !");
                ((session) list.get(i)).closeSession();
            }
        }
    }
}
