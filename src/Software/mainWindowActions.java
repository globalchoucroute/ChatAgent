package Software;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import Session.session;
import java.net.*;


public class mainWindowActions {

    //Attributes
    protected String username;
    private userData myself;
    private userList userList;

    //Constructor
    public mainWindowActions(userData myself, userList u, sessionTable sessionTable) {
        userList = u;
        this.username = myself.getUsername();
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
                userData otherUser;
                String instruction;

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
                    Object systemMsg = null;
                    //Recover the datagram sent by client
                    try {
                        serverSocket.receive(packet);
                        ByteArrayInputStream inStream = new ByteArrayInputStream(packet.getData());
                        ObjectInput inObj = new ObjectInputStream(inStream);
                        systemMsg = inObj.readObject();
                    } catch (IOException | ClassNotFoundException e){
                        e.printStackTrace();
                    }

                    if (systemMsg != null && systemMsg.getClass().toString().equals("class Software.systemMessage")) {
                        receivedSystemMessage = (systemMessage) systemMsg;
                        if (!receivedSystemMessage.userData.getIPAddress().equals(myself.getIPAddress())){
                            //Set up the useful variables for later use
                            instruction = receivedSystemMessage.instruction;
                            otherUser = receivedSystemMessage.userData;
                            theirUsername = otherUser.getUsername();
                            try {
                                address = InetAddress.getByName(receivedSystemMessage.userData.getIPAddress());
                            } catch (UnknownHostException uhe){
                                address = null;
                                uhe.printStackTrace();
                            }
                            port = receivedSystemMessage.port;

                            System.out.println("System message received with instruction " + instruction);

                            switch (instruction) {
                                case "check":
                                    if (theirUsername.equals(myself.getUsername())) {
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
                                    sessionTable.addSession(new session(this.myself, otherUser, port, false));
                                    System.out.println("Session began on client's side with port : " + port);
                                    break;
                                case "disconnect":
                                    sessionTable.closeSession(theirUsername);
                                    break;
                                case "bye":
                                    System.out.println("Received the disconnect message");
                                    otherUser = userList.getUserByName(theirUsername);
                                    userList.deleteElement(otherUser);
                                    break;
                                case "change":
                                    userList.modifyUsername(otherUser.getMacAddress(), theirUsername);
                                    sessionTable.updateSessionOtherUserdata(otherUser);
                                    break;
                                case "hello":
                                    System.out.println("Received sendHello message... Adding new user with username " + theirUsername);
                                    systemMessageSender systemMessageSender = new systemMessageSender();
                                    try {
                                        systemMessageSender.sendSystemMessage(new systemMessage("hi", myself, port), InetAddress.getByName(otherUser.getIPAddress()), false, 2002);
                                    } catch (UnknownHostException uhe){
                                        uhe.printStackTrace();
                                    }
                                    //Update the activeList table
                                    userList.addElement(otherUser);

                                    System.out.println("New user added : " + otherUser.getUsername());
                                    break;
                                case "Available":
                                    userList.modifyStatus(otherUser.getMacAddress(), "Available");
                                    break;
                                case "Away":
                                    userList.modifyStatus(otherUser.getMacAddress(), "Away");
                                    break;
                                case "Busy":
                                    userList.modifyStatus(otherUser.getMacAddress(), "Busy");
                                default:
                                    break;
                            }
                        }
                    }
                }
            } catch (SocketException se){
                se.printStackTrace();
            }
        });
        messageReception.start();

    }

    /**
     * Sends a system message asking the other user to start a chat session.
     * @param port is the port on which the session should be started.
     * @param otherUserData is the data of the user we're sending the message to.
     * @param sessionTable is the general session table. When starting a session, we also need to add it to the table.
     */
    public void beginChatSession(int port, userData otherUserData, sessionTable sessionTable){
        try {
            systemMessageSender systemMessageSender = new systemMessageSender();
            systemMessageSender.sendSystemMessage(new systemMessage("begin", myself, port), InetAddress.getByName(otherUserData.getIPAddress()), false, 3000);
            System.out.println("beginSession message sent");
            session session = new session(myself, otherUserData, port,true);
            sessionTable.addSession(session);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the desired username is available by checking through the entire user list. In case it is, sends a system message informing the other
     * users of the change.
     * @param newName is the new username the user would like to take.
     */
    public void changeUsername(String newName){
        if (userList.isAvailable(newName)){
            try {
                //Send the message via the systemMessageSender
                systemMessageSender systemMessageSender = new systemMessageSender();
                systemMessageSender.sendSystemMessage(new systemMessage("change", new userData(newName, myself.getMacAddress(), myself.getIPAddress(),myself.getStatus()), 0), InetAddress.getByName("255.255.255.255"), true, 3000);
                URL url = new URL("https://srv-gei-tomcat.insa-toulouse.fr/DeloffreGarnier?username="+newName+"&status="+myself.getStatus()+"&mac="+myself.getMacAddress());
                URLConnection con = url.openConnection();
                HttpURLConnection http = (HttpURLConnection)con;
                http.setRequestMethod("PUT");
                http.setDoOutput(true);
                http.connect();
                System.out.println("put request sent");
                myself.setUsername(newName);

            } catch( Exception e){
                System.out.println("getLocalhost failed");
            }
        }
    }

    public void modifyStatus(String status) {
        myself.setStatus(status);
        try {

            URL url = new URL("https://srv-gei-tomcat.insa-toulouse.fr/DeloffreGarnier?username=" + myself.getUsername() + "&status=" + status + "&mac=" + myself.getMacAddress());
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("PUT");
            http.setDoOutput(true);
            http.connect();
            System.out.println("put request sent");
        } catch (Exception e){
            System.out.println("update status failed");
        }
    }
}
