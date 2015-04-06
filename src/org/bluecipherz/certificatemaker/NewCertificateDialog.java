/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import com.sun.javafx.scene.KeyboardShortcutsHandler;
import com.sun.javafx.scene.traversal.Direction;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    private RegexUtils regexUtils;
    private CertificateUtils certificateUtils;
    private final ObservableList<Node> dataHolders = FXCollections.observableArrayList();
    private File lastSavePath;
    
    private FileChooser fileChooser;
    private TextField savePathField;
    private TextField avatarPathField;
    private GridPane gridPane;
    
    private final ImageWriterService iws;
    
    private double FIELD_WIDTH = 150;

    public NewCertificateDialog(Stage parent, CertificateWrapper wrapper, final Window window) {
        super();
        primaryStage = parent;
        this.window = window;
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Create new certificate");
        getIcons().add(ResourceManger.getInstance().newx16);
        iws = new ImageWriterService(window);
        
        regexUtils = new RegexUtils();
        certificateUtils = new CertificateUtils();
        
        this.wrapper = wrapper;
        this.certificateImage = new Image(wrapper.getCertificateImage().toURI().toString());
        
        fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JPEG & PNG Files", "*.jpg", "*.jpeg", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        gridPane = createEntryFieldsandLabels(wrapper); // MEGA FUNCTION
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
        savePathField.setPrefWidth(FIELD_WIDTH);
        Button savePathBtn = new Button("Browse...");
        
        gridPane.add(savePathLabel, 0, 0); // col, rows
        gridPane.add(savePathField, 1, 0);
        gridPane.add(savePathBtn, 2, 0);
        
        savePathBtn.setOnAction(getBrowseAction());
        
        int row = 1;
//        int lastcol = 1; // sometimes you dont need to add browse button if there is no image specified
        
//        HashMap<FieldType, CertificateField> collected = populateHashMap(wrapper);
//        for(Map.Entry<FieldType, CertificateField> field : collected.entrySet()) {
//            
//        }
//        for(int i=0;i<collected.size();i++) {
//            Label label;
//            if(collected.containsKey(FieldType.DATE)) {
//                label = new Label("Date");
//            } else if(collected.containsKey(FieldType.REGNO)) {
//                label = new Label("Date");
//            } else if(collected.containsKey(FieldType.COURSE)) {
//                label = new Label("Date");
//            } else if(collected.containsKey(FieldType.IMAGE)) {
//                label = new Label("Date");
//            } else if(collected.containsKey(FieldType.TEXT)) {
//            }
//        }
//        ArrayList<CertificateField> certificateFields = orderFields(wrapper.getCertificateFields());
        
        /* start populating gui components according to wrapper*/
        System.out.println("Populating fields");
        for (CertificateField certificateField : wrapper.getCertificateFields()) {
            Label label;
            if(certificateField.getFieldType() == FieldType.TEXT){
                label = new Label(certificateField.getFieldName() + " : ");
                System.out.println("Adding TEXT : " + certificateField.getFieldName()); // debug
            } else {
                label = new Label(certificateField.getFieldType().toString() + " : ");
                System.out.println("Adding " + certificateField.getFieldType().toString()); // debug
            }
            gridPane.add(label, 0, row);
            
            if (certificateField.getFieldType() == FieldType.IMAGE) {
                avatarPathField = new TextField();
                avatarPathField.setPrefWidth(FIELD_WIDTH);
                gridPane.add(avatarPathField, 1, row);
                dataHolders.add(avatarPathField); // save a copy for printing later
                Button browseButton = getBrowseButton();
                gridPane.add(browseButton, 2, row);
            } else if(certificateField.getFieldType() == FieldType.COURSE){
                System.out.println("Loading courses : " + certificateField.getCourses().size()); // debug
                ObservableList<String> list = FXCollections.observableArrayList(certificateField.getCourses());
                ComboBox<String> box = new ComboBox(list);
                if(box.getPrefWidth() < FIELD_WIDTH) box.setPrefWidth(FIELD_WIDTH);
//                box.setOnAction(getComboActionTraverse()); // TODO action traverse
                gridPane.add(box, 1, row);
                dataHolders.add(box); // save a copy for printing later
                box.getSelectionModel().select(0);
            } else if(certificateField.getFieldType() == FieldType.COURSEDETAILS) {
                ObservableList<String> list = FXCollections.observableArrayList(certificateField.getCoursesDetails());
                ComboBox<String> box = new ComboBox<>(list);
                if(box.getPrefWidth() < FIELD_WIDTH) box.setPrefWidth(FIELD_WIDTH);
//                box.setOnAction(getComboActionTraverse()); // TODO action traverse
                gridPane.add(box, 1, row);
                dataHolders.add(box);
                box.getSelectionModel().select(0);
            } else if(certificateField.getFieldType() == FieldType.TEXT){
//                TextField textField = new TextField();
                ComboBox<String> textField = new ComboBox<>();
                if(textField.getPrefWidth() < FIELD_WIDTH) textField.setPrefWidth(FIELD_WIDTH);
                textField.setEditable(true);
//                textField.widthProperty().
                textField.setOnKeyPressed(getActionTraverse());
                gridPane.add(textField, 1, row);
                dataHolders.add(textField); // save a copy for printing later
            } else {
                TextField textField = new TextField();
                textField.setOnKeyPressed(getActionTraverse());
                gridPane.add(textField, 1, row);
                dataHolders.add(textField); // save a copy for printing later
            }
            row++;
        }
        /* populated */
        
        Button nextButton = new Button("Next");
        GridPane.setHalignment(nextButton, HPos.RIGHT);
        gridPane.add(nextButton, 1 , row); // add before the last column
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if(isFieldsFilled()) {
                    retrieveInfoAndSendForPrinting();
                    clearOrIncrementFields();
                }
            }
        });
        Button finishButton = new Button("Finish");
        GridPane.setHalignment(finishButton, HPos.RIGHT);
        gridPane.add(finishButton, 2, row); // add to the last column
        finishButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(isFieldsFilled()) {
                    retrieveInfoAndSendForPrinting();
                    close();
                }
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
            if (field.getFieldType() == FieldType.COURSE || field.getFieldType() == FieldType.COURSEDETAILS || field.getFieldType() == FieldType.TEXT) {
                fields.put(field, ((ComboBox)dataHolders.get(index)).getSelectionModel().getSelectedItem().toString());
            } else {
                TextField tf = (TextField)dataHolders.get(index);
                fields.put(field, tf.getText());
                if(field.getFieldType() == FieldType.REGNO) savename = tf.getText();
            }
            index++;
        }
        
        
        File saveFile;
        if(savename.contains("/")) {
            // remove slash and every character after slash because windows doesnt support slashes in filenames
            saveFile = new File(savePathField.getText() + File.separatorChar + savename.substring(0, savename.lastIndexOf("/")));
        } else {
            saveFile = new File(savePathField.getText() + File.separatorChar + savename);
        }
        System.out.println("Savename : " + savename + "\nwriting certificate image : " + saveFile.getAbsolutePath());

        saveFile = certificateUtils.correctPngExtension(saveFile); // correct file extension
