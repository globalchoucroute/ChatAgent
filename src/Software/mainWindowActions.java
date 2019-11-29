package Software;

import javax.xml.crypto.Data;
import java.net.*;

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



    public void changeUsername(String usr){
        try {
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket outPacket = new DatagramPacket(usr.getBytes(), usr.length(), InetAddress.getByName("10.1.255.255"), 5000);
            socket.setBroadcast(true);
            socket.send(outPacket);
        } catch(Exception e){
            System.out.println("raté change username");
        }

    }
}
