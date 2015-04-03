package org.bluecipherz.certificatemaker;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javax.imageio.ImageWriter;

/**
 * @author bazi
 * Class cannot be instantiated
 * Used mainly to draw the output image. has several other useful image manipulation functions though,
 * and much more to be added.
 * Created by bazi on 19/3/15.
 */
public class ImageUtils {

    // class cannot be instantiated
    private ImageUtils() { }

    /**
     * Creates a deep copy of the specified BufferedImage.
     * @param bufferedImage
     * @return
     */
    public static BufferedImage deepCopy(BufferedImage bufferedImage) {
        ColorModel colorModel = bufferedImage.getColorModel();
        boolean isAlphaPreMultiplied = colorModel.isAlphaPremultiplied();
        WritableRaster writableRaster = bufferedImage.copyData(null);
        return new BufferedImage(colorModel, writableRaster, isAlphaPreMultiplied, null);
    }

    /**
     * Hardcoded stupid image watermark method
     * @param text
     * @param sourceImageFile
     * @param destImageFile
     */
    public static void addTextWatermark(String text, File sourceImageFile, File destImageFile) {
        try {
            BufferedImage sourceImage = ImageIO.read(sourceImageFile);
            Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();
            // initializes necessary graphics properties
            AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f);
            g2d.setComposite(alphaChannel);
            g2d.setColor(Color.BLUE);
            g2d.setFont(new Font("Helvetica", Font.BOLD, 64));
            FontMetrics fontMetrics = g2d.getFontMetrics();
            Rectangle2D rect = fontMetrics.getStringBounds(text, g2d);
            // calculates the coordinate where the String is painted
            int centerX = (sourceImage.getWidth() - (int)rect.getWidth()) / 2;
            int centerY = sourceImage.getHeight() / 2;
            // paints the textual watermark
            g2d.drawString(text, centerX, centerY);
            ImageIO.write(sourceImage, "png", destImageFile);
            g2d.dispose();
            System.out.println("The tex watermark is added to the image");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates and returns a new BufferedImage object using the specified image path. Image path must be an
     * absolute path.
     * @param path The absolute path to the image file
     * @return Returns the image object
     */
    public static BufferedImage openImage(String path) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(path));
            System.out.println("Opening image : " + path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    /**
     * Saves a BufferedImage to the given file, pathname must not have any periods "." in it except for the one
     * before the format, i.e. C:/Images/fooimage.png
     * @param img The image object
     * @param ref The save path
     */
    public static void saveImage(BufferedImage img, String ref) {
        try {
            String format = (ref.endsWith(".png")) ? "png" : "jpg";
            ImageIO.write(img, format, new File(ref));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean saveImageCopy(String imageFile) {
        BufferedImage img;
        try {
            String saveAs = "copy.png";
            img = ImageIO.read(new File(imageFile));
            File saveImage = new File("C:\\Users", saveAs);
            ImageIO.write(img, "png", saveImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void createCertificateImage(CertificateWrapper wrapper, Image certificateImage, File file) {
        BufferedImage image = SwingFXUtils.fromFXImage(certificateImage, null);
        image = writeFieldsToImage(image, wrapper);
        saveImage(image, file.getAbsolutePath());
    }

    private static BufferedImage writeFieldsToImage(BufferedImage image, CertificateWrapper wrapper) {
        Graphics imageGraphics = image.getGraphics();
        imageGraphics.setColor(Color.BLACK);
//        for (CertificateField field : wrapper.getCertificateFields()) {
////            imageGraphics.setFont(Font.getFont(field.getFontFamily()));
//            imageGraphics.setFont(new Font(field.getFontFamily(), field.getFontStyle() == Font.BOLD ? Font.BOLD : Font.PLAIN, field.getFontSize()));
////            System.out.println(field.getFontFamily() + ", " + field.getFontSize() + "," + (field.isBoldText()?"Bold":"Plain"));
////            imageGraphics.drawString(field.text, field.getX(), field.getY());
//        }
        return image;
    }
    
    // new Method, use this
    public static BufferedImage createBufferedImage(Image image, HashMap<CertificateField, String> fields) throws FileNotFoundException, IOException {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        
        Graphics2D imageGraphics = (Graphics2D) bufferedImage.getGraphics();
        // TODO antialiasing, dithering
        
        imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // there are three antialiasing options that i think can be used here but dont know which to use.
        imageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); // default
//        imageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB); // good on lcd
//        imageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP); // if the font has antialiasing details in it
        imageGraphics.setColor(Color.BLACK);
        
        int index=0;
        for (Map.Entry<CertificateField, String> field : fields.entrySet()) {
            if(field.getKey().getFieldType() == FieldType.IMAGE) {
                // draw avatar image
//                imageGraphics.drawImage(new BufferedImage(), index, index, null);
                imageGraphics.drawImage(ImageIO.read(new File(field.getValue())), null, field.getKey().getX(), field.getKey().getY());
            } else {
                imageGraphics.setFont(new Font(field.getKey().getFontFamily(), field.getKey().getFontStyle(), field.getKey().getFontSize()));
                imageGraphics.drawString(field.getValue(), field.getKey().getX(), field.getKey().getY());
                System.out.println("drawString(" + field.getValue() + ")"); // debug
            }
            index++;
        }       
        
        return bufferedImage;
    }


}
