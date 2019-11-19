package GUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class userActions extends JPanel {

    JButton startSession = new JButton("Begin chat session");
    JLabel userName = new JLabel();
    JButton changeUsername = new JButton("Change username");

    public userActions(JList L, String name, JFrame parent){
        super();
        userName.setText("Logged in as " + name);

        startSession.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (L.getSelectedIndex() != -1){
                    chatWindow chatWindow = new chatWindow((String) L.getSelectedValue());
                }
                else {
                    JOptionPane.showMessageDialog(parent, "Please chose a username in the list");
                }
            }
        });
        add(startSession, BorderLayout.NORTH);
        add(userName, BorderLayout.CENTER);
        add(changeUsername, BorderLayout.SOUTH);

    }
}
