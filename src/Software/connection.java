package Software;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
public class connection {

    //Attributes
    public userList userList = new userList();

    //Control class for the checkUsername shared boolean
    static class Control {
        public volatile boolean unique = true;
    }
    final Control control = new Control();


    public connection (){
    }

    //Methods
    public boolean checkUsername(String usr){
        try {
            //Message format for the username check : "jean-michel|check"
            String message = usr + "|check";

            //Creating the server socket for potential reception
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket outPacket = new DatagramPacket(message.getBytes(),message.length(), InetAddress.getByName("10.1.255.255"), 2003);
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

    public userList sendHello(String usr){
        InetAddress ip;
        Timer timer;

        try {
            //Retrieving the MAC address
            ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();

            //Message format for the sendHello = "jean-michel|00:1B:44:11:3A:B7"
            String message = usr + "|" + Arrays.toString(mac);

            //Sending the sendHello package with username and mac address in broadcast mode
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket outPacket = new DatagramPacket(message.getBytes(),message.length(), InetAddress.getByName("10.1.255.255"), 2001);
            socket.setBroadcast(true);
            socket.send(outPacket);

            //Start value for the timer
            long startTime = System.currentTimeMillis();
            long elapsedTime = 0L;

            Thread userNameReceptionThread = new Thread(new Runnable(){
                @Override
                public void run(){
                    try{
                        DatagramSocket serverSocket = new DatagramSocket(2002);
                        while(true){

                            //Creating the buffer for incoming messages
                            byte[] buffer = new byte[1024];
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                            //Recover the datagram sent by client
                            serverSocket.receive(packet);

                            //Printing received message
                            String msg = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                            System.out.println(msg);

                            //Parse the received string in order to update the activeList properly
                            String[] data = msg.split("|");
                            userList.addElement(new userData(data[0], data[1]));

                            //Resetting datagram length
                            packet.setLength(buffer.length);
                        }
                    } catch (Exception e) {
                        System.out.println("Error while receiving the users info.");
                    }
                }
            });

            //Run the thread for the duration of the timer
            userNameReceptionThread.start();
            while (elapsedTime < 2000) {
                elapsedTime = (new Date()).getTime() - startTime;
            }
            userNameReceptionThread.interrupt();


        } catch( Exception e){
            System.out.println("getLocalhost failed");
        }

        //returns a list of active users, linked with their mac address
        return userList;
    }
}
