package GUI;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class chatWindow extends JFrame {

    public JLabel messageDisplay;
    public JPanel displayPanel = new JPanel();
    public textArea textArea = new textArea(this);

    public chatWindow (String title){
        super();

        this.setPreferredSize(new Dimension(400,600));
        this.messageDisplay = new JLabel("This is the start of your conversation with " + title + ".\n");
        this.setTitle(title);
        displayPanel.add(messageDisplay);
        displayPanel.setPreferredSize(new Dimension(400, 300));
        add(displayPanel, BorderLayout.NORTH);
        add(textArea, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    public void changeDisplayedMessages(String msg){
        String current = this.messageDisplay.getText();
        this.messageDisplay.setText("\n" + current + "\n" + msg);
    }
}
