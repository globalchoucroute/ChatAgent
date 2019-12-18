package Software;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Observable;


public class chatSession {

    //Attributes

    String[][] messageList = new String[2][];
    public ArrayList messages;
    public static int index = 0;
    public boolean isNewMessage = false;
    public String receivedMessage = "";
    public PrintWriter out;
    public String otherUsername;
    Thread connectionThread;

    public chatSession(int port, userData otherUserData, boolean isServer){
        otherUsername = otherUserData.getUsername();
        try {
            Socket connectionSocket;
            if (isServer) {
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Message before accept");
                connectionSocket = serverSocket.accept();
                System.out.println("Message after accept");
            } else {
                connectionSocket = new Socket(InetAddress.getByName(otherUserData.getIPAddress()), port);
            }
            this.out = new PrintWriter(connectionSocket.getOutputStream(), true);
            connectionThread = new Thread(() -> {
                try {
                    BufferedReader bufferIn = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    while(true){
                        String message = bufferIn.readLine();
                        isNewMessage = true;
                        System.out.println("Message received : " + message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            connectionThread.start();
            System.out.println("Connection thread started");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message){
        out.println(message);
        System.out.println("Message sent : " + message);
    }


    public ArrayList getMessages(){
        ArrayList temp = new ArrayList();
        for (int i = index; i<messages.size(); i++){
            temp.add(messages.get(i));
        }
        index = messages.size();
        isNewMessage = false;
        return temp;
    }

    public String getOtherUsername(){
        return otherUsername;
    }

    public boolean getIsNewMessage(){
        return isNewMessage;
    }

    public void endChatSession(String username){
        if (username.equals(otherUsername)) {
            connectionThread.interrupt();
        }
    }

    String retrieveTimeStamp(int indexMsg){
        return "";
    }

    void saveMessage(String msg){
    }
}
