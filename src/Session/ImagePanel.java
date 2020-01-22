package Session;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;

public class ImagePanel extends JPanel implements MouseListener {

    private File file;
    private Component parent;

    public ImagePanel(File i, Component p){
        file = i;
        parent = p;

        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            System.out.println("Image illisible");
            e.printStackTrace();
        }

        //resize the image
        int h = image.getHeight(null);
        int w = image.getWidth(null);
        float r = (float) (w * 1.0 / h);
        int nH = 150;
        int nW = (int) (r * nH);
        ImageIcon scaled = new ImageIcon(image.getScaledInstance(nW, nH, Image.SCALE_FAST));

        JLabel img_pnl = new JLabel(scaled);
        this.add(img_pnl);
        this.addMouseListener(this);

        this.setMaximumSize(img_pnl.getPreferredSize());
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        JFileChooser chooser = new JFileChooser();

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File dir = chooser.getCurrentDirectory();

            InputStream is = null;
            OutputStream os = null;
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

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
