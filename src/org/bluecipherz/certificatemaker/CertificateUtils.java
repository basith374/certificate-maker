package org.bluecipherz.certificatemaker;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bazi
 */
public class CertificateUtils {
    
    /**
     * used by outsiders
     * converts and CertificateField into a Text object
     * @param certificateField
     * @return 
     */
    public CertificateText createText(final CertificateField certificateField) {
        CertificateText text = new CertificateText(certificateField);
//        text.setFont(Font.font(certificateField.getFontFamily(), certificateField.getFontSize())); // TODO custom font functionality
        EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {
            public double initialEventX;
            public double initialEventY;
            public double initialComponentX;
            public double initialComponentY;
            @Override
            public void handle(MouseEvent event) {
                EventType<? extends Event> eventType = event.getEventType();
                CertificateText text = (CertificateText) event.getSource();
                if (eventType.equals(MouseEvent.MOUSE_PRESSED)) {
//                    if(MOUSEMODE == MouseMode.ADD || MOUSEMODE == MouseMode.ADD_IMAGE || MOUSEMODE == MouseMode.MOVE) {
                    if(Window.getMouseMode() == MouseMode.MOVE) {
                        initialComponentX = text.getX();
                        initialComponentY = text.getY();
                        initialEventX = event.getX();
                        initialEventY = event.getY();
                        System.out.println("Mode : move"); // debug
                    } else if (Window.getMouseMode() == MouseMode.DELETE) {
                        Group parent = (Group) text.getParent();
                        parent.getChildren().remove(text);
                        // tab field flags
                        TabPane tabPane = (TabPane) ((ScrollPane)parent.getParent()).getParent();
                        CertificateTab tab = (CertificateTab) tabPane.getSelectionModel().getSelectedItem();
                        if(text.getCertificateField().getFieldType() == FieldType.COURSE)
                            tab.setCourseFieldAdded(false);
                        else if(text.getCertificateField().getFieldType() == FieldType.DATE)
                            tab.setDateFieldAdded(false);
                        else if(text.getCertificateField().getFieldType() == FieldType.REGNO)
                            tab.setRegnoFieldAdded(false);
                        System.out.println("Mode : delete"); // debug
                    } else if (Window.getMouseMode() == MouseMode.EDIT) {
                        Window.showEditFieldDialog(text);
                        System.out.println("Mode : edit"); // debug
//                        if(text.getCertificateField().getFieldType() == FieldType.COURSE) { // new debug
//                            System.out.println("Courses :");
//                            for(String s :text.getCertificateField().getCourses()) {
//                                System.out.println(s);
//                            }
//                        }
                    }
                } else if (eventType.equals(MouseEvent.MOUSE_DRAGGED)) {
                    if(Window.getMouseMode() == MouseMode.MOVE) {
                        double currentX = event.getX();
                        double currentY = event.getY();
                        double x = currentX - initialEventX + initialComponentX;
                        double y = currentY - initialEventY + initialComponentY;
                        text.setX(x);
                        text.setY(y);
                    }
                } else if (eventType.equals(MouseEvent.MOUSE_RELEASED)) {
                    if(Window.getMouseMode() == MouseMode.MOVE) {
                        double currentX = event.getX();
                        double currentY = event.getY();
                        double x = currentX - initialEventX + initialComponentX;
                        double y = currentY - initialEventY + initialComponentY;
                        text.setX(x);
                        text.setY(y);
                    }
                }
            }
        };
        text.setOnMousePressed(mouseHandler);
        text.setOnMouseDragged(mouseHandler);
        text.setOnMouseReleased(mouseHandler);
        return text;
    }

    public ImageView createAvatarImage(int x, int y, int width, int height) {
        Image image = new Image(getClass().getResourceAsStream("icons/avatarx320.png"), width, height, true, true);
        System.out.println("image dimensions : " + width + "x" + height);
        ImageView imageView = new ImageView(image);
        imageView.setX(x);
        imageView.setY(y);
        return createAvatarImage(x, y, imageView);
    }
    
