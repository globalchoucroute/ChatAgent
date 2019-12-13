package Software;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import GUI.chatWindow;

public class mainWindowActions {

    //Attributes
    protected String username;
    public userList contactList;

    //Control class for the checkUsername shared boolean
    class Control {
        public volatile boolean unique = true;
    }
    final connection.Control control = new connection.Control();

    //Constructor
    public mainWindowActions(String username, userList userList) {
        Thread messageReception = new Thread(() -> {
            try {
                DatagramSocket serverSocket = new DatagramSocket(3000);

                while(true) {
                    //Creating the buffer for incoming messages
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    //Recover the datagram sent by client
                    serverSocket.receive(packet);

                    messageTreatment m = new messageTreatment(username, userList, packet);
                    m.start();
                }
            } catch (Exception e){
                System.out.println("J'EN AI MARRE DES EXCEPTIONS ALORS QUE C'EST BIEN PRATIQUE POURTANT");
            }
        });
        messageReception.start();
        this.username = username;
    }

    //Methods
    public chatSession beginSession(String usr, int port, int portListen, userData otherUserData, DatagramSocket d) {
        try {
            String msg = usr + "|begin";
            DatagramPacket outPacket = new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName(otherUserData.getIPAddress()), 3000);
            d.send(outPacket);
            return new chatSession(new DatagramSocket(port), otherUserData);
        } catch (SocketException e) {
            e.printStackTrace();
            return null;
        } catch (Exception r){
            r.printStackTrace();
            return null;
        }

    }

    public void changeUsername(String usr){
        if (checkUsername(usr)){
            try {
                DatagramSocket socket = new DatagramSocket();
                DatagramPacket outPacket = new DatagramPacket(usr.getBytes(), usr.length(), InetAddress.getByName("10.1.255.255"), 3000);
                socket.setBroadcast(true);
                socket.send(outPacket);
            } catch(Exception e){
                System.out.println("raté change username");
            }
        }
    }

    public boolean checkUsername(String usr){
        try {
            //Message format for the username check : "jean-michel|check"
            String message = usr + "|check";

            //Creating the server socket for potential reception
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket outPacket = new DatagramPacket(message.getBytes(),message.length(), InetAddress.getByName("10.1.255.255"), 3000);
            socket.setBroadcast(true);
            socket.send(outPacket);

            //Start value for the timer
            long startTime = System.currentTimeMillis();
            long elapsedTime = 0L;

            //Reception thread. It will last for 0.5 seconds.
            Thread userNameReceptionThread = new Thread(() -> {
                try{
                    DatagramSocket serverSocket = new DatagramSocket(2004);
                    while(true){

                        //Creating the buffer for incoming messages
                        byte[] buffer = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                        //Recover the datagram sent by client
                        serverSocket.receive(packet);
                        control.unique = false;

                    }
                } catch (Exception e) {
                    System.out.println("Error while receiving the users info.");
                }
            });


            //Run the thread for the duration of the timer
            userNameReceptionThread.start();
            while (elapsedTime < 500) elapsedTime = (new Date()).getTime() - startTime;
            userNameReceptionThread.interrupt();


        } catch( Exception e){
            System.out.println("getLocalhost failed");
        }

        //returns true if no message has been received
        return control.unique;
    }

}

class messageTreatment extends Thread {

    public String me;
    public String theirs;
    public String message;
    public userList contactList;
    public InetAddress address;
    public int port;
    messageTreatment(String a, userList u, DatagramPacket packet){
        contactList = u;
        me = a;
        try{
            message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
            port = packet.getPort();
            address = packet.getAddress();
        } catch (Exception e){
            System.out.println("Error");
        }
    }

    public void run(){
        String[] data = this.message.split("|");
        this.theirs=data[0];
        switch (data[1]){
            case "check":
                if (theirs == me){
                    try {
                        DatagramSocket clientSocket = new DatagramSocket();
                        DatagramPacket outPacket = new DatagramPacket(me.getBytes(),me.length(), address, 2004);
                        clientSocket.send(outPacket);
                    } catch (Exception e) {
                        System.out.println("Could not send the message");
                    }
                }
                break;
            case "begin":
                //TODO : Begin case for the message treatment
                break;
            case "bye":
                //TODO : Bye case for the message treatment
                break;
            default:
                try {
                    //Check if the user already exists in the list
                    if (contactList.exists(data[1])){
                        contactList.modifyUsername(data[1], theirs);
                    }

                    else {
                        //Retrieving the MAC address
                        InetAddress ip = InetAddress.getLocalHost();
                        NetworkInterface network = NetworkInterface.getByInetAddress(ip);
                        byte[] mac = network.getHardwareAddress();

                        //Message format for the sendHello = "jean-michel|00:1B:44:11:3A:B7|192.168.0.1"
                        String message = me + "|" + Arrays.toString(mac) + "|" + ip.toString();

                        //Returning a packet with our info, so that the new user can create their active list
                        DatagramSocket socket = new DatagramSocket();
                        DatagramPacket outPacket = new DatagramPacket(message.getBytes(), message.length(), address, 3000);
                        socket.send(outPacket);

                        //Update the activeList table
                        contactList.addElement(new userData(data[0], data[1], data[2]));
                    }
                } catch (Exception e) {
                    System.out.println("Could not send the message");
                }
            return;
        }
    }
}