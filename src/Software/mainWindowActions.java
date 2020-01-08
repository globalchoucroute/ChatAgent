package Software;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Enumeration;
import Session.session;

import GUI.chatWindow;

public class mainWindowActions {

    //Attributes
    protected String username;
    public userList contactList;

    final connection.Control control = new connection.Control();

    //Constructor
    public mainWindowActions(String username, userList userList, sessionTable sessionTable) {

        Thread messageReception = new Thread(() -> {
            try {
                DatagramSocket serverSocket = new DatagramSocket(3000);

                //This is the message we receive through port 3000
                String message;

                //Contains the different values of the message
                String[] messageData;


                int port;
                InetAddress address;
                String theirUsername;
                String answerMessage;
                userData otherUser;
                while(true) {

                    //Debugging
                    System.out.println("List of online users as it is during the message reception thread");
                    for (int i = 0; i<userList.getLength(); i++){
                        System.out.println("*************************************");
                        System.out.println("User " + i + " :");
                        System.out.println("Username : " + userList.getUser(i).getUsername());
                        System.out.println("MAC address : " + userList.getUser(i).getMacAddress());
                        System.out.println("IP address : " + userList.getUser(i).getIPAddress());
                    }
                    System.out.println("*************************************");


                    //Creating the buffer for incoming messages
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    System.out.println("Waiting for a packet reception");
                    //Recover the datagram sent by client
                    serverSocket.receive(packet);
                    message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    System.out.println("Received message : " + message);

                    //Setup the variables for treatment
                    address = packet.getAddress();
                    messageData = message.split(" ");
                    theirUsername = messageData[0];
                    Character index = theirUsername.charAt(0);
                    if (index.equals("0")){ System.out.println("Received a non treatable message"); }

                    else {//Start the message treatment
                        System.out.println("messageTreatment running");
                        System.out.println("Username : " + messageData[0] + "\nInstruction : " + messageData[1]);

                        switch (messageData[1]) {
                            case "check":
                                if (theirUsername.equals(username)) {
                                    try {
                                        DatagramSocket clientSocket = new DatagramSocket();
                                        DatagramPacket outPacket = new DatagramPacket(username.getBytes(), username.length(), address, 2004);
                                        clientSocket.send(outPacket);
                                    } catch (Exception e) {
                                        System.out.println("Could not send the message");
                                    }
                                }
                                break;
                            case "begin":
                                port = Integer.parseInt(messageData[2]);
                                System.out.println("Received the begin message with username  = " + theirUsername + " and port = " + port);
                                otherUser = userList.getUserByName(theirUsername);
                                System.out.println("The client needs to send to : " + otherUser.getIPAddress() + "\nPort : " + port);
                                //new chatWindow(username, theirUsername, new chatSession(port, otherUser, false));
                                sessionTable.addSession(new session(username, otherUser, port, false));
                                System.out.println("Session began on client's side with port : " + port);
                                break;
                            case "disconnect":
                                //session currentSession = sessionTable.getSessionByName(theirUsername);
                                sessionTable.closeSession(theirUsername);
                                System.out.println("print random");
                                //System.out.println("Going to close session with " + currentSession.otherUserData.getUsername());
                                //currentSession.closeSession();
                                //System.out.println("Finished closing session with " + currentSession.otherUserData.getUsername());
                                break;
                            case "bye":
                                System.out.println("Received the disconnect message");
                                otherUser = userList.getUserByName(theirUsername);
                                userList.deleteElement(otherUser);
                                break;
                            default:
                                try {
                                    //Check if the user already exists in the list
                                    if (userList.exists(messageData[1])) {
                                        userList.modifyUsername(messageData[1], theirUsername);
                                    } else {
                                        //Retrieving the MAC address
                                        InetAddress ip = InetAddress.getLocalHost();
                                        //NetworkInterface network = NetworkInterface.getByName("eth0");
                                        NetworkInterface network = NetworkInterface.getByName("eth4");
                                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                                        while (addresses.hasMoreElements()) {
                                            InetAddress currentAddress = addresses.nextElement();
                                            if (currentAddress instanceof Inet4Address && !currentAddress.isLoopbackAddress()) {
                                                ip = currentAddress;
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

                                        //Message format for the sendHello = "jean-michel 00:1B:44:11:3A:B7 192.168.0.1"
                                        answerMessage = username + " " + macs + " " + ips;
                                        System.out.println("RecvHello message : " + answerMessage);

                                        //Returning a packet with our info, so that the new user can create their active list
                                        DatagramSocket socket = new DatagramSocket();
                                        DatagramPacket outPacket = new DatagramPacket(answerMessage.getBytes(), answerMessage.length(), address, 2002);
                                        socket.send(outPacket);
                                        socket.close();

                                        //Update the activeList table
                                        userList.addElement(new userData(messageData[0], messageData[1], messageData[2]));
                                        System.out.println("New user added : " + messageData[0]);
                                        break;
                                    }
                                } catch (SocketException se){
                                    System.out.println("Error while setting up the recvHelloSocket");
                                    se.printStackTrace();
                                } catch (UnknownHostException he){
                                    System.out.println("Unknown host for the recvHello message");
                                    he.printStackTrace();
                                } catch (IOException ie){
                                    System.out.println("Error while sending the message");
                                    ie.printStackTrace();
                                }
                                break;
                        }
                    }
                }
            } catch (Exception e){
                System.out.println("Error while setting up the reception socket (mainWindowActions)");
                e.printStackTrace();
            }
        });
        this.username = username;
        messageReception.start();

    }

    //Methods
    public chatSession beginSession(String usr, int port, userData otherUserData, DatagramSocket d) {
        try {
            String msg = usr + " begin "+ port;
            System.out.println("beginSession message sent :" + msg);
            DatagramPacket outPacket = new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName(otherUserData.getIPAddress()), 3000);
            d.send(outPacket);
            return new chatSession(port, otherUserData, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public session beginChatSession(String username, int port, userData otherUserData, DatagramSocket d, sessionTable sessionTable){
        try {
            String msg = username + " begin "+ port;
            System.out.println("beginSession message sent :" + msg);
            DatagramPacket outPacket = new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName(otherUserData.getIPAddress()), 3000);
            d.send(outPacket);
            System.out.println("Session began on server's side with port : " + port);
            session session = new session(username, otherUserData, port,true);
            sessionTable.addSession(session);
            return session;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void disconnect(){
        try{
            String message = username + " disconnect";
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket outPacket = new DatagramPacket(message.getBytes(), message.length(), InetAddress.getByName("10.1.255.255"), 3000);
            socket.setBroadcast(true);
            socket.send(outPacket);
        } catch (Exception e){
            System.out.println("Error while sending the disconnect message");
            e.printStackTrace();
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
            //Message format for the username check : "jean-michel check"
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

}
