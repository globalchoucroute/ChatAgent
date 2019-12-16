package GUI;

import Software.chatSession;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.InetAddress;

public class chatWindow extends JFrame {

    //Attributes
    public String username;
    public JTextArea messageDisplay;
    public JScrollPane messageArea;
    public JPanel textAreaPanel = new JPanel();
    JTextField text = new JTextField("Write a message...");
    public JButton sendButton = new JButton("Send");
    chatSession session;

    //Constructor
    public chatWindow (String username, String title, chatSession s){
        super();
        this.username = username;
        this.session = s;

        sendButton.setMnemonic(KeyEvent.VK_ENTER);
        text.setPreferredSize(new Dimension(250,40));
        sendButton.setPreferredSize(new Dimension(100, 40));

        textAreaPanel.add(text, BorderLayout.WEST);
        textAreaPanel.add(sendButton, BorderLayout.EAST);
        textAreaPanel.setPreferredSize(new Dimension(350,40));

        messageDisplay = new JTextArea("This is the start of your conversation with " + title + ".\n");
        messageDisplay.setEditable(false);
        messageArea = new JScrollPane(messageDisplay);
        messageArea.setPreferredSize(new Dimension(400, 300));
        setPreferredSize(new Dimension(600,500));


        this.setTitle(title);

        add(messageArea, BorderLayout.NORTH);
        add(textAreaPanel, BorderLayout.SOUTH);
        text.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    if (text.getText() != null){
                        messageDisplay.append("\n" + username + " : " + text.getText());
                        text.setText("");

                        try {
                            session.sendMessage(text.getText());
                        } catch (Exception ex) {
                            System.out.println("Failed to send the message");
                        }
                    }
                }
            }
        });
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (text.getText() != null){
                    messageDisplay.append("\n" + username + " : " + text.getText());
                    text.setText("");
                    try {
                        session.sendMessage(text.getText());
                    } catch (Exception ex) {
                        System.out.println("Failed to send the message");
                    }
                }
            }
        });


        pack();
        setVisible(true);
    }

    //Methods
    public void changeDisplayedMessages(String msg){
        String current = this.messageDisplay.getText();
        this.messageDisplay.setText(current + "\n" + this.username + " : " + msg);
    }
}
