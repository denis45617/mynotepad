package hr.fer.oprpp1.hw08.jnotepadpp.icons;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class for getting Icons from resources
 */
public class Icons {

    /**
     * Getter for blue floppy disk icon
     * @return ImageIcon - blue floppy disk
     */
    public  static ImageIcon getBlueIcon() {
        try {
            InputStream is = Icons.class.getResourceAsStream("/hr/fer/oprpp1/hw08/jnotepadpp/icons/blueDisk.png");
            return getImageIcon(is);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Getter for red floppy disk icon
     * @return ImageIcon - red floppy disk
     */
    public  static ImageIcon getRedIcon() {
        try {
            InputStream is = Icons.class.getResourceAsStream("/hr/fer/oprpp1/hw08/jnotepadpp/icons/redDisk.png");
            return getImageIcon(is);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Method for getting image icon from inputstream and scaling it down to to 10x10px
     * @param is input stream
     * @return  ImageIcon scaled to 10px to 1'px
     * @throws IOException when I/O error occurs
     */
    public static ImageIcon getImageIcon(InputStream is) throws IOException {
        if (is == null)
            return null;
        byte[] bytes = is.readAllBytes();
        is.close();
        ImageIcon imageIcon = new ImageIcon(bytes);
        Image image = imageIcon.getImage(); // transform it
        Image newimg = image.getScaledInstance(10, 10,  Image.SCALE_SMOOTH);

        return new ImageIcon(newimg);
    }
}
