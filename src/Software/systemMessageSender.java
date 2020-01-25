package Software;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class systemMessageSender {

    //This class will be used when sending a system message to the other users
    public systemMessageSender(){}

    public void sendSystemMessage(systemMessage systemMessage, InetAddress destination, boolean isBroadcast, int port) {

        try {
            //Set up for the serialized message send
            DatagramSocket socket = new DatagramSocket();
            ByteArrayOutputStream outByte;

            //Send the serialized message to other listening users (listening is made in a separate thread on port 3000)
            try {
                outByte = new ByteArrayOutputStream();
                byte[] objectSerialized;
                ObjectOutputStream objOut = new ObjectOutputStream(outByte);
                objOut.writeObject(systemMessage);
                objectSerialized = outByte.toByteArray();
                DatagramPacket outPacket = new DatagramPacket(objectSerialized, objectSerialized.length, destination, port);
                socket.setBroadcast(isBroadcast);
                try {
                    socket.send(outPacket);
                    System.out.println("Message has been sent to " + destination + " with instruction " + systemMessage.instruction);
                    outByte.close();
                    objOut.close();
                } catch (IOException io) {
                    System.out.println("Error while setting up the socket for the sendHello");
                    io.printStackTrace();
                }
                socket.close();
            } catch (UnknownHostException uhe) {
                System.out.println("Could not find the host");
                uhe.printStackTrace();
            } catch (IOException e) {
                System.out.println("Error while sending the message");
                e.printStackTrace();
            }
        } catch (SocketException se) {
            System.out.println("Error while setting up the socket");
            se.printStackTrace();
        }

    }
}
