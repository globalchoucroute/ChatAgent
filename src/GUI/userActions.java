package GUI;

import Software.chatSession;
import Software.mainWindowActions;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramSocket;

public class userActions extends JPanel {

    public JButton startSession = new JButton("Begin chat session");
    public JLabel userName = new JLabel();
    public JButton changeUsername = new JButton("Change username");

    public userActions(JList L, String name, JFrame parent){
        super();
        userName.setText("Logged in as " + name);
        mainWindowActions mainWindowActions = new mainWindowActions(name);

        startSession.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (L.getSelectedIndex() != -1){
                    try {
                        //When the user starts a session, a new Window appears
                        //The second argument starts a new UDP session
                        chatWindow chatWindow = new chatWindow((String) L.getSelectedValue(), mainWindowActions.beginSession(4564));
                    } catch (Exception ex) {
                        System.out.println("aled");
                    }
                }
                else {
                    JOptionPane.showMessageDialog(parent, "Please choose a user in the list");
                }
            }
        });

        changeUsername.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    changeUsernameWindow changeUsernameWindow = new changeUsernameWindow(userName);
            }
        });
        add(startSession, BorderLayout.NORTH);
        add(userName, BorderLayout.CENTER);
        add(changeUsername, BorderLayout.SOUTH);

    }
}
