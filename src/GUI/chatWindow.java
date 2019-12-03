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
    public JLabel messageDisplay;
    public JScrollPane messageArea;
    public JPanel displayPanel = new JPanel();
    public JPanel textAreaPanel = new JPanel();
    JTextArea text = new JTextArea("Write a message...");
    public JButton sendButton = new JButton("Send");
    chatSession session;

    //Constructor
    public chatWindow (String title, chatSession s){
        super();

        this.session = s;

        sendButton.setMnemonic(KeyEvent.VK_ENTER);
        text.setPreferredSize(new Dimension(250,40));
        sendButton.setPreferredSize(new Dimension(100, 40));

        textAreaPanel.add(text, BorderLayout.WEST);
        textAreaPanel.add(sendButton, BorderLayout.EAST);
        textAreaPanel.setPreferredSize(new Dimension(350,40));

        this.messageArea = new JScrollPane(displayPanel);
        messageArea.setSize(new Dimension(400, 300));
        this.setPreferredSize(new Dimension(600,500));
        this.messageDisplay = new JLabel("This is the start of your conversation with \n" + title + ".\n");
        this.setTitle(title);

        displayPanel.add(messageDisplay);
        displayPanel.setSize(new Dimension(400, 300));

        add(messageArea, BorderLayout.NORTH);
        add(textAreaPanel, BorderLayout.SOUTH);
        text.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    if (text.getText() != null){
                        text.setText("");
                        //changeDisplayedMessages(text.getText());
                        try {
                            InetAddress localaddress = InetAddress.getLocalHost();
                            session.sendMessage(session.buildPDU(text.getText(), localaddress, 2345));
                        } catch (Exception ex) {
                            System.out.println("raté");
                        }
                    }
                }
            }
        });
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (text.getText() != null){
                    text.setText("");
                    //changeDisplayedMessages(text.getText());
                    try {
                        InetAddress localaddress = InetAddress.getLocalHost();
                        session.sendMessage(session.buildPDU(text.getText(), localaddress, 2345));
                    } catch (Exception ex) {
                        System.out.println("raté sendmessage");
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
        this.messageDisplay.setText("\n" + current + "\n" + msg);
    }
}
