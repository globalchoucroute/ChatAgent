package GUI;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

public class textArea extends JPanel {

    //Attributes
    JTextArea text = new JTextArea("Write a message...");
    JButton sendButton = new JButton("Send");

    //Constructor
    public textArea(){
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        sendButton.setMnemonic(KeyEvent.VK_ENTER);

        text.setPreferredSize(new Dimension(250,40));
        sendButton.setPreferredSize(new Dimension(100, 40));
        this.add(text);
        this.add(sendButton);


    }
}
