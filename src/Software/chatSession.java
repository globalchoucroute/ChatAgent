package Software;

import javax.swing.JButton;
import javax.xml.crypto.Data;
import java.net.*;

public class chatSession {

    //Attributes
    String[][] messageList = new String[2][];
    private DatagramSocket clientSock;

    //Constructor
    public chatSession(DatagramSocket ds){
        clientSock = ds;
        Thread t = new Thread(new Runnable(){
            public void run(){
                try{
                    DatagramSocket serverSocket = new DatagramSocket(2345);

                    while(true){
                        //Creating the buffer for incoming messages
                        byte[] buffer = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                        //Recover the datagram sent by client
                        serverSocket.receive(packet);

                        //Printing received message
                        String msg = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                        System.out.println(msg);

                        //Resetting datagram length
                        packet.setLength(buffer.length);

                    }
                } catch (Exception e) { System.out.println("pb recv message chatsession");}
            }
        });
        t.start();
        /*try {
            InetAddress localaddress = InetAddress.getLocalHost();
            for (int i=0; i<3; i++){
                sendMessage(buildPDU("message num "+i, localaddress, 2345));

            }
        } catch (Exception e) {
            System.out.println("raté");
        }*/


    }

    //Methods
    public DatagramPacket buildPDU(String msg, InetAddress address, int port){
        DatagramPacket outPacket = new DatagramPacket(msg.getBytes(),msg.length(), address, port);
        return outPacket;
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
