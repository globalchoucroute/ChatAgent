package Session;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class FilePanel extends JPanel implements MouseListener {
    private static final long serialVersionUID = 3339L;
    private File file;
    private Component parent;

    public FilePanel (File f, Component p){
        super (new BorderLayout());
        file = f;
        parent = p;
        String name = f.getName();
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(new File("file_icon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JLabel jl = new JLabel(new ImageIcon(bi));
        jl.setBorder(new EmptyBorder(0, 0, 0, 10));

        JPanel grid = new JPanel(new GridLayout(2, 1));
        JLabel size_lbl = new JLabel(convertSize(f.length()));
        size_lbl.setForeground(Color.BLUE);
        grid.add(new JLabel(name));
        grid.add(size_lbl);

        add(jl, BorderLayout.WEST);
        add(grid, BorderLayout.EAST);

        addMouseListener(this);

        setAlignmentX(Component.LEFT_ALIGNMENT);
        setMaximumSize(getPreferredSize());
    }

    // converts bytes in a more readable unit
    private String convertSize(long size) {
        String unit = "B";
        double s = size;

        if (s / 1000 > 1) {
            s = s / 1000;
            unit = "kB";
            if (s / 1000 > 1) {
                s = s / 1000;
                unit = "GB";
            }
        }

        // round to two places
        BigDecimal bd = BigDecimal.valueOf(s);
        bd = bd.setScale(1, RoundingMode.HALF_UP);

        return bd.doubleValue() + unit;
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
