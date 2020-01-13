package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import Software.connection;
import Software.sessionTable;
import Software.userList;

public class connectionWindow extends JFrame {

    //Attributes
    public JButton connectButton = new JButton("Connect");
    public JTextField userNameField = new JTextField("");
    public JFrame parent;

    //Constructor
    public connectionWindow (){
        super();

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setTitle("Connection - ChatAgent");
        JPanel chatAgentPanel = new JPanel();
        add(chatAgentPanel);
        ImageIcon icon = new ImageIcon("images/icon.png");
        setIconImage(icon.getImage());
        JLabel title = new JLabel("<html><h1 style = 'font-size:130%;font-family:Calibri;'> Welcome to ChatAgent 2020 </h1></html>");
        chatAgentPanel.add(title);
        chatAgentPanel.setAlignmentY(Component.CENTER_ALIGNMENT);
        chatAgentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        chatAgentPanel.setPreferredSize(new Dimension(330,30));
        //setPreferredSize(new Dimension(330, 80));
        setPreferredSize(new Dimension(330,160));
        setResizable(false);
        parent = this;

        Border border = BorderFactory.createLineBorder(Color.black);
        JPanel username = new JPanel();
        username.setAlignmentY(Component.CENTER_ALIGNMENT);
        username.setBorder(border);
        username.setPreferredSize(new Dimension(330, 20));
        JLabel enter = new JLabel("<html><p style = 'font-size:100%;font-family:Calibri;'>Please enter your username : </p></html>");
        username.add(enter, BorderLayout.WEST);
        username.add(userNameField, BorderLayout.EAST);
        userNameField.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        enter.setAlignmentY(Component.CENTER_ALIGNMENT);
        userNameField.setAlignmentY(Component.CENTER_ALIGNMENT);
        userNameField.setToolTipText("Enter your username here");
        userNameField.setPreferredSize(new Dimension(150,20));
        add(username);

        JPanel buttonPane = new JPanel();
        buttonPane.setBorder(new EmptyBorder(2,2,2,2));
        buttonPane.add(connectButton);
        buttonPane.setPreferredSize(new Dimension(330,20));
        connectButton.setAlignmentY(Component.CENTER_ALIGNMENT);
        connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        connectButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(buttonPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        connection connection = new connection();

        userNameField.addKeyListener(new KeyAdapter() {
             public void keyPressed(KeyEvent e) {
                 String username = userNameField.getText();
                 int key = e.getKeyCode();
                 if (key == KeyEvent.VK_ENTER) {
                     if (username != null && !username.equals("")){
                         if(connection.checkUsername(username)) {
                             userList userList = connection.sendHello(username);
                             new mainWindow(connection.getPersonalUserData(), userList, new sessionTable());
                             setVisible(false);
                             dispose();
                         }
                         else {
                             JOptionPane.showMessageDialog(parent, "This username is already taken !");
                             connection.setControlTrue();
                         }
                     }
                 }
             }
         });

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userNameField.getText();
                if (username != null && !username.equals("")){
                    if(connection.checkUsername(username)){
                        userList userList = connection.sendHello(username);
                        new mainWindow(connection.getPersonalUserData(), userList, new sessionTable());
                        setVisible(false);
                        dispose();
                    }
                    else {
                        JOptionPane.showMessageDialog(parent, "This username is already taken !");
                        connection.setControlTrue();
                    }
                }
            }
        });

        pack();
        setVisible(true);

    }
}
