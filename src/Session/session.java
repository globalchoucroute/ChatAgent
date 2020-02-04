package Session;

import Software.Message.fileMessage;
import Software.Message.imageMessage;
import Software.Message.message;
import Software.Message.textMessage;
import Software.systemMessage;
import Software.systemMessageSender;
import Software.userData;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.dnd.DropTarget;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class session extends JFrame {

    //Attributes for the TCP protocol
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private message mainMessage;
    //Attributes for the UI
    public String username;
    private userData otherUserData;
    private JTextField text = new JTextField("Write a message...");
    private JPanel messageDisplayPane;
    private JScrollPane messageArea;

    //Attributes for the message fetching
    private JSONArray chatHistory;
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

    //Constructor. Will display the new window, fetch the message history and start the reception thread.
    public session(userData myself, userData otherUser, int port,  boolean isServer){
        super();

        setLocationRelativeTo(null);
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

        //*****************************************************
        // THIS IS THE PART CONCERNING THE WINDOW DISPLAY
        //*****************************************************
        messageDisplayPane = new JPanel();
        messageDisplayPane.setLayout(new BoxLayout(messageDisplayPane, BoxLayout.PAGE_AXIS));
        messageDisplayPane.setBackground(Color.white);

        setBackground(Color.white);
        new DropTarget(this, new dropListener(this));

        JButton sendButton = new JButton("Send");
        JPanel textAreaPanel = new JPanel();
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
        sendButton.setPreferredSize(new Dimension(96, 36));
        sendButton.setBorder(new EmptyBorder(2,2,2,2));
        text.setPreferredSize(new Dimension(250,40));

        textAreaPanel.add(text, BorderLayout.WEST);
        textAreaPanel.add(sendButton, BorderLayout.EAST);
        textAreaPanel.setBorder(new EmptyBorder(3,3,3,3));
        textAreaPanel.setPreferredSize(new Dimension(350,40));

        messageArea.setPreferredSize(new Dimension(400, 300));
        setPreferredSize(new Dimension(500,400));

        setLayout(new BoxLayout(getContentPane(),BoxLayout.PAGE_AXIS));
        add(messageArea);
        add(textAreaPanel);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

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
                        closeSession();
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
                            } catch (Exception ex) {
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
                        System.out.println("Failed to send the message");
                    }
                    text.setText("");
                }
            }
        });

        this.pack();
        if (isServer){setVisible(true);}

        //*****************************************************

        //*****************************************************
        // THIS IS THE PART CONCERNING THE MESSAGE FETCH
        //*****************************************************
        try {
            jsonObject = (JSONObject) new JSONParser().parse(new FileReader(userPath));
            chatHistory = (JSONArray) jsonObject.get("messages");
        } catch (FileNotFoundException e) {
            jsonObject = new JSONObject();
            chatHistory = new JSONArray();
            jsonObject.put("messages", chatHistory);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        for (Object o : chatHistory) {
            JSONObject current = (JSONObject) o;
            String message = (String) current.get("message");
            String isMe = (String) current.get("flag");
            String timestamp = (String) current.get("timestamp");
            if (isMe.equals("1")) {
                addMessage(true, new textMessage(message), timestamp);
            } else if (isMe.equals("0")) {
                addMessage(false, new textMessage(message), timestamp);
            }
        }


        //*****************************************************
        // THIS IS THE PART CONCERNING THE TCP PROTOCOL
        //*****************************************************
        try {
            Socket connectionSocket;
            if (isServer) {
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Message before accept");
                connectionSocket = serverSocket.accept();
                System.out.println("Message after accept");
            } else {
                try {Thread.sleep(100);} catch (InterruptedException ie) {ie.printStackTrace();}
                connectionSocket = new Socket(InetAddress.getByName(otherUser.getIPAddress()), port);
            }
            this.out = new ObjectOutputStream(connectionSocket.getOutputStream());
            this.in = new ObjectInputStream(connectionSocket.getInputStream());

            //Attributes for the TCP connection and the connection Thread
            Thread connectionThread = new Thread(() -> {
                try {
                    message message = null;
                    boolean temp = false;
                    while (controlConnected.getConnected()) {
                        try {
                            message = (message) in.readObject();
                        } catch (ClassNotFoundException e) {e.printStackTrace();}
                        if (message != null) {
                            if (!temp && !isServer){
                                setVisible(true);
                                temp = true;
                            }
                            Date date = new Date();
                            String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date);
                            addMessage(false, message, timestamp);
                            if (message instanceof textMessage) {
                                log(false, ((textMessage) message).content, timestamp);
                            }
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


    /**
     * Sends a text message to the other user via the TCP connection.
     * @param message is the message to be sent.
     */
    private void sendMessage(message message){
        Date date = new Date();
        String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date);
        try{
            out.writeObject(message);
            out.flush();
        } catch(IOException e){
            e.printStackTrace();
        }
        if (message != null) {
            addMessage(true, message, timestamp);
            log(true, ((textMessage) message).content, timestamp);
        }
    }

    /**
     * Sends a file (can be an image or a simple text file for example) to the other user via the tcp connection.
     * @param file is the file to be sent.
     */
    void sendFile(File file){
        Date date = new Date();
        String timestamp = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date);
        String[] supportedImageFormats = {"png", "jpg", "gif", "jpeg","PNG","JPG","JPEG","GIF"};
        String extension = getExtension(file.getName());
        boolean isImage = Arrays.asList(supportedImageFormats).contains(extension);
        if (isImage) {
            mainMessage = new imageMessage(file);
            try {
                out.writeObject(mainMessage);
            } catch (IOException e){
                e.printStackTrace();
            }
            addMessage(true, mainMessage, timestamp);
        }
        else {
            mainMessage = new fileMessage(file);
            try {
                out.writeObject(mainMessage);
            } catch (IOException e){
                e.printStackTrace();
            }
            addMessage(true, mainMessage, timestamp);
        }
    }

    //Getters/Setters

    /**
     * Finds the extension of a file, in order to determine if it is an image or not.
     * @param filename is the name of the file (extension included).
     * @return the extension in a String format.
     */
    private String getExtension(String filename) {
        String extension = "";

        int i = filename.lastIndexOf('.');
        if (i > 0) {
            extension = filename.substring(i+1);
        }

        return extension;
    }

    public userData getOtherUserData(){
        return otherUserData;
    }

    public String getUsername(){
        return username;
    }

    public void updateOtherUserdata(userData newOtherUserdata){
        otherUserData = newOtherUserdata;
    }

    /**
     * Adds a message to the chat window. The message can be either a text or a file (image or other).
     * @param isMe determines who sent the message.
     * @param message is the message to be displayed.
     * @param timestamp is the time at which the message has been sent.
     */
    private void addMessage(boolean isMe, message message, String timestamp){
        Border border;
        TitledBorder titledBorder;
        JPanel wrappingPanel = new JPanel();
        wrappingPanel.setBackground(Color.white);
        wrappingPanel.setLayout(new BoxLayout(wrappingPanel, BoxLayout.LINE_AXIS));

        if (isMe) {
            border = BorderFactory.createLineBorder(Color.blue,2,true);
            titledBorder = BorderFactory.createTitledBorder(border, "You :");
            titledBorder.setTitleJustification(TitledBorder.RIGHT);
            titledBorder.setTitleColor(Color.blue);
        } else {
            border = BorderFactory.createLineBorder(Color.red,2,true);
            titledBorder = BorderFactory.createTitledBorder(border, otherUserData.getUsername() + " :");
            titledBorder.setTitleJustification(TitledBorder.LEFT);
            titledBorder.setTitleColor(Color.red);
        }

        if (message instanceof textMessage) {
            String content = ((textMessage) message).content;
            TextPanel textPanel = new TextPanel(content);
            textPanel.setToolTipText(timestamp);
            wrappingPanel.setPreferredSize(new Dimension(400, textPanel.getHeightRatio() * 15 + 25));
            wrappingPanel.setMaximumSize(new Dimension(400, (int) wrappingPanel.getPreferredSize().getHeight()));
            wrappingPanel.setBorder(titledBorder);
            wrappingPanel.add(textPanel);
        }
        else if (message instanceof fileMessage) {
            FilePanel filePanel = new FilePanel(((fileMessage) message).file, this);
            filePanel.setToolTipText(timestamp);
            wrappingPanel.setPreferredSize(new Dimension(400, filePanel.getHeight() + 30));
            wrappingPanel.setMaximumSize(new Dimension(400, (int) wrappingPanel.getPreferredSize().getHeight()));
            wrappingPanel.setBorder(titledBorder);
            wrappingPanel.add(filePanel);
        }
        else if (message instanceof imageMessage) {
            ImagePanel imagePanel = new ImagePanel(((imageMessage) message).image, this, isMe);
            imagePanel.setToolTipText(timestamp);
            wrappingPanel.setPreferredSize(new Dimension(400, (int)imagePanel.getPreferredSize().getHeight() + 30));
            wrappingPanel.setMaximumSize(new Dimension(400, (int) wrappingPanel.getPreferredSize().getHeight()));
            wrappingPanel.setBorder(titledBorder);
            wrappingPanel.add(imagePanel);
        }

        wrappingPanel.setToolTipText(timestamp);
        messageDisplayPane.add(wrappingPanel);
        messageDisplayPane.validate();
        messageDisplayPane.repaint();
        messageArea.validate();
        messageArea.repaint();

    }

    /**
     * Creates and add an entry to our chat history. Only activated when sending or receiving text messages.
     * @param isMe determines which user sent the message.
     * @param content is the content of the text message.
     * @param timestamp is the time at which the message has been sent.
     */
    private void log(boolean isMe, String content, String timestamp){
        JSONObject entry = new JSONObject();
        entry.put("message", content);
        if (isMe) {
            entry.put("flag", "1");
        } else {
            entry.put("flag", "0");
        }
        entry.put("timestamp", timestamp);
        chatHistory.add(entry);
        saveLog();
    }

    /**
     * Writes down the logs in the JSON file. This function is called every time we log a message, to make sure everything we be logged properly.
     */
    private void saveLog(){
        PrintWriter p;
        try {
            p = new PrintWriter(userPath);
            p.write(jsonObject.toJSONString());
            p.flush();
            p.close();
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    /**
     * Close the session by turning down the controller in the message reception thread.
     */
    public void closeSession(){
        System.out.println("Entered the closeSession method...");
        controlConnected.disconnect();
        System.out.println("Turned the controller to false");
        this.dispose();
    }

}