    public ImageView createAvatarImage(int x, int y, ImageView imageView) {
        EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {
            public double initialEventX;
            public double initialEventY;
            public double initialComponentX;
            public double initialComponentY;
            @Override
            public void handle(MouseEvent event) {
                EventType<? extends Event> eventType = event.getEventType();
                ImageView imageView = (ImageView) event.getSource();
                if (eventType.equals(MouseEvent.MOUSE_PRESSED)) {
//                    if(MOUSEMODE == MouseMode.ADD || MOUSEMODE == MouseMode.ADD_IMAGE || MOUSEMODE == MouseMode.MOVE) {
                    if(Window.getMouseMode() == MouseMode.MOVE) {
                        initialComponentX = imageView.getX();
                        initialComponentY = imageView.getY();
                        initialEventX = event.getX();
                        initialEventY = event.getY();
                        System.out.println("Mode : move"); // debug
                    } else if (Window.getMouseMode() == MouseMode.DELETE) {
                        Group parent = (Group) imageView.getParent();
                        parent.getChildren().remove(imageView);
                        TabPane tabPane = (TabPane) ((ScrollPane)parent.getParent()).getParent();
                        ((CertificateTab)tabPane.getSelectionModel().getSelectedItem()).setAvatarFieldAdded(false);
                        System.out.println("Mode : delete"); // debug
                    }
                } else if (eventType.equals(MouseEvent.MOUSE_DRAGGED)) {
                    if(Window.getMouseMode() == MouseMode.MOVE) {
                    double currentX = event.getX();
                        double currentY = event.getY();
                        double x = currentX - initialEventX + initialComponentX;
                        double y = currentY - initialEventY + initialComponentY;
                        imageView.setX(x);
                        imageView.setY(y);
                    }
                } else if (eventType.equals(MouseEvent.MOUSE_RELEASED)) {
                    if(Window.getMouseMode() == MouseMode.MOVE) {
                        double currentX = event.getX();
                        double currentY = event.getY();
                        double x = currentX - initialEventX + initialComponentX;
                        double y = currentY - initialEventY + initialComponentY;
                        imageView.setX(x);
                        imageView.setY(y);
                    }
                }
            }
        };
        imageView.setOnMousePressed(mouseHandler);
        imageView.setOnMouseDragged(mouseHandler);
        imageView.setOnMouseReleased(mouseHandler);
        return imageView;
    }
    
    
    
    /**
     * Converts a text object in a tab to a certificatefield object that encapsulates its data.
     * @param node
     * @return
     */
    public CertificateField convertToCertificateField(Node node) {
        CertificateText text = (CertificateText) node;
        int x = (int) text.getX();
        int y = (int) text.getY();
        Font font = text.getFont();
        int fontSize = (int) font.getSize(); // TODO do something
        String fontFamily = font.getFamily();
        String fontWeight = font.getStyle();
        int fontStyle = (FontWeight.BOLD.toString().equalsIgnoreCase(fontWeight)) ? java.awt.Font.BOLD : java.awt.Font.PLAIN;
        
        // NEWLY CALCULATE MIDDLE AND ALIGN SHIT
        int middlex = (int) (x + text.getLayoutBounds().getWidth() / 2);
        int middley = (int) (y + text.getLayoutBounds().getHeight() / 2);
        
        CertificateField field = new CertificateField(middlex, middley);
//        CertificateField field = new CertificateField(x, y, text.getText(), fontSize, fontFamily, fontStyle);
        field.setFieldType(text.getCertificateField().getFieldType());
        if(field.getFieldType() == FieldType.TEXT) field.setFieldName(text.getText());
        if(field.getFieldType() == FieldType.COURSE) field.setCourses(text.getCertificateField().getCourses()); // new fix , not sure about it. LIFE SAVER! BIG BUG KILLER!
//        if(field.getFieldType() == FieldType.COURSE) { // debug
//            System.out.println("converting to certificatefield :\nCourses : ");
//            for(String s : field.getCourses()) {
//                System.out.print(s + ", ");
//            }
//            System.out.println("");
//        }
        field.setFontFamily(fontFamily);
        field.setFontSize(fontSize);
        field.setFontStyle(fontStyle);
        return field;
    }
    
    public CertificateField convertToCertificateImage(Node node) {
        ImageView imageView = (ImageView) node;
        int x = (int) imageView.getX();
        int y = (int) imageView.getY();
        /*there are two image views in the graph, one is the certificate image
         * and the second is the avatar image, we only need to get the avatar image.
         */
        if(x != 0 && y != 0) {
            // NEWLY CALCULATE MIDDLE AND ALIGN SHIT
//            int middlex = (int) (x + imageView.getImage().getWidth() / 2);
//            int middley = (int) (y + imageView.getImage().getHeight() / 2);
            
//            CertificateField field = new CertificateField(middlex, middley);
            CertificateField field = new CertificateField(x, y);
            field.setWidth((int) imageView.getImage().getWidth());
            field.setHeight((int) imageView.getImage().getHeight());
            field.setFieldType(FieldType.IMAGE);
            return field;
        } else {
            return null;
        }
    }

