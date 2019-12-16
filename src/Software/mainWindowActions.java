package Software;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;

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

                    System.out.println("Waiting for a packet reception");
                    //Recover the datagram sent by client
                    serverSocket.receive(packet);


                    messageTreatment m = new messageTreatment(username, userList, packet);
                    m.start();

                    System.out.println("Started the message treatment thread");
                }
            } catch (Exception e){
                System.out.println("J'EN AI MARRE DES EXCEPTIONS ALORS QUE C'EST BIEN PRATIQUE POURTANT");
            }
        });
        this.username = username;
        messageReception.start();

    }

    //Methods
    public chatSession beginSession(String usr, int port, userData otherUserData, DatagramSocket d) {
        try {
            //TODO : FORMAT DES MESSAGES POUR LE TRAITEMENT
            String msg = usr + " begin "+ port;
            System.out.println("beginSession message sent :" + msg);
            DatagramPacket outPacket = new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName(otherUserData.getIPAddress()), 3000);
            d.send(outPacket);
            return new chatSession(port, otherUserData, true);
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
            System.out.println("usernameCheck message : "+ message);

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
    public String[] data;
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
        data = this.message.split(" ");
    }

    public void run(){
        System.out.println("messageTreatment thread running with " + message);
        this.theirs=data[0];
        System.out.println("Username : " + data[0] + "\nInstruction : " + data[1]);
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
                System.out.println("Received the begin message");
                userData otherUser = contactList.getUserByName(theirs);
                chatWindow chatWindow = new chatWindow(me, theirs, new chatSession(port, otherUser, false));
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
                        NetworkInterface network = NetworkInterface.getByName("eth0");
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        while (addresses.hasMoreElements()){
                            InetAddress address = addresses.nextElement();
                            if (address instanceof Inet4Address && !address.isLoopbackAddress()){
                                ip = address;
                            }
                        }
                        byte[] mac = network.getHardwareAddress();

                        //Formatting the message for a clean display
                        String ips = ip.toString().substring(1);
                        StringBuilder sb = new StringBuilder(18);
                        for (byte b : mac) {
                            if (sb.length() > 0)
                                sb.append(':');
                            sb.append(String.format("%02x", b));
                        }
                        String macs = sb.toString();

                        //Message format for the sendHello = "jean-michel|00:1B:44:11:3A:B7|192.168.0.1"
                        String message = me + "|" + macs + "|" + ips;
                        System.out.println("RecvHello message : "+message);

                        //Returning a packet with our info, so that the new user can create their active list
                        DatagramSocket socket = new DatagramSocket();
                        DatagramPacket outPacket = new DatagramPacket(message.getBytes(), message.length(), address, 2002);
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

    //getters
    public String getOtherUsername(){
        return data[0];
    }

    public int getPort(){
        return Integer.parseInt(data[2]);
    }

}