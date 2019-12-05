package Software;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class mainWindowActions {

    //Attributes
    protected String username;
    protected String[][] activeList = new String[2][];

    //Constructor
    public mainWindowActions(String username) {
        Thread messageReception = new Thread(() -> {
            try {
                DatagramSocket serverSocket = new DatagramSocket(3000);

                while(true) {
                    //Creating the buffer for incoming messages
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    //Recover the datagram sent by client
                    serverSocket.receive(packet);

                    messageTreatment m = new messageTreatment(username, activeList, packet);
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
    public chatSession beginSession(int port) {
        try {
            return new chatSession(new DatagramSocket(port));
        } catch (SocketException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void changeUsername(String usr){
        //TODO : Check if the new username is valid
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

class messageTreatment extends Thread {

    public String me;
    public String theirs;
    public String[][] list;
    public String message;
    public InetAddress address;
    public int port;
    messageTreatment(String a, String[][] c, DatagramPacket packet){
        me = a;
        list = c;
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
                        DatagramPacket outPacket = new DatagramPacket(me.getBytes(),me.length(), address, port);
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
                    //Retrieving the MAC address
                    InetAddress ip = InetAddress.getLocalHost();
                    NetworkInterface network = NetworkInterface.getByInetAddress(ip);
                    byte[] mac = network.getHardwareAddress();

                    //Message format for the sendHello = "jean-michel|00:1B:44:11:3A:B7"
                    String message = me + "|" + Arrays.toString(mac);

                    //Sending the sendHello package with username and mac address in broadcast mode
                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket outPacket = new DatagramPacket(message.getBytes(),message.length(), InetAddress.getByName("10.1.255.255"), 2001);
                    socket.setBroadcast(true);
                    socket.send(outPacket);

                    //Update the activeList table
                    //TODO : ACTIVE LIST CLASS
                } catch (Exception e) {
                    System.out.println("Could not send the message");
                }
            return;
        }
    }
}