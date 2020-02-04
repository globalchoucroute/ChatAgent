package Software;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.ThreadLocalRandom;

public class connection {

    //Attributes
    private userList userList = new userList();
    private String macs;
    private String ips;
    public String username;

    //Control class for the checkUsername shared boolean
    private static class Control {
        private volatile boolean unique = true;
        private void setTrue(){
            unique = true;
        }
    }
    private final Control control = new Control();


    public connection (){
        InetAddress ip;
        this.macs = "";
        this.ips = "";
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            OUTER : for (NetworkInterface interface_ : Collections.list(interfaces)) {
                if (interface_.isLoopback()) continue;
                if (!interface_.isUp()) continue;
                Enumeration<InetAddress> addresses = interface_.getInetAddresses();
                for (InetAddress address : Collections.list(addresses)) {
                    if (address instanceof Inet6Address) continue;
                    else if (!address.isReachable(3000)) continue;
                    try (SocketChannel socket = SocketChannel.open()){
                        socket.socket().setSoTimeout(3000);
                        //Connect to a random port, otherwise connection may be refused
                        socket.bind(new InetSocketAddress(address, ThreadLocalRandom.current().nextInt(9000, 10001)));
                        socket.connect(new InetSocketAddress("google.com", 80));
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                        continue;
                    }
                    byte[] mac = interface_.getHardwareAddress();
                    ip = address;
                    //Formatting the message for a clean display
                    StringBuilder sb = new StringBuilder(18);
                    for (byte b : mac) {
                        if (sb.length() > 0)
                            sb.append(':');
                        sb.append(String.format("%02x", b));
                    }

                    this.macs = sb.toString();
                    this.ips = ip.toString().substring(1);

                    break OUTER;
                }
            }
        } catch (UnknownHostException uhe) {
            System.out.println("Could not find the host");
            uhe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Methods

    /**
     * Checks if the chosen username is available.
     * @param usr is the chosen username.
     * @return true if the chosen username is available, false if another user has already taken it.
     */
    public boolean checkUsername(String usr){
        try {

            //Send the message via the systemMessageSender
            systemMessageSender systemMessageSender = new systemMessageSender();
            systemMessageSender.sendSystemMessage(new systemMessage("check", new userData(usr, macs, ips, "Available"), 0), InetAddress.getByName("255.255.255.255"), true, 3000);

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

            userNameReceptionThread.start();
            Thread.sleep(500);
            userNameReceptionThread.interrupt();


        } catch( Exception e){
            System.out.println("getLocalhost failed");
        }

        //returns true if no message has been received
        return control.unique;
    }

    /**
     * Sends a System Message with our user data (username, mac address, IP address) to every connected user.
     * @param usr is the chosen username.
     * @return a list of data from every currently connected user.
     */
    public userList sendHello(String usr){
        try {
            //Send the message via the systemMessageSender
            systemMessageSender systemMessageSender = new systemMessageSender();
            systemMessageSender.sendSystemMessage(new systemMessage("hello", new userData(usr, macs, ips,"Available"), 0), InetAddress.getByName("255.255.255.255"), true, 3000);


            Thread userNameReceptionThread = new Thread(() -> {
                try{
                    DatagramSocket serverSocket = new DatagramSocket(2002);
                    while(true){

                        //Creating the buffer for incoming messages
                        byte[] buffer = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                        System.out.println("Waiting for a packet reception");
                        //Recover the datagram sent by client
                        serverSocket.receive(packet);
                        ByteArrayInputStream inStream = new ByteArrayInputStream(packet.getData());
                        ObjectInput inObj = new ObjectInputStream(inStream);
                        Object systemMsg = inObj.readObject();

                        if (systemMsg.getClass().toString().equals("class Software.systemMessage")) {
                            systemMessage receivedSystemMessage = (systemMessage) systemMsg;
                            userList.addElement(receivedSystemMessage.userData);
                        }

                        //Resetting datagram length
                        packet.setLength(buffer.length);
                    }
                } catch (Exception e) {
                    System.out.println("Error while receiving the users info.");
                }
            });

            //Run the thread for the duration of the timer
            userNameReceptionThread.start();
            Thread.sleep(1000);
            userNameReceptionThread.interrupt();


        } catch( Exception e){
            System.out.println("getLocalhost failed");
        }
        this.username = usr;
        //returns a list of active users, linked with their mac & IP addresses
        return userList;
    }

    /**
     * @return our own user data.
     */
    public userData getPersonalUserData(){
        return new userData(username, macs, ips,"Available");
    }
    public void setControlTrue(){
        control.setTrue();
    }
}
