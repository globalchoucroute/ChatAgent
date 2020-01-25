package Software;

import Session.session;
import java.util.ArrayList;

public class chatMessages {
    //TODO : create a table containing a chatSession, the messages associated to this session, the username too

    public ArrayList list;

    public chatMessages(){
        list = new ArrayList();
    }

    public void addSession(session s){
        list.add(s);
    }

    public session getSessionById(int index){
        return (session) list.get(index);
    }

    public session getSessionByName(String username){
        for (int i = 0; i<list.size(); i++){
            if (((session) list.get(i)).getUsername().equals(username)){
                return (session) list.get(i);
            }
        }
        return null;
    }

    public void removeSession(session s){
        list.remove(s);
    }
}
