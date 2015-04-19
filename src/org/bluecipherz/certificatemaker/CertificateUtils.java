/*
 * Copyright BCZ Inc. 2015.
 * This file is part of Certificate Maker.
 *
 * Certificate Maker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Certificate Maker is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Certificate Maker.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bluecipherz.certificatemaker;

import java.io.File;
import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

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
     * UPDATE : This method has no formal use now, it just aligns
     * Converts a text object in a tab to a certificatefield object that encapsulates its data.
     * @param node
     * @return
     */
    @Deprecated
    public CertificateField convertToCertificateField(Node node) {
//        CertificateText text = (CertificateText) node;
//        int x = (int) text.getX();
//        int y = (int) text.getY();
//        Font font = text.getFont();
//        int fontSize = (int) font.getSize(); // TODO do something
//        String fontFamily = font.getFamily();
//        String fontWeight = font.getStyle();
//        int fontStyle = (FontWeight.BOLD.toString().equalsIgnoreCase(fontWeight)) ? java.awt.Font.BOLD : java.awt.Font.PLAIN;
        
        // NEWLY CALCULATE MIDDLE AND ALIGN SHIT
//        int middlex = (int) (x + text.getLayoutBounds().getWidth() / 2);
//        int middley = (int) (y + text.getLayoutBounds().getHeight() / 2);
        
//        CertificateField field = new CertificateField(middlex, y);  // redundant
//        CertificateField field = text.getCertificateField();
//        field.setX(middlex);
//        field.setY(y);
//        CertificateField field = new CertificateField(middlex, middley);
//        CertificateField field = new CertificateField(x, y, text.getText(), fontSize, fontFamily, fontStyle);
//        field.setFieldType(text.getCertificateField().getFieldType()); // redundant
//        if(field.getFieldType() == FieldType.TEXT) field.setFieldName(text.getText());
//        if(field.getFieldType() == FieldType.COURSE) field.setCourses(text.getCertificateField().getCourses()); // new fix , not sure about it. LIFE SAVER! BIG BUG KILLER!
//        if(field.getFieldType() == FieldType.COURSE) { // debug
//            Debugger.log("converting to certificatefield :\nCourses : ");
//            for(String s : field.getCourses()) {
//                System.out.print(s + ", ");
//            }
//            Debugger.log("");
//        }
//        field.setFontFamily(fontFamily);
//        field.setFontSize(fontSize);
//        field.setFontStyle(fontStyle); 
//        return field;
        return null;
    }
    
    @Deprecated
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
//            CertificateField field = new CertificateField(x, y);
//            field.setWidth((int) imageView.getImage().getWidth());
//            field.setHeight((int) imageView.getImage().getHeight());
//            field.setFieldType(FieldType.IMAGE);
//            return field;
            return null; // ugh
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
    @Deprecated
    public ArrayList<CertificateField> populateCertificateFields(Group group) {
        ArrayList<CertificateField> certificateFieldList = new ArrayList<>();
//        for (Node node : group.getChildren()) {
//            if (node instanceof CertificateText) {
////                CertificateText text = (CertificateText) node;
////                certificateFieldList.add(text.getCertificateField()); // new fix, but this wont work, need alignment
//                certificateFieldList.add(convertToCertificateField(node)); // fixed, courses
//            } else if(node instanceof ImageView) {
//                CertificateField field = convertToCertificateImage(node); // this returns null for the main certificate image
//                if(field != null) {
//                    certificateFieldList.add(field);
//                }
//            }
//        }
        return certificateFieldList;
    }

        /**
     * used by outsiders, so public
     * create a new blank certificate wrapper object using the specified file and image
     * @param name
     * @param imagePath
     * @return 
     */
    public CertificateWrapper createEmptyWrapper(String name, String imagePath) {
        Debugger.log("[CertificateUtils] Creating empty wrapper..."); // debug
        CertificateWrapper wrapper = new CertificateWrapper();
        wrapper.setName(name); // used as tab name and save name
        wrapper.setCertificateImage(new File(imagePath));
        ArrayList<CertificateField> fields = new ArrayList<>();
//        ObservableList<CertificateField> fields = FXCollections.observableArrayList();
//        fields.addListener(new ListChangeListener<CertificateField>() {
//            @Override
//            public void onChanged(ListChangeListener.Change<? extends CertificateField> change) {
//                Debugger.log("wrapper list changed : current size " + change.getList().size());
//            }
//        });
        wrapper.setCertificateFields(fields);
        return wrapper;
    }
    
    
    /**
     * saves certificate using the elements from the tablist and ceritificate list.
     * @param index
     * @param file 
     */
    public CertificateWrapper saveFileAtTab(CertificateTab tab, File file) {
//        ScrollPane scrollPane = (ScrollPane) tab.getContent();
//        Group group = (Group) scrollPane.getContent();
        CertificateWrapper wrapper = tab.getSerializableWrapper();
        Debugger.log("[CertificateUtils] retrieving wrapper : total contents " + wrapper.getCertificateFields().size()); // debug
//        ArrayList<CertificateField> certificateFieldList = populateCertificateFields(group);
//        wrapper.setCertificateFields(certificateFieldList);
//        Debugger.log("populated wrapper :\n" + wrapper); // very helpful debug
        saveCertificateFile(file, wrapper);
//        tab.setFile(file); // done at an upper level
        UserDataManager.setLastActivityPath(file);
        return wrapper;
    }

    /**
     * Used to retrieve certificatewrapper object from the xml template file.
     * @param file
     * @return
     */
    public CertificateWrapper openTemplate(File file) { // new implementation
        CertificateWrapper wrapper = null;
        try {
            JAXBContext context = JAXBContext.newInstance(CertificateWrapper.class);
            Unmarshaller um = context.createUnmarshaller();
            // new debug
            um.setEventHandler(new ValidationEventHandler() {
                @Override
                public boolean handleEvent(ValidationEvent event) {
                    throw new RuntimeException(event.getMessage(), event.getLinkedException());
                }
            });
            if (file.exists()) {
                wrapper = (CertificateWrapper) um.unmarshal(file);
            }
            Debugger.log("[CertificateUtils] unmarshalling file + " + file.getAbsolutePath() 
                    + "...\n" + wrapper); // IMPORTANT debug, update : VERY IMPORTANT!~
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return wrapper;
    }

    /**
     * Low level method used to save a certificatewrapper object as a template xml file.
     * @param file
     * @param wrapper
     */
    public void saveCertificateFile(File file, CertificateWrapper wrapper) {
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
     * checks for the extension of the file and corrects.
     * @param file
     * @return 
     */
    public File correctXmlExtension(File file) {
        if(file != null) {
            String path = file.getAbsolutePath();
            if(!path.endsWith(".xml")) {
    //            System.out.print("correcting file extension... , previous path : "+ path ); // debug
                path = path.concat(".xml");
    //            Debugger.log(" , corrected path : " + path); // debug
            }
            return new File(path);
        } else return null; // new null pointer fix
    }
    
    /********* END LOW LEVEL METHODS *********/

        
    /**
     * makes sure the given file has a .png extension
     * @param file
     * @return 
     */
    public File correctPNGExtension(File file) {
        String path = file.getAbsolutePath();
        if(!path.endsWith(".png") || !path.endsWith(".jpg")) {
            path = path.concat(".png");
        }
        return new File(path);
    }

    public File correctJPGExtension(File file) {
        String path = file.getAbsolutePath();
        if(!path.endsWith(".png") || !path.endsWith(".jpg")) {
            path = path.concat(".jpg");
        }
        return new File(path);
    }
    
    public FileChooser getXMLFileChooser() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extensionFilter);
        return fileChooser;
    }
    
    public FileChooser getImageFileChooser() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("JPEG & PNG Files", "*.jpg", "*.jpeg", "*.png");
        fileChooser.getExtensionFilters().add(extensionFilter);
        return fileChooser;
    }
    
    public boolean isImageFile(File file) {
        if(!file.isFile()) return false;
        if(!file.canRead()) return false;
        String path = file.getAbsolutePath();
        if(!path.endsWith(".jpg") || !path.endsWith(".png")) return false;
        return true;
    }
    
}
