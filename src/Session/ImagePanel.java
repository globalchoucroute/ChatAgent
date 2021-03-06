package Session;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * This class will be needed when a user sends or receives an image. It's a personalized JPanel.
 */
public class ImagePanel extends JPanel implements MouseListener {

    private File file;
    private Component parent;

    /**
     * Standard constructor for the FilePanel. Will build a JPanel containing the clickeable file.
     * @param i is the image we'll display in the panel.
     * @param p is the parent container in which we'll display it.
     * @param isMe determines on which side the image should be displayed (left or right, depending on the user sending it).
     */
    ImagePanel(File i, Component p, boolean isMe){
        file = i;
        parent = p;
        setBackground(Color.white);
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            System.out.println("Can't read the image file");
            e.printStackTrace();
        }

        //resize the image
        assert image != null;
        int h = image.getHeight(null);
        int w = image.getWidth(null);
        float r = (float) (w * 1.0 / h);
        int nH = 200;
        int nW = (int) (r * nH);
        ImageIcon scaled = new ImageIcon(image.getScaledInstance(nW, nH, Image.SCALE_FAST));

        JLabel img_pnl = new JLabel(scaled);
        add(img_pnl);
        addMouseListener(this);

        setMaximumSize(img_pnl.getPreferredSize());
        if (isMe){
            setAlignmentX(Component.RIGHT_ALIGNMENT);
        } else {
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }
    }

    /**
     * Makes the user chose a save file when he clicks on the image.
     * @param e is the event expected.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        JFileChooser chooser = new JFileChooser();

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File dir = chooser.getCurrentDirectory();

            InputStream is;
            OutputStream os;
            try {
                is = new FileInputStream(file);
                os = new FileOutputStream(new File(dir + "/" + file.getName()));
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }

                is.close();
                os.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
