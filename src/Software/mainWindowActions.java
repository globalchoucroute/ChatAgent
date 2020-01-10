package Software;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Enumeration;
import Session.session;

import GUI.chatWindow;

public class mainWindowActions {

    //Attributes
    protected String username;
    public userData myself;
    final connection.Control control = new connection.Control();

    //Constructor
    public mainWindowActions(userData myself, userList userList, sessionTable sessionTable) {

        this.myself = myself;
        //This thread is meant to listen to system messages, such as :
        //  - A user wants to start a chat session
        //  - A user disconnects from the chat system
        // And so on...
        Thread messageReception = new Thread(() -> {
            try {
                DatagramSocket serverSocket = new DatagramSocket(3000);

                //This is the message we receive through port 3000
                systemMessage receivedSystemMessage;

                int port;
                InetAddress address;
                String theirUsername;
                String answerMessage;
                userData otherUser;
                String instruction;
                ByteArrayOutputStream outByte;
                byte[] objectSerialized;

                while (true) {

                    //Debugging
                    System.out.println("List of online users as it is during the message reception thread");
                    for (int i = 0; i < userList.getLength(); i++) {
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
                    ByteArrayInputStream inStream = new ByteArrayInputStream(packet.getData());
                    ObjectInput inObj = new ObjectInputStream(inStream);
                    Object systemMsg = inObj.readObject();
                    if (systemMsg.getClass().toString().equals("class systemMessage")) {
                        receivedSystemMessage = (systemMessage) systemMsg;

                        //Set up the useful variables for later use
                        instruction = receivedSystemMessage.instruction;
                        otherUser = receivedSystemMessage.userData;
                        theirUsername = otherUser.getUsername();
                        address = InetAddress.getByName(receivedSystemMessage.userData.getIPAddress());
                        port = receivedSystemMessage.port;

                        switch (instruction) {
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
                            case "change":
                                userList.modifyUsername(otherUser.getMacAddress(), theirUsername);
                            case "hello":
                                try {
                                    DatagramSocket socket = new DatagramSocket();
                                    outByte = new ByteArrayOutputStream();
                                    ObjectOutputStream objOut = new ObjectOutputStream(outByte);
                                    objOut.writeObject(new systemMessage("hello", this.myself, 0));
                                    objectSerialized = outByte.toByteArray();
                                    DatagramPacket outPacket = new DatagramPacket(objectSerialized, objectSerialized.length, address, 2002);
                                    try {
                                        socket.send(outPacket);
                                        outByte.close();
                                        objOut.close();
                                        socket.close();
                                    } catch (IOException io) {
                                        System.out.println("Error while setting up the socket for the sendHello");
                                        io.printStackTrace();
                                    }
                                } catch (UnknownHostException uhe) {
                                    System.out.println("Could not find the host");
                                    uhe.printStackTrace();
                                } catch (IOException e) {
                                    System.out.println("Error while sending the message");
                                    e.printStackTrace();
                                }

                                //Update the activeList table
                                userList.addElement(otherUser);

                                System.out.println("New user added : " + otherUser.getUsername());
                                break;
                            default:
                                break;
                        }
                    }
                }
            }  catch (IOException io) {
                System.out.println("Error while setting up the socket for the message recovery");
                io.printStackTrace();
            } catch (ClassNotFoundException cnfe) {
                System.out.println("Error : class was not found");
                cnfe.printStackTrace();
            }
        });

        this.username = username;
        messageReception.start();

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

            //Send the message via the systemMessageSender
            systemMessageSender systemMessageSender = new systemMessageSender();
            systemMessageSender.sendSystemMessage(new systemMessage("check", new userData(usr, myself.getMacAddress(), myself.getIPAddress()), 0), InetAddress.getByName("255.255.255.255"), true, 3000);

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
