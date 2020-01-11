package Session;

import Software.systemMessage;
import Software.systemMessageSender;
import Software.userData;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class session extends JFrame {

    //Attributes for the TCP protocol
    private PrintWriter out;

    //Attributes for the UI
    private DefaultListModel messageListModel;
    public String username;
    private userData otherUserData;
    private JTextArea messageDisplay;
    private JTextField text = new JTextField("Write a message...");
    private Socket connectionSocket;
    private BufferedReader bufferIn;

    //Attributes for the message fetching
    public File messagesFile;
    public File messagesJson;
    public JSONObject jsonObject = new JSONObject();
    public String userPath;

    static class Connected {
        public volatile boolean isConnected = true;
        public boolean getConnected () {
            return isConnected;
        }
        public void disconnect(){
            isConnected = false;
        }
    }
    final Connected controlConnected = new Connected();

    //Constructor. Will display the new window and start the reception thread.
    public session(userData myself, userData otherUser, int port,  boolean isServer){
        super();

        username = myself.getUsername();

        String correctPathName = "";
        String mac = otherUser.getMacAddress();
        for (int i = 0; i<mac.length(); i++){
            Character current = mac.charAt(i);
            if (!current.equals(':')) correctPathName += current;
        }
        System.out.println("The corrected version of the mac address is : " + correctPathName);

        otherUserData = otherUser;
        String otherUsername = otherUser.getUsername();
        String path = "conversationData/" + correctPathName;
        messagesFile = new File(path);
        String userPath = path + "/" + correctPathName + ".json";
        messagesJson = new File(userPath);


        //*****************************************************
        // THIS IS THE PART CONCERNING THE MESSAGE FETCH
        //*****************************************************
        if (!messagesFile.exists()) {
            boolean b = messagesFile.mkdir();

            PrintWriter writer;
            try {
                String filename = correctPathName + ".json";
                writer = new PrintWriter(filename, StandardCharsets.UTF_8);
            } catch (Exception e){
                System.out.println("Error while creating the json file");
                e.printStackTrace();
            }
        }
        else if (messagesFile.exists() && !messagesFile.isDirectory()){
            JSONArray a;
            try {
                JSONParser parser = new JSONParser();
                a = (JSONArray) parser.parse(new FileReader(messagesFile));

            } catch (Exception e) {
                System.out.println("Error. Could not find the message file");
                e.printStackTrace();
                a = null;
            }
            for (Object o : a){
                JSONObject messageData = (JSONObject) o;
                String message = (String) messageData.get("message");
                String isMe = (String) messageData.get("flag");
                if (isMe.equals("1")){
                    messageDisplay.append("\n" + username + " : " + message);
                }
                else if (isMe.equals("0")){
                    messageDisplay.append("\n" + otherUsername + " : " + message);
                }
            }
        }

        //*****************************************************
        // THIS IS THE PART CONCERNING THE WINDOW DISPLAY
        //*****************************************************

        //TODO : message display is still absolutely terrible. Might need to fix that.
        messageListModel = new DefaultListModel();
        JLabel test = new JLabel("oui");
        messageListModel.addElement(test);
        JList<JLabel> messageList = new JList<>();

        JButton sendButton = new JButton("Send");
        JPanel textAreaPanel = new JPanel();
        messageDisplay = new JTextArea("This is the start of your conversation with " + otherUsername + ".\n");
        messageDisplay.setEditable(false);
        JScrollPane messageArea = new JScrollPane(messageDisplay);

        this.setTitle(otherUsername);

        sendButton.setMnemonic(KeyEvent.VK_ENTER);
        sendButton.setPreferredSize(new Dimension(100, 40));
        text.setPreferredSize(new Dimension(250,40));

        textAreaPanel.add(text, BorderLayout.WEST);
        textAreaPanel.add(sendButton, BorderLayout.EAST);
        textAreaPanel.setPreferredSize(new Dimension(350,40));

        messageArea.setPreferredSize(new Dimension(400, 300));
        setPreferredSize(new Dimension(600,500));

        add(messageArea, BorderLayout.NORTH);
        add(textAreaPanel, BorderLayout.SOUTH);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        WindowListener exitListener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirm = JOptionPane.showOptionDialog(
                        null, "Are you sure you want to disconnect from current chat session ?",
                        "Confirmation", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (confirm == 0){
                    try {
                        systemMessageSender systemMessageSender = new systemMessageSender();
                        systemMessage systemMessage = new systemMessage("disconnect", myself, 0 );
                        systemMessageSender.sendSystemMessage(systemMessage, InetAddress.getByName(otherUserData.getIPAddress()), false, 3000);
                        controlConnected.disconnect();
                        System.out.println("Turned the controller to false");
                        connectionSocket.close();
                        System.out.println("Closed the socket");
                        dispose();
                        try (FileWriter file = new FileWriter(userPath)){
                            file.write(jsonObject.toJSONString());
                            file.flush();
                        } catch (IOException oskour) {
                            System.out.println("Error while writing in the json file");
                            oskour.printStackTrace();
                        }
                        //connectionThread.join();
                    } catch (SocketException se){
                        System.out.println("Error while setting up the dcSocket");
                        se.printStackTrace();
                    } catch (UnknownHostException he){
                        System.out.println("Unknown host for the disconnect message");
                        he.printStackTrace();
                    } catch (IOException ie){
                        System.out.println("Error while sending the message");
                        ie.printStackTrace();
                    }
                }
            }
        };
        addWindowListener(exitListener);

        text.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    if (text.getText() != null){
                        if (!text.getText().equals("")) {
                            try {
                                sendMessage(text.getText());
                                messageDisplay.append("\n" + username + " : " + text.getText());
                            } catch (Exception ex) {
                                messageDisplay.append("\nSorry, there was an error while trying to send the message.");
                                System.out.println("Failed to send the message");
                            }
                            text.setText("");
                        }
                    }
                }
            }
        });
        sendButton.addActionListener(e -> {
            if (text.getText() != null){
                if (!text.getText().equals("")) {
                    try {
                        sendMessage(text.getText());
                        messageDisplay.append("\n" + username + " : " + text.getText());
                    } catch (Exception ex) {
                        messageDisplay.append("\nSorry, there was an error while trying to send the message.");
                        System.out.println("Failed to send the message");
                    }
                    text.setText("");
                }
            }
        });

        this.pack();
        setVisible(true);
        //*****************************************************

        //*****************************************************
        // THIS IS THE PART CONCERNING THE TCP PROTOCOL
        //*****************************************************
        try {
            if (isServer) {
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Message before accept");
                connectionSocket = serverSocket.accept();
                System.out.println("Message after accept");
            } else {
                try {Thread.sleep(100);} catch (InterruptedException ie) {ie.printStackTrace();}
                connectionSocket = new Socket(InetAddress.getByName(otherUser.getIPAddress()), port);
            }
            this.out = new PrintWriter(connectionSocket.getOutputStream(), true);
            //Attributes for the TCP connection and the connection Thread
            Thread connectionThread = new Thread(() -> {
                try {
                    bufferIn = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    while (controlConnected.getConnected()) {
                        String message = bufferIn.readLine();
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        messageDisplay.append("\n" + otherUsername + " : " + message);
                        System.out.println("Message received : " + message);
                        if (!message.equals("null") && !message.equals("")) {
                            JSONObject jsonMessage = new JSONObject();
                            jsonMessage.put("message", message);
                            jsonMessage.put("timestamp", timestamp);
                            jsonMessage.put("flag", 0);
                            jsonObject.put("messagedata", jsonMessage);
                        }
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
        //*****************************************************


    }


    private void sendMessage(String message){
        out.println(message);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("Message sent : " + message);
        JSONObject jsonMessage = new JSONObject();
        if (!message.equals("null") && !message.equals("")) {
            jsonMessage.put("message", message);
            jsonMessage.put("timestamp", timestamp);
            jsonMessage.put("flag", 1);
            jsonObject.put("messagedata", jsonMessage);
        }
    }

    //Getters/Setters
    public userData getOtherUserData(){
        return otherUserData;
    }

    public String getUsername(){
        return username;
    }

    private void addMessage(boolean isMe, String message, String timestamp){
        JPanel messagePanel = new JPanel();
        JTextArea messageText = new JTextArea(message);
        if (isMe){
            Border border = BorderFactory.createLineBorder(Color.blue);
            TitledBorder titledBorder = BorderFactory.createTitledBorder(border, timestamp + " - You :");
            titledBorder.setTitleJustification(TitledBorder.RIGHT);
            titledBorder.setTitleColor(Color.blue);
        }
        else {
            Border border = BorderFactory.createLineBorder(Color.red);
            TitledBorder titledBorder = BorderFactory.createTitledBorder(border, timestamp + " - " + otherUserData.getUsername() + " :");
            titledBorder.setTitleJustification(TitledBorder.LEFT);
            titledBorder.setTitleColor(Color.red);
        }
        messagePanel.add(messageText);
        messageText.setLineWrap(true);
        messageText.setWrapStyleWord(true);
        messageText.setEditable(false);


    }
    public void closeSession(){
        try {
            System.out.println("Entered the closeSession method...");
            //bufferIn.close();
            System.out.println("Closed the buffer");
            controlConnected.disconnect();
            System.out.println("Turned the controller to false");
            connectionSocket.close();
            System.out.println("Closed the socket");
            try (FileWriter file = new FileWriter(userPath)){
                file.write(jsonObject.toJSONString());
                file.flush();
            } catch (IOException oskour) {
                System.out.println("Error while writing in the json file");
                oskour.printStackTrace();
            }
            this.dispose();
        } catch (IOException e){
            System.out.println("Error while closing the TCP connection socket");
            e.printStackTrace();
        }
    }

}
