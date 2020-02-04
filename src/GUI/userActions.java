package GUI;

import Software.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;

public class userActions extends JPanel {

    private JLabel userName = new JLabel();
    public String username;
    private static int port = 5000;
    mainWindowActions mainWindowActions;

    userActions(JList L, userData myself, mainWindow parent, userList userList, sessionTable sessionTable){
        super();
        String[] status = {"Available", "Away", "Busy"};
        JComboBox statusList = new JComboBox(status);
        statusList.setSelectedIndex(0);

        username = myself.getUsername();
        JPanel statusPanel = new JPanel();
        JPanel titlePanel = new JPanel();
        JPanel restPanel = new JPanel();
        JPanel labelPanel = new JPanel();

        JLabel statusString = new JLabel("Status :");
        JLabel title = new JLabel("<html><h1 style = 'font-size:140%;font-family:Calibri;'> Welcome to ChatAgent </h1></html>");
        titlePanel.add(title);
        titlePanel.setPreferredSize(new Dimension(300, 50));
        statusPanel.add(statusString, BorderLayout.WEST);
        statusPanel.add(statusList, BorderLayout.EAST);
        Border border = BorderFactory.createLineBorder(Color.black);
        titlePanel.setBorder(border);
        restPanel.setBorder(border);
        labelPanel.setBorder(border);

        labelPanel.setBackground(Color.white);
        userName.setText("Currently logged in as " + username);
        labelPanel.add(userName);
        mainWindowActions = new mainWindowActions(myself, userList, sessionTable);

        //Starts a session with the selected user when the button is clicked
        JPanel startSessionPane = new JPanel();
        JButton startSession = new JButton("Begin chat session");
        startSessionPane.add(startSession);
        startSession.addActionListener(e -> {
            if (L.getSelectedIndex() != -1){
                try {
                    //Start a new session if a non busy user is selected in the list
                    userData otherUserData = userList.getUserByName((String) L.getSelectedValue());
                    if (otherUserData.getStatus().equals("Busy")){
                        JOptionPane.showMessageDialog(parent, "Sorry, the user you are trying to reach is Busy.");
                    }
                    else {
                        mainWindowActions.beginChatSession(port, otherUserData, sessionTable);
                        port++;
                    }
                } catch (Exception ex) {
                    System.out.println("Failed to begin the session (userActions)");
                }
            }
            else {
                JOptionPane.showMessageDialog(parent, "Please choose a user in the list");
            }
        });

        ActionListener statusListener = e -> {
            String s = (String) statusList.getSelectedItem();
            systemMessageSender systemMessageSender = new systemMessageSender();
            systemMessage systemMessage = new systemMessage(s, myself, 0);
            try {
                systemMessageSender.sendSystemMessage(systemMessage, InetAddress.getByName("255.255.255.255"), true, 3000);
                mainWindowActions.modifyStatus(s);
            } catch (IOException er) {
                er.printStackTrace();
            }
        };

        statusList.addActionListener(statusListener);
        //Open the change username window when the button is clicked
        JPanel changeUsernamePane = new JPanel();
        //changeUsernamePane.setBackground(Color.white);
        //startSessionPane.setBackground(Color.white);
        JButton changeUsername = new JButton("Change username");
        changeUsernamePane.add(changeUsername);
        changeUsername.addActionListener(e -> new changeUsernameWindow(parent));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        restPanel.add(startSessionPane, BorderLayout.CENTER);
        restPanel.add(changeUsernamePane, BorderLayout.NORTH);
        restPanel.add(statusPanel, BorderLayout.SOUTH);
        restPanel.setPreferredSize(new Dimension(300, 130));
        add(titlePanel);
        add(labelPanel);
        add(restPanel);
        setBackground(Color.white);
        setPreferredSize(new Dimension(300,200));
    }

    void changeUsername(String name){
        username = name;
        userName.setText("Logged in as " + name);
    }
}
