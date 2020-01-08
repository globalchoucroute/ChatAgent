package Software;

import java.net.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Timer;
public class connection {

    //Attributes
    public userList userList = new userList();

    //Control class for the checkUsername shared boolean
    static class Control {
        public volatile boolean unique = true;
        public boolean getUnique () {
            return unique;
        }
        public void setTrue(){
            unique = true;
        }
    }
    final Control control = new Control();


    public connection (){
    }

    //Methods
    public boolean checkUsername(String usr){
        try {
            //Message format for the username check : "jean-michel|check"
            String message = usr + " check";
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

    public userList sendHello(String usr){
        InetAddress ip;
        Timer timer;

        try {
            //Retrieving the MAC address
            ip = Inet4Address.getLocalHost();
            //NetworkInterface network = NetworkInterface.getByName("eth0");
            NetworkInterface network = NetworkInterface.getByName("eth4");
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
            String message = usr + " " + macs + " " + ips;
            System.out.println("sendHello message : "+message);

            //Sending the sendHello package with username and mac address in broadcast mode
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket outPacket = new DatagramPacket(message.getBytes(),message.length(), InetAddress.getByName("10.1.255.255"), 3000);
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
                            String[] data = msg.split(" ");
                            userList.addElementInit(new userData(data[0], data[1], data[2]));
                            System.out.println("New user added : " + data[0]);

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
        //returns a list of active users, linked with their mac & IP addresses
        return userList;
    }

    public void setControlTrue(){
        control.setTrue();
    }
}
