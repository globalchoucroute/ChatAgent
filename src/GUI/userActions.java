package GUI;

import Software.mainWindowActions;
import Software.sessionTable;
import Software.userData;
import Software.userList;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class userActions extends JPanel {

    private JLabel userName = new JLabel();
    public String username;
    private static int port = 5000;
    mainWindowActions mainWindowActions;

    userActions(JList L, userData myself, mainWindow parent, userList userList, sessionTable sessionTable){
        super();
        username = myself.getUsername();
        JPanel titlePanel = new JPanel();
        JPanel restPanel = new JPanel();
        JPanel labelPanel = new JPanel();
        Border border = BorderFactory.createLineBorder(Color.black);
        JLabel title = new JLabel("<html><h1 style = 'font-size:140%;font-family:Calibri;'> Welcome to ChatAgent </h1></html>");
        titlePanel.add(title);
        titlePanel.setPreferredSize(new Dimension(300, 50));
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
                    //When the user starts a session, a new Window appears
                    //The second argument starts a new UDP session
                    userData otherUserData = userList.getUserByName((String) L.getSelectedValue());
                    mainWindowActions.beginChatSession(port, otherUserData, sessionTable);
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
        //changeUsernamePane.setBackground(Color.white);
        //startSessionPane.setBackground(Color.white);
        JButton changeUsername = new JButton("Change username");
        changeUsernamePane.add(changeUsername);
        changeUsername.addActionListener(e -> new changeUsernameWindow(parent));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        restPanel.add(startSessionPane, BorderLayout.SOUTH);
        restPanel.add(changeUsernamePane, BorderLayout.NORTH);
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
