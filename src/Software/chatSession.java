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
    private boolean isNewMessage = false;
    private PrintWriter out;

    public chatSession(int port, userData otherUserData, boolean isServer){
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
            Thread connectionThread = new Thread(() -> {
                try {
                    BufferedReader bufferIn = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    while (true) {
                        String message = bufferIn.readLine();
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

    public boolean getIsNewMessage(){
        return isNewMessage;
    }

}
