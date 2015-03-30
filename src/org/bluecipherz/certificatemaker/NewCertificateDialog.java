/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author bazi
 */
class NewCertificateDialog extends Stage {
    private final CertificateWrapper wrapper; // no use yet
    private final Stage primaryStage;
    private final Image certificateImage;
    private File avatarImage;
    private final Window window; // no use yet
    private final ObservableList<TextField> textFields = FXCollections.observableArrayList();
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
        gridPane = createEntryFieldsandLabels(wrapper);
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
        
        gridPane.add(savePathLabel, 0, 0);
        gridPane.add(savePathField, 1, 0);
        gridPane.add(savePathBtn, 2, 0);
        
        savePathBtn.setOnAction(getBrowseAction());
        
        int row = 1;
        int lastcol = 1; // sometimes you dont need to add browse button if there is no image specified
        Set<Map.Entry<FieldType, CertificateField>> set = wrapper.getCertificateFields().entrySet();
        
        for (Map.Entry<FieldType, CertificateField> certificateField : set) {
            Label label;
            if(certificateField.getKey() == FieldType.TEXT){
                label = new Label(certificateField.getValue().getFieldName());
                System.out.println("Adding text " + certificateField.getValue().getFieldName());
            } else {
                label = new Label(certificateField.getKey().toString());
                System.out.println("Adding " + certificateField.getKey().toString());
            }
            gridPane.add(label, 0, row);
            
            if (certificateField.getKey() == FieldType.IMAGE) {
                avatarPathField = new TextField();
                gridPane.add(avatarPathField, 1, row);
                textFields.add(avatarPathField); // save a copy for printing later
//                label.setText(certificateField.getKey().toString());
                lastcol++;
                Button browseButton = getBrowseButton(); // add browse button for avatar image
                gridPane.add(browseButton, lastcol, row);
            } else if(certificateField.getKey() == FieldType.COURSE){
                ComboBox<String> box = new ComboBox( (ObservableList) certificateField.getValue().getCourses());
                gridPane.add(box, 1, row);
            } else {
                TextField textField = new TextField();
                gridPane.add(textField, 1, row);
                textFields.add(textField); // save a copy for printing later
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

    private void createCertificateImage(CertificateWrapper wrapper, Image certificateImage, File file) {
        File saveFile = file;
        if(saveFile == null) {
            fileChooser.setTitle("Save certificate");
            saveFile = fileChooser.showSaveDialog(primaryStage); // dependency
            UserDataManager.setLastSavePath(saveFile);
        }
        saveFile = correctPngExtension(saveFile); // correct file extension
        ImageUtils.createCertificateImage(wrapper, certificateImage, saveFile);
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
            }
        });
        return button;
    }
    
    private void retrieveInfoAndSendForPrinting() {
        // System.out.println("Populating certificate fields :"); // debug
        // retrieve all the textFields from the arraylist
        HashMap<FieldType, String> fields = new HashMap<>();
        int index = 0; // necessary locating textfields saved earlier on construction
        Set<Map.Entry<FieldType, CertificateField>> set = wrapper.getCertificateFields().entrySet();
        for (Map.Entry<FieldType, CertificateField> field : set) {
            if(field.getKey() == FieldType.IMAGE) {
                fields.put(FieldType.IMAGE, textFields.get(index).getText());
            } else {
                fields.put(FieldType.TEXT, textFields.get(index).getText());
            }
            index++;
        }
        
        if(UserDataManager.getLastSavePath() != null) {
            File saveFile = new File(UserDataManager.getLastSavePath().getAbsolutePath() + fields.get(FieldType.REGNO).toString());
            createCertificateImage(wrapper, certificateImage, saveFile);
        } else {
            createCertificateImage(wrapper, certificateImage, null);
        }
    }
    
    private void clearOrIncrementFields() {
        
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