    /**
     * create a certificatefield array using all the text objects in a tab by specifying the group object
     * inside the tab and getting its fields manually.
     * @param group
     * @return
     */
    public ArrayList<CertificateField> populateCertificateFields(Group group) {
        ArrayList<CertificateField> certificateFieldList = new ArrayList<>();
        for (Node node : group.getChildren()) {
            if (node instanceof CertificateText) {
//                CertificateText text = (CertificateText) node;
//                certificateFieldList.add(text.getCertificateField()); // new fix, but this wont work, need alignment
                certificateFieldList.add(convertToCertificateField(node)); // fixed, courses
            } else if(node instanceof ImageView) {
                CertificateField field = convertToCertificateImage(node); // this returns null for the main certificate image
                if(field != null) {
                    certificateFieldList.add(field);
                }
            }
        }
        return certificateFieldList;
    }

        /**
     * used by outsiders, so public
     * create a new blank certificate wrapper object using the specified file and image
     * @param name
     * @param imagePath
     * @return 
     */
    public CertificateWrapper createCertificateWrapper(String name, String imagePath) {
        CertificateWrapper certificateWrapper = new CertificateWrapper();
        certificateWrapper.setName(name); // used as tab name and save name
        certificateWrapper.setCertificateImage(new File(imagePath));
        certificateWrapper.setCertificateFields(new ArrayList<CertificateField>());
        return certificateWrapper;
    }
    
    
    /**
     * saves certificate using the elements from the tablist and ceritificate list.
     * @param index
     * @param file 
     */
    public void saveFileAtTab(CertificateTab tab, File file) {
        ScrollPane scrollPane = (ScrollPane) tab.getContent();
        Group group = (Group) scrollPane.getContent();
        CertificateWrapper wrapper = tab.getCertificateWrapper(); // TODO change certificatelist to tabs
        ArrayList<CertificateField> certificateFieldList = populateCertificateFields(group);
        wrapper.setCertificateFields(certificateFieldList);
        System.out.println("populated wrapper :" + wrapper);
        createCertificateFile(file, wrapper);
        tab.setChanged(false);
//        tab.setFile(file); // done at an upper level
        UserDataManager.setLastActivityPath(file);
    }

    /**
     * Used to retrieve certificatewrapper object from the xml template file.
     * @param file
     * @return
     */
    public CertificateWrapper openTemplate(File file) { // new implementation
        CertificateWrapper wrapper = null;
        boolean fileOk = testFileIntegrity(file);
        if(fileOk) {
            try {
                JAXBContext context = JAXBContext.newInstance(CertificateWrapper.class);
                Unmarshaller um = context.createUnmarshaller();
                if (file.exists()) {
                    wrapper = (CertificateWrapper) um.unmarshal(file);
                }
                System.out.println("Unmarshalling...\n" + wrapper); // IMPORTANT debug
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        return wrapper;
    }

    /**
     * Low level method used to save a certificatewrapper object as a template xml file.
     * @param file
     * @param wrapper
     */
    public void createCertificateFile(File file, CertificateWrapper wrapper) {
        try {
            JAXBContext context = JAXBContext.newInstance(CertificateWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(wrapper, file);
            UserDataManager.setCertificateFilePath(file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
        
    /**
     * used to check the xml file for issues.
     * @param file 
     */
    public boolean testFileIntegrity(File file) {
        if(file != null){
            if(file.isFile()){
                // TODO not yet implemented
            }
        }
        return true;
    }
    
    /**
     * checks for the extension of the file and corrects.
     * @param file
     * @return 
     */
    public File correctXmlExtension(File file) {
        String path = file.getAbsolutePath();
        if(!path.endsWith(".xml")) {
//            System.out.print("correcting file extension... , previous path : "+ path ); // debug
            path = path.concat(".xml");
//            System.out.println(" , corrected path : " + path); // debug
        }
        return new File(path);
    }
    
    public Task createWorker() {
        return new Task() {

            @Override
            protected Object call() throws Exception {
                // 
                return true;
            }
            
        };
    }
    
    /********* END LOW LEVEL METHODS *********/

    
    public String incrementRegno(String regno) {
        Pattern digitPattern = Pattern.compile("(\\d)");
        Matcher matcher = digitPattern.matcher(regno);
        StringBuffer result = new StringBuffer();
        matcher.appendReplacement(result, String.valueOf(Integer.parseInt(matcher.group(1)) + 1));
        matcher.appendTail(result);
        return result.toString();
    }
    
}
