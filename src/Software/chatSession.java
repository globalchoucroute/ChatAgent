package Software;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;


public class chatSession {

    //Attributes
    String[][] messageList = new String[2][];
    private DatagramSocket clientSock;
    public InetAddress ipDest;
    public String macAddress;
    public static int recPort = 6000;
    public PrintWriter out;

    // TODO : Passage en TCP parce que UDP c'est rincé
    public chatSession(int port, userData otherUserData, boolean isServer){
        try {
            Socket connectionSocket;
            if (isServer) {
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Message avant accept");
                connectionSocket = serverSocket.accept();
                System.out.println("Message après accept");
            } else {
                connectionSocket = new Socket(InetAddress.getByName(otherUserData.getIPAddress()), port);
            }
            this.out = new PrintWriter(connectionSocket.getOutputStream(), true);
            Thread connectionThread = new Thread(() -> {
                try {
                    while(true){
                        BufferedReader bufferIn = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                        String message = bufferIn.readLine();
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            connectionThread.start();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message){
        out.println(message);
    }

    /*public chatSession(DatagramSocket ds, userData userData){
        clientSock = ds;

        //Recover the IP address from the user info
        String ipDestName = userData.getIPAddress();
        try {
            this.ipDest = InetAddress.getByName(ipDestName);
            this.macAddress = userData.getMacAddress();
        } catch (Exception e){
            e.printStackTrace();
        }

        Thread t = new Thread(() -> {
            try{
                DatagramSocket serverSocket = new DatagramSocket(recPort);

                while(true){
                    //Creating the buffer for incoming messages
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    //Recover the datagram sent by client
                    serverSocket.receive(packet);

                    //Printing received message
                    String msg = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    //System.out.println(msg);

                    //Resetting datagram length
                    packet.setLength(buffer.length);

                }
            } catch (Exception e) { System.out.println("Failed to recover message (chatSession)");}
        });
        t.start();
        recPort++;
    }

    //Methods
    public DatagramPacket buildPDU(String msg, int port){
        return new DatagramPacket(msg.getBytes(),msg.length(), ipDest, port);
    }

    public void sendMessage(DatagramPacket pdu){
        try {
            this.clientSock.send(pdu);
        } catch (Exception e) {
            System.out.println("Failed to send the message :/");
        }
    }*/

    String retrieveTimeStamp(int indexMsg){
        return "";
    }

    void saveMessage(String msg){
    }
}
