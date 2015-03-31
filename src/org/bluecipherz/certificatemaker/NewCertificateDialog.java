/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.stream.ImageOutputStream;

/**
 * needs lots of work!, lots of redundant methods mainly changesubjecttext() & generatecertificatefield()
 * @author bazi
 */
class NewCertificateDialog extends Stage {
    private final CertificateWrapper wrapper; // no use yet
    private final Stage primaryStage;
    private final Image certificateImage;
    private File avatarImage;
    private final Window window; // no use yet
    private final ObservableList<Node> dataHolders = FXCollections.observableArrayList();
    private File lastSavePath;
    
    FileChooser fileChooser;
    private TextField savePathField;
    private TextField avatarPathField;
    private GridPane gridPane;

    public NewCertificateDialog(Stage parent, CertificateWrapper wrapper, final Window window) {
        super();
        primaryStage = parent;
        this.window = window;
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Create new certificate");
        this.wrapper = wrapper;
        this.certificateImage = new Image(wrapper.getCertificateImage().toURI().toString());
        //            org.bluecipherz.certificatemaker.Window.this.createNewTab(wrapper);
        fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JPEG & PNG Files", "*.jpg", "*.jpeg", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        gridPane = createEntryFieldsandLabels(wrapper); // null pointer source
        Scene scene = new Scene(gridPane, Color.WHITE);
        setScene(scene);
        sizeToScene();
        show();
    }

