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
import java.io.IOException;

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
        for (CertificateField field : wrapper.getCertificateFields()) {
//            imageGraphics.setFont(Font.getFont(field.getFontFamily()));
            imageGraphics.setFont(new Font(field.getFontFamily(), field.isBoldText()?Font.BOLD:Font.PLAIN, field.getFontSize()));
            System.out.println(field.getFontFamily() + ", " + field.getFontSize() + "," + (field.isBoldText()?"Bold":"Plain"));
            imageGraphics.drawString(field.text, field.getX(), field.getY());
        }
        return image;
    }


}