//        progressiveSave(fields, saveFile);
        
        iws.takeWork(certificateImage, fields, saveFile);
        
//        try {
//            BufferedImage createBufferedImage = ImageUtils.createBufferedImage(certificateImage, fields);
//            System.out.println("created buffered image...");
//            ImageUtils.saveImage(createBufferedImage, saveFile.getAbsolutePath());
//            System.out.println("saved image...");
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(NewCertificateDialog.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(NewCertificateDialog.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    private EventHandler<? super KeyEvent> getActionTraverse() {
        return new EventHandler<KeyEvent>() { // TODO focus traversal
            @Override
            public void handle(KeyEvent t) {
                if(t.getCode() == KeyCode.ENTER) {
                    System.out.println("KeyCode.ENTER");
                    Node n = (Node) t.getSource();
                    n.impl_traverse(Direction.NEXT);
                }
            }
        };
    }

    
    private boolean isFieldsFilled() {
        boolean filled = true;
        for(Node node : dataHolders) {
            if(node instanceof TextField) {
                TextField tf = (TextField) node;
                if("".equalsIgnoreCase(tf.getText())) filled = false;
            }
        }
        return filled;
    }
    
    private void clearOrIncrementFields() {
        int index = 0;
        for(CertificateField field : wrapper.getCertificateFields()) {
            Node node = dataHolders.get(index);
            if(node instanceof TextField) {
                TextField tf = (TextField) node;
                if(field.getFieldType() == FieldType.IMAGE) {
                    tf.setText("");
                } else if(field.getFieldType() == FieldType.REGNO) {
                    tf.setText(regexUtils.incrementRegno(tf.getText()));
                }
            } else if(node instanceof ComboBox) {
                ComboBox<String> box = (ComboBox) node;
                if(field.getFieldType() == FieldType.TEXT) {
                    String item = box.getSelectionModel().getSelectedItem();
                    if(!box.getItems().contains(item)) box.getItems().add(item); // save history
                    if(!box.getItems().contains("")) box.getItems().add(0, "");
                    box.getSelectionModel().select(0); // reset value
                }
            }
            index++;
        }
    }
    

    private EventHandler<ActionEvent> getBrowseAction() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                DirectoryChooser dirChooser = new DirectoryChooser();
//                dirChooser.setInitialDirectory();
                dirChooser.setTitle("Set save path");
                File file = dirChooser.showDialog(primaryStage);
                if(file != null) {
                    savePathField.setText(file.getAbsolutePath());
                    UserDataManager.setCertificateSavePath(file);
                }
            }
        };
    }
}
