package Software;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;


public class chatSession {

    //Attributes
    String[][] messageList = new String[2][];
    private DatagramSocket clientSock;
    public InetAddress ipDest;
    public String macAddress;
    public static int recPort = 6000;

    // TODO : Passage en TCP parce que UDP c'est rincé
    //Constructor
    public chatSession(DatagramSocket ds, userData userData){
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
    }

    String receiveMessage(){
        return "";
    }

    String retrieveTimeStamp(int indexMsg){
        return "";
    }

    void saveMessage(String msg){
    }
}