    private GridPane createEntryFieldsandLabels(final CertificateWrapper wrapper) {
        final GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        
        Label savePathLabel = new Label("Save path : ");
        savePathField = new TextField();
        Button savePathBtn = new Button("Browse...");
        
        gridPane.add(savePathLabel, 0, 0); // col, rows
        gridPane.add(savePathField, 1, 0);
        gridPane.add(savePathBtn, 2, 0);
        
        savePathBtn.setOnAction(getBrowseAction());
        
        int row = 1;
        int lastcol = 1; // sometimes you dont need to add browse button if there is no image specified
        
        for (CertificateField certificateField : wrapper.getCertificateFields()) {
            Label label;
            if(certificateField.getFieldType() == FieldType.TEXT){
                label = new Label(certificateField.getFieldName());
                System.out.println("Adding text " + certificateField.getFieldName());
            } else {
                label = new Label(certificateField.getFieldType().toString());
                System.out.println("Adding " + certificateField.getFieldType().toString());
            }
            gridPane.add(label, 0, row);
            
            if (certificateField.getFieldType() == FieldType.IMAGE) {
                avatarPathField = new TextField();
                gridPane.add(avatarPathField, 1, row);
                dataHolders.add(avatarPathField); // save a copy for printing later
                lastcol++;
                Button browseButton = getBrowseButton();
                gridPane.add(browseButton, lastcol, row);
            } else if(certificateField.getFieldType() == FieldType.COURSE){
                System.out.println("Loading courses : " + certificateField.getCourses().size());
                ObservableList<String> list = FXCollections.observableArrayList(certificateField.getCourses());
                ComboBox<String> box = new ComboBox(list);
                gridPane.add(box, 1, row);
                dataHolders.add(box); // save a copy for printing later
                box.getSelectionModel().select(0);
            } else {
                TextField textField = new TextField();
                gridPane.add(textField, 1, row);
                dataHolders.add(textField); // save a copy for printing later
            }
            row++;
        }
        
        Button nextButton = new Button("Next");
        GridPane.setHalignment(nextButton, HPos.RIGHT);
        gridPane.add(nextButton, lastcol-1 , row); // add before the last column
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                retrieveInfoAndSendForPrinting();
                clearOrIncrementFields();
            }
        });
        
        Button finishButton = new Button("Finish");
        GridPane.setHalignment(finishButton, HPos.RIGHT);
        gridPane.add(finishButton, lastcol, row); // add to the last column
        finishButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                retrieveInfoAndSendForPrinting();
                close();
            }
        });
        return gridPane;
    }

    private Button getBrowseButton() {
        Button button = new Button("Browse...");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                fileChooser.setTitle("Open photo");
                if(UserDataManager.getAvatarImagePath() != null) {
                    fileChooser.setInitialDirectory(UserDataManager.getAvatarImagePath());
                }
                File file = fileChooser.showOpenDialog(primaryStage);
                UserDataManager.setAvatarImagePath(file);
                avatarPathField.setText(file.getAbsolutePath());
            }
        });
        return button;
    }
    
    private void retrieveInfoAndSendForPrinting() {
        // System.out.println("Populating certificate fields :"); // debug
        HashMap<CertificateField, String> fields = new HashMap<>(); // certificate field and user input
        String savename = "";
        int index = 0;
        for (CertificateField field : wrapper.getCertificateFields()) {
            if (field.getFieldType() == FieldType.COURSE) {
                fields.put(field, ((ComboBox)dataHolders.get(index)).getSelectionModel().getSelectedItem().toString());
            } else {
                TextField tf = (TextField)dataHolders.get(index);
                fields.put(field, tf.getText());
                if(field.getFieldType() == FieldType.REGNO) savename = tf.getText();
            }
            index++;
        }
        
        
        File saveFile = new File(savePathField.getText() + File.separatorChar + savename);
        System.out.println("writing certificate image : " + saveFile.getAbsolutePath());
//        File saveFile = new File(savePathField.getText() + fields.getKey(FieldType.REGNO)); // doesnt work
        saveFile = correctPngExtension(saveFile); // correct file extension
        try {
            //        createSaveTask(fields, saveFile); // generates error
            BufferedImage createBufferedImage = ImageUtils.createBufferedImage(certificateImage, fields);
            ImageUtils.saveImage(createBufferedImage, saveFile.getAbsolutePath());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NewCertificateDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private String incrementRegno(String regno) {
        String parts[] = regno.split("/");
        
        
        
        return regno;
    }
    
    class ImageWriteProgressListener implements IIOWriteProgressListener {

        @Override
        public void imageStarted(ImageWriter source, int imageIndex) {
            System.out.println("Image #" + imageIndex + " started " + source);
        }

        @Override
        public void imageProgress(ImageWriter source, float percentageDone) {
            System.out.println("Image progress " + source + ": " + percentageDone + "%");
        }

        @Override
        public void imageComplete(ImageWriter source) {
            System.out.println("Image Completer " + source);
        }

        @Override
        public void thumbnailStarted(ImageWriter source, int imageIndex, int thumbnailIndex) {
            System.out.println("Thumbnail progress " + source + ", " + thumbnailIndex + " of" + imageIndex);
        }

        @Override
        public void thumbnailProgress(ImageWriter source, float percentageDone) {
            System.out.println("Thumbnail started " + source + ": " + percentageDone + "%");
        }

        @Override
        public void thumbnailComplete(ImageWriter source) {
            System.out.println("Thumbnail complete " + source);
        }

        @Override
        public void writeAborted(ImageWriter source) {
            System.out.println("Write aborted " + source);
        }
        
    }
    
    private void clearOrIncrementFields() {
        int index = 0;
        for(CertificateField field : wrapper.getCertificateFields()) {
            Node node = dataHolders.get(index);
            if(node instanceof TextField) {
                TextField tf = (TextField) node;
                if(field.getFieldType() == FieldType.IMAGE || field.getFieldType() == FieldType.TEXT) {
                    tf.setText("");
                } else if(field.getFieldType() == FieldType.REGNO) {
                    tf.setText(incrementRegno(tf.getText()));
                }
            }
            index++;
        }
    }
    
    public void createSaveTask(HashMap<CertificateField, String> fields, File saveFile) {
        try {
            // write certificate image
            BufferedImage bufferedImage = ImageUtils.createBufferedImage(certificateImage, fields); // IMPORTANT
            FileOutputStream fos = new FileOutputStream(saveFile);
            Iterator writers = ImageIO.getImageWritersBySuffix(".png");
            ImageWriter writer = (ImageWriter) writers.next(); // creates nosuchelement error
            ImageOutputStream ios = ImageIO.createImageOutputStream(fos);
            writer.setOutput(ios);
            writer.addIIOWriteProgressListener(new ImageWriteProgressListener());
            writer.write(bufferedImage);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NewCertificateDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NewCertificateDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    /**
     * makes sure the given file has a .png extension
     * @param file
     * @return 
     */
    private File correctPngExtension(File file) {
        String path = file.getAbsolutePath();
        if(!path.endsWith(".png") || !path.endsWith(".jpg")) {
            path = path.concat(".png");
        }
        return new File(path);
    }

    private EventHandler<ActionEvent> getBrowseAction() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                DirectoryChooser dirChooser = new DirectoryChooser();
//                dirChooser.setInitialDirectory();
                dirChooser.setTitle("Set save path");
                File file = dirChooser.showDialog(primaryStage);
                savePathField.setText(file.getAbsolutePath());
                UserDataManager.setCertificateSavePath(file);
            }
        };
    }
}
