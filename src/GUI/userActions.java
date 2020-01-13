package GUI;

import Software.mainWindowActions;
import Software.sessionTable;
import Software.userData;
import Software.userList;

import javax.swing.*;
import java.awt.BorderLayout;
import java.net.DatagramSocket;
import Session.session;
public class userActions extends JPanel {

    private JLabel userName = new JLabel();
    public String username;
    private static int port = 5000;
    public mainWindowActions mainWindowActions;

    userActions(JList L, userData myself, mainWindow parent, userList userList, sessionTable sessionTable){
        super();
        username = myself.getUsername();
        userName.setText("Logged in as " + username);
        mainWindowActions = new mainWindowActions(myself, userList, sessionTable);

        //Starts a session with the selected user when the button is clicked
        JPanel startSessionPane = new JPanel();
        JButton startSession = new JButton("Begin chat session");
        startSessionPane.add(startSession);
        startSession.addActionListener(e -> {
            if (L.getSelectedIndex() != -1){
                try {
                    //When the user starts a session, a new Window appears
                    //The second argument starts a new UDP session
                    DatagramSocket beginSessionNotifySocket = new DatagramSocket(4999);
                    userData otherUserData = userList.getUserByName((String) L.getSelectedValue());
                    mainWindowActions.beginChatSession(port, otherUserData, beginSessionNotifySocket, sessionTable);
                    //new chatWindow(username, (String) L.getSelectedValue(), mainWindowActions.beginSession(username, port, otherUserData, beginSessionNotifySocket));
                    beginSessionNotifySocket.close();
                    port++;
                } catch (Exception ex) {
                    System.out.println("Failed to begin the session (userActions)");
                }
            }
            else {
                JOptionPane.showMessageDialog(parent, "Please choose a user in the list");
            }
        });

        //Open the change username window when the button is clicked
        JPanel changeUsernamePane = new JPanel();
        JButton changeUsername = new JButton("Change username");
        changeUsernamePane.add(changeUsername);
        changeUsername.addActionListener(e -> new changeUsernameWindow(parent));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(startSessionPane);
        add(userName);
        add(changeUsernamePane);

    }

    public void changeUsername(String name){
        username = name;
        userName.setText("Logged in as " + name);
    }
}
