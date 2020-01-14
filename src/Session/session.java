package Session;

import Software.Message.message;
import Software.Message.textMessage;
import Software.systemMessage;
import Software.systemMessageSender;
import Software.userData;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class session extends JFrame {

    //Attributes for the TCP protocol
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private textMessage mainMessage = new textMessage("");
    //Attributes for the UI
    public String username;
    private userData otherUserData;
    private JTextArea messageDisplay;
    private JTextField text = new JTextField("Write a message...");
    private Socket connectionSocket;
    private BufferedReader bufferIn;
    private JPanel messageDisplayPane;
    private JScrollPane messageArea;

    //Attributes for the message fetching
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private String userPath;

    static class Connected {
        volatile boolean isConnected = true;
        boolean getConnected () {
            return isConnected;
        }
        void disconnect(){
            isConnected = false;
        }
    }
    private final Connected controlConnected = new Connected();

    //Constructor. Will display the new window and start the reception thread.
    public session(userData myself, userData otherUser, int port,  boolean isServer){
        super();

        username = myself.getUsername();

        StringBuilder correctPathName = new StringBuilder();
        String mac = otherUser.getMacAddress();
        for (int i = 0; i<mac.length(); i++){
            Character current = mac.charAt(i);
            if (!current.equals(':')) correctPathName.append(current);
        }

        System.out.println("The corrected version of the mac address is : " + correctPathName);

        otherUserData = otherUser;
        String otherUsername = otherUser.getUsername();
        userPath = "conversationData/" + correctPathName + ".json";
        //messagesFile = new File(userPath);


        //*****************************************************
        // THIS IS THE PART CONCERNING THE MESSAGE FETCH
        //*****************************************************
        /*try {
            jsonObject = (JSONObject) new JSONParser().parse(new FileReader(userPath));
            jsonArray = (JSONArray) jsonObject.get("messages");
            for (Object o : jsonArray){
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
        } catch (FileNotFoundException e) {
            //The file does not exist, we create it
            jsonObject = new JSONObject();
            jsonArray = new JSONArray();
            jsonObject.put("messages", jsonArray);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }*/

        //*****************************************************
        // THIS IS THE PART CONCERNING THE WINDOW DISPLAY
        //*****************************************************
        messageDisplayPane = new JPanel();
        messageDisplayPane.setLayout(new BoxLayout(messageDisplayPane, BoxLayout.Y_AXIS));
        messageDisplayPane.setBackground(Color.white);
        setBackground(Color.white);
;

        JButton sendButton = new JButton("Send");
        JPanel textAreaPanel = new JPanel();
        messageDisplay = new JTextArea("This is the start of your conversation with " + otherUsername + ".\n");
        messageDisplay.setEditable(false);
        messageArea = new JScrollPane(messageDisplayPane);
        messageArea.setWheelScrollingEnabled(true);
        messageArea.setAutoscrolls(true);
        messageArea.setVerticalScrollBarPolicy(messageArea.VERTICAL_SCROLLBAR_AS_NEEDED);
        var ref = new Object() {
            int verticalScrollBarMaximumValue = messageArea.getVerticalScrollBar().getMaximum();
        };
        messageArea.getVerticalScrollBar().addAdjustmentListener(e->{
            if ((ref.verticalScrollBarMaximumValue - e.getAdjustable().getMaximum()) == 0) return;
            e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            ref.verticalScrollBarMaximumValue = messageArea.getVerticalScrollBar().getMaximum();
        });
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
                        dispose();
                        /*try (FileWriter file = new FileWriter(userPath)){
                            file.write(jsonObject.toJSONString());
                            file.flush();
                        } catch (IOException oskour) {
                            System.out.println("Error while writing in the json file");
                            oskour.printStackTrace();
                        }*/
                        //connectionThread.join();
                    } catch (UnknownHostException he){
                        System.out.println("Unknown host for the disconnect message");
                        he.printStackTrace();
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
                                mainMessage = new textMessage(text.getText());
                                sendMessage(mainMessage);
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
                        mainMessage = new textMessage(text.getText());
                        sendMessage(mainMessage);
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
            //this.out = new PrintWriter(connectionSocket.getOutputStream(), true);
            this.out = new ObjectOutputStream(connectionSocket.getOutputStream());
            this.in = new ObjectInputStream(connectionSocket.getInputStream());

            //Attributes for the TCP connection and the connection Thread
            Thread connectionThread = new Thread(() -> {
                try {
                    textMessage message = null;
                    //bufferIn = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    while (controlConnected.getConnected()) {
                        try {
                            message = (textMessage) in.readObject();
                            System.out.println("Message received : " + message.content);
                        } catch (ClassNotFoundException e) {e.printStackTrace();}
                        //String message = bufferIn.readLine();
                        if (message != null && message.content != null && !message.content.equals("")) {
                            addMessage(false, message.content);
                            JSONObject jsonMessage = new JSONObject();
                            /*jsonMessage.put("message", message);
                            jsonMessage.put("timestamp", timestamp);
                            jsonMessage.put("flag", 0);
                            jsonArray.add(jsonMessage);*/
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


    private void sendMessage(textMessage message){
        try{
            out.writeObject(message);
            out.flush();
        } catch(IOException e){
            e.printStackTrace();
        }
        JSONObject jsonMessage = new JSONObject();
        if (message != null && message.content != null && !message.content.equals("")) {
            System.out.println("Message sent : " + message.content);
            addMessage(true, message.content);
            /*jsonMessage.put("message", message);
            jsonMessage.put("timestamp", timestamp);
            jsonMessage.put("flag", 1);
            jsonArray.add(jsonMessage);*/
        }
    }

    //Getters/Setters
    public userData getOtherUserData(){
        return otherUserData;
    }

    public String getUsername(){
        return username;
    }

    private void addMessage(boolean isMe, String message){
        Date date = new Date();
        String testTimestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date);
        JPanel messagePanel;
        JTextArea messageText = new JTextArea(message);
        messageText.setEditable(false);
        messageText.setLineWrap(true);
        Border border;
        TitledBorder titledBorder;
        if (isMe){
            messagePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            border = BorderFactory.createLineBorder(Color.blue);
            titledBorder = BorderFactory.createTitledBorder(border, "You :");
            titledBorder.setTitleJustification(TitledBorder.RIGHT);
            titledBorder.setTitleColor(Color.blue);
        }
        else {
            messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            border = BorderFactory.createLineBorder(Color.red);
            titledBorder = BorderFactory.createTitledBorder(border, otherUserData.getUsername() + " :");
            titledBorder.setTitleJustification(TitledBorder.LEFT);
            titledBorder.setTitleColor(Color.red);
        }
        messagePanel.setToolTipText(testTimestamp);
        messageText.setPreferredSize(new Dimension(350, 40));
        messageText.setMaximumSize(new Dimension(350,40));
        messageText.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageText.setAlignmentY(Component.CENTER_ALIGNMENT);
        messagePanel.setPreferredSize(new Dimension(400,80));
        messagePanel.setMaximumSize(new Dimension(400, 80));
        messagePanel.setBorder(titledBorder);
        messagePanel.add(messageText);
        messageDisplayPane.setBackground(Color.white);
        messageDisplayPane.add(messagePanel);
        messageDisplayPane.validate();
        messageDisplayPane.repaint();
        messageArea.validate();
        messageArea.repaint();
    }


    public void closeSession(){
        System.out.println("Entered the closeSession method...");
        controlConnected.disconnect();
        System.out.println("Turned the controller to false");
        this.dispose();
        /*try (FileWriter file = new FileWriter(userPath)){
            file.write(jsonObject.toJSONString());
            file.flush();
        } catch (IOException oskour) {
            System.out.println("Error while writing in the json file");
            oskour.printStackTrace();
        }*/
    }

}
