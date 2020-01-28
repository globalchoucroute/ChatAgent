package Session;

import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Font;

class TextPanel extends JTextArea {
    private String message;
    TextPanel (String content){
        super();
        message = content;
        setFont(new Font("Courier", Font.PLAIN, 12));
        setBackground(Color.white);
        setEditable(false);
        setLineWrap(true);
        setWrapStyleWord(true);
        setText(content);
        //setMinimumSize(new Dimension(380,40));
    }

    int getHeightRatio(){
        int ratio = 0;
        int length = message.length();
        while (length > 0){
            ratio++;
            length = length - 59;
        }
        return ratio;
    }

}
