package Software;

import java.util.ArrayList;
import Session.session;
import Software.userData;

/**
 * sessionTable class. Its only attribute is a list containing every active session. Every time we need to find a certain session, we check the elements of the list.
 */
public class sessionTable {

    //Attributes
    private ArrayList list;

    public sessionTable (){
        list = new ArrayList();
    }

    /**
     * Adds the parameter to the opened sessions list.
     * @param session The session that will be added to the list.
     */
    void addSession(session session){
        list.add(session);
        for (int i = 0; i< list.size(); i++){System.out.println("Opened session number " + (i+1) + " with user " + ((session)list.get(i)).getOtherUserData().getUsername());}
    }

    /**
     * Finds the opened session associated the the received userData (its mac address). When found, calls the updateOtherUserdata method from the
     * session class.
     * @param newUserdata The freshly received userData containing the user's new username
     *
     */
    void updateSessionOtherUserdata(userData newUserdata){
        for (Object o : list) {
            if (((session) o).getOtherUserData().getMacAddress().equals(newUserdata.getMacAddress())) {
                ((session) o).updateOtherUserdata(newUserdata);
            }
        }
    }

    public boolean isEmpty(){
        return list.isEmpty();
    }
    public int length(){
        return list.size();
    }
    public session element(int index){
        return (session) list.get(index);
    }

    /**
     * Finds the opened session associated to the received mac address. When found, calls the closeSession method from the session class in order to close it.
     * Also removes the session from the table.
     * @param mac The mac address corresponding to the user asking for a session close
     */
    void closeSession(String mac){
        for (Object o : list) {
            if (((session) o).getOtherUserData().getMacAddress().equals(mac)) {
                System.out.println("Found the correct session");
                ((session) o).closeSession();
                list.remove(o);
                break;
            }
        }
    }
}
