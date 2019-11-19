package GUI;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class chatWindow extends JFrame {

    textArea textArea = new textArea();
    public chatWindow (String title){
        super();
        this.setPreferredSize(new Dimension(400,600));
        this.setTitle(title);
        add(textArea, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }
}
