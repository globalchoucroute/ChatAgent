package Software;

import java.net.DatagramSocket;
import java.net.SocketException;

public class mainWindowActions {

    //Attributes
    protected String username;
    protected String[][] activeList = new String[2][];

    //Constructor
    public mainWindowActions(String username) {
        this.username = username;
    }

    //Methods
    public chatSession beginSession(int port) {
        try {
            chatSession session = new chatSession(new DatagramSocket(1234));
            return session;
        } catch (SocketException e) {
            e.printStackTrace();
            return null;
        }
    }



    String changeUsername(String usr){
        return "";
    }
}
