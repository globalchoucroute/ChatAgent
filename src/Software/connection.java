package Software;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Date;
import java.util.Timer;
public class connection {

    //Attributes
    private String username;
    protected String[][] activeList = new String[2][];

    public connection (){

    }
    //Methods
    boolean checkUsername(String usr){


        return true;
    }

    String[][] sendHello(String usr){
        InetAddress ip;
        Timer timer;

        try {
            ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            String message = usr + "|" + mac.toString();

            DatagramSocket socket = new DatagramSocket();
            DatagramPacket outPacket = new DatagramPacket(message.getBytes(),message.length(), InetAddress.getByName("10.1.255.255"), 2001);
            socket.setBroadcast(true);
            socket.send(outPacket);

            long startTime = System.currentTimeMillis();
            long elapsedTime = 0L;

            Thread t = new Thread(new Runnable(){
                public void run(){
                    try{
                        DatagramSocket serverSocket = new DatagramSocket(2002);
                        while(true){

                            //Creating the buffer for incoming messages
                            byte[] buffer = new byte[1024];
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                            //Recover the datagram sent by client
                            serverSocket.receive(packet);

                            //Printing received message
                            String msg = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                            System.out.println(msg);
                            for (int i=0; i<msg.length(); i++){
                                while (Character.isLetter(msg.charAt(i))){

                                }
                            }


                            //Resetting datagram length
                            packet.setLength(buffer.length);
                        }
                    } catch (Exception e) {
                        System.out.println("Error while receiving the users info.");
                    }
                }
            });

            t.start();
            while (elapsedTime < 2000) {
                elapsedTime = (new Date()).getTime() - startTime;
            }


        } catch( Exception e){
            System.out.println("getLocalhost failed");
        }
        this.activeList = null;
        return activeList;
    }
}
