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
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
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
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
class CreateCertificateDialog extends Stage {
//    private final CertificateWrapper wrapper; // no use yet
    private final Stage primaryStage;
    private Image certificateImage;
    private CertificateWrapper wrapper;
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
    private EventHandler<? super KeyEvent> actionTraverse = getActionTraverse();
    private EventHandler<ActionEvent> actionComboTraverse = getComboActionTraverse();
    private EventHandler<KeyEvent> enterKeyAction = getEnterKeyAction();
    
    private final ImageWriterService iws;
    
    private double FIELD_WIDTH = 150;
    private ProgressIndicator indicator;
    
    private ReadOnlyDoubleWrapper populatingProgress = new ReadOnlyDoubleWrapper(0);
    private Scene scene;
    private final EventHandler<ActionEvent> buttonAction;
    private Button finishButton;
    private Button nextButton;

    public CreateCertificateDialog(Stage parent, final Window window) {
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
        
        fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JPEG & PNG Files", "*.jpg", "*.jpeg", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        
        scene = new Scene(new VBox(), Color.WHITE);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if(t.getCode() == KeyCode.ESCAPE) {
                    close();
                }
            }
        });

        buttonAction = getButtonActions();
        nextButton = new Button("Next");
        GridPane.setHalignment(nextButton, HPos.RIGHT);
        nextButton.setOnAction(buttonAction);
        finishButton = new Button("Finish");
        GridPane.setHalignment(finishButton, HPos.RIGHT);
        finishButton.setOnAction(buttonAction);
    }

    public void openFor(CertificateWrapper newWrapper) {
        iws.setDefaultExtension(UserDataManager.getDefaultImageFormat()); // update changes
        iws.setA3Output(UserDataManager.isA3Output()); // update changes
//        certificateImage = new Image(wrapper.getCertificateImage().toURI().toString());
        if(this.wrapper == null) {
            System.out.println("setting new wrapper..."); // debug
            gridPane = createEntryFieldsandLabels(newWrapper); // MEGA FUNCTION
            this.wrapper = (CertificateWrapper) newWrapper.clone(); // new wrapper bug solution
        } else {
//            System.out.println("old wrapper : " + this.wrapper); // new debug
//            System.out.println("new wrapper : " + newWrapper); // new debug
            if(!this.wrapper.equals(newWrapper)) {
                System.out.println("wrapper changed..."); // debug
                gridPane = createEntryFieldsandLabels(newWrapper);
                this.wrapper = (CertificateWrapper) newWrapper.clone(); // new wrapper bug solution
            } else {
                System.out.println("wrapper hasnt changed..."); // debug
            }
        }
        iws.setCertificateImage(new Image(this.wrapper.getCertificateImage().toURI().toString()));
        scene.setRoot(gridPane);
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
        Node previousNode;
        for (CertificateField certificateField : wrapper.getCertificateFields()) {
            Label label;
            if(certificateField.getFieldType() == FieldType.TEXT){
                label = new Label(certificateField.getFieldName() + " : ");
                System.out.println("Adding TEXT : " + certificateField.getFieldName()); // debug
            } else {
                label = new Label(certificateField.getFieldType().getName() + " : "); // new enum implementation
//                label = new Label(certificateField.getFieldType().toString() + " : ");
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
                box.setMinWidth(FIELD_WIDTH);
//                if(box.getPrefWidth() < FIELD_WIDTH) box.setPrefWidth(FIELD_WIDTH); // stupid width set
                // USEFUL DEBUG INFO
//                System.out.println("combobox layout width " + box.getLayoutBounds().getWidth() + ", layout height " + box.getLayoutBounds().getHeight()); // debug
//                System.out.println("combobox boundsinlocal width " + box.getBoundsInLocal().getWidth() + ", boundsinlocal height " + box.getBoundsInLocal().getHeight()); // debug                
//                System.out.println("combobox boundsinparent width " + box.getBoundsInParent().getWidth() + ", boundsinparent height " + box.getBoundsInParent().getHeight()); // debug                
                // END
//                box.setOnAction(getComboActionTraverse()); // TODO action traverse
//                box.setOnKeyPressed(actionTraverse); // doesnt work
//                box.setOnAction(actionComboTraverse); // not good
                box.addEventFilter(KeyEvent.KEY_PRESSED, enterKeyAction);
                gridPane.add(box, 1, row);
                dataHolders.add(box); // save a copy for printing later
                box.getSelectionModel().select(0);
            } else if(certificateField.getFieldType() == FieldType.COURSEDETAILS) {
                ObservableList<String> list = FXCollections.observableArrayList(certificateField.getCoursesDetails());
                ComboBox<String> box = new ComboBox<>(list);
                box.setMinWidth(FIELD_WIDTH);
//                if(box.getPrefWidth() < FIELD_WIDTH) box.setPrefWidth(FIELD_WIDTH); // stupid width set
//                box.setOnAction(getComboActionTraverse()); // TODO action traverse
//                box.setOnKeyPressed(actionTraverse); // doesnt work
//                box.setOnAction(actionComboTraverse); // not good
                box.addEventFilter(KeyEvent.KEY_PRESSED, enterKeyAction);
                gridPane.add(box, 1, row);
                dataHolders.add(box);
                box.getSelectionModel().select(0);
            } else if(certificateField.getFieldType() == FieldType.TEXT){
//                TextField textField = new TextField();
                ComboBox<String> box = new ComboBox<>();
                box.setMinWidth(FIELD_WIDTH);
//                if(box.getPrefWidth() < FIELD_WIDTH) box.setPrefWidth(FIELD_WIDTH); // stupid width set
                box.setEditable(true);
                box.addEventFilter(KeyEvent.KEY_PRESSED, enterKeyAction);
//                box.setOnAction(actionComboTraverse); // not good
//                box.setOnKeyPressed(actionTraverse); // doesnt work
                gridPane.add(box, 1, row);
                dataHolders.add(box); // save a copy for printing later
            } else { // REGNO, DATE
                TextField textField = new TextField();
                textField.setOnKeyPressed(actionTraverse);
                gridPane.add(textField, 1, row);
                dataHolders.add(textField); // save a copy for printing later
            }
//            indicator.setProgress(row * 100 / wrapper.getCertificateFields().size()); // null pointer
            populatingProgress.set(row * 100 / wrapper.getCertificateFields().size());
//            System.out.println("populating progress" + populatingProgress.get());
            row++;
        }
        /* populated */
        
        gridPane.add(nextButton, 1 , row); // add before the last column
        gridPane.add(finishButton, 2, row); // add to the last column
        
        
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
        
        
//        File saveFile;
//        if(savename.contains("/")) {
//            // remove slash and every character after slash because windows doesnt support slashes in filenames
//            saveFile = new File(savePathField.getText() + File.separatorChar + savename.substring(0, savename.lastIndexOf("/")));
//        } else {
//            saveFile = new File(savePathField.getText() + File.separatorChar + savename);
//        }
//        System.out.println("Savename : " + savename + "\nwriting certificate image : " + saveFile.getAbsolutePath());
        
        if(savename.contains("/")) {
            // remove slash and every character after slash because windows doesnt support slashes in filenames
            savename =  savename.substring(0, savename.lastIndexOf("/"));
        }
        
        String savePath = savePathField.getText();

//        saveFile = certificateUtils.correctPngExtension(saveFile); // correct file extension, DONT DO THIS NOW

        try {
            // give order to image writer service
    //        iws.takeWork(certificateImage, fields, saveFile); // no need to specify certificate image
            iws.takeImageWriteOrder(new ImageWriteOrder(fields, savePath, savename));
            
    //        try {
    //            BufferedImage createBufferedImage = ImageUtils.createBufferedImage(certificateImage, fields);
    //            System.out.println("created buffered image...");
    //            ImageUtils.saveImage(createBufferedImage, saveFile.getAbsolutePath());
    //            System.out.println("saved image...");
    //        } catch (FileNotFoundException ex) {
    //            Logger.getLogger(CreateCertificateDialog.class.getName()).log(Level.SEVERE, null, ex);
    //        } catch (IOException ex) {
    //            Logger.getLogger(CreateCertificateDialog.class.getName()).log(Level.SEVERE, null, ex);
    //        }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CreateCertificateDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CreateCertificateDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (OutOfMemoryError ex) {
            Logger.getLogger(CreateCertificateDialog.class.getName()).log(Level.SEVERE, null, ex);
            Alert.showAlertError(primaryStage, "Error", "OutOfMemoryError");
        }
    }

    private EventHandler<? super KeyEvent> getActionTraverse() {
        return new EventHandler<KeyEvent>() { // TODO focus traversal
            @Override
            public void handle(KeyEvent t) {
                if(t.getCode() == KeyCode.ENTER) {
                    System.out.println("KeyCode.ENTER : Textfield traverse");
                    Node n = (Node) t.getSource();
                    n.impl_traverse(Direction.NEXT);
                }
            }
        };
    }
    
    private EventHandler<ActionEvent> getComboActionTraverse() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                Node n = (Node) t.getSource();
                n.impl_traverse(Direction.NEXT);
                System.out.println("ActionEvent on combobox");
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
    
    private void resumeFocus() {
        Node nextFocusNode = null;
        int index = 0;
        for(CertificateField field : wrapper.getCertificateFields()) {
            FieldType type = field.getFieldType();
            if(type == FieldType.TEXT) {
                if(!field.isRepeating()) nextFocusNode = dataHolders.get(index);
            }
            index++;
        }
        if(nextFocusNode != null) nextFocusNode.requestFocus();
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
    
    private Node getSuitableComponent(FieldType fieldType, CertificateField certificateField) {
        if (fieldType == FieldType.IMAGE) {
            TextField textField = new TextField();
            textField.setPrefWidth(FIELD_WIDTH);
            return textField;
        } else if(fieldType == FieldType.COURSE){
            ObservableList<String> list = FXCollections.observableArrayList(certificateField.getCourses());
            ComboBox<String> box = new ComboBox(list);
            if(box.getPrefWidth() < FIELD_WIDTH) box.setPrefWidth(FIELD_WIDTH);
            box.setOnAction(actionComboTraverse);
            return box;
        } else if(certificateField.getFieldType() == FieldType.COURSEDETAILS) {
            ObservableList<String> list = FXCollections.observableArrayList(certificateField.getCoursesDetails());
            ComboBox<String> box = new ComboBox<>(list);
            if(box.getPrefWidth() < FIELD_WIDTH) box.setPrefWidth(FIELD_WIDTH);
            box.setOnAction(actionComboTraverse);
            return box;
        } else if(certificateField.getFieldType() == FieldType.TEXT){
            ComboBox<String> box = new ComboBox<>();
            if(box.getPrefWidth() < FIELD_WIDTH) box.setPrefWidth(FIELD_WIDTH);
            box.setEditable(true);
            box.setOnAction(actionComboTraverse);
            return box;
        } else {
            TextField textField = new TextField();
            textField.setOnKeyPressed(actionTraverse);
            return textField;
        }
    }

    private EventHandler<KeyEvent> getEnterKeyAction() {
        return new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if(t.getCode() == KeyCode.ENTER){
                    System.out.println("KeyCode.ENTER : traversing to Direction.NEXT"); // debug
                    ((Node)t.getSource()).impl_traverse(Direction.NEXT);
                }
            }
        };
    }

    private EventHandler<ActionEvent> getButtonActions() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Button source = (Button) event.getSource();
                if(source.equals(nextButton)) {
                    System.out.println("pressed next.."); // debug
                    if(isFieldsFilled()) {
                        retrieveInfoAndSendForPrinting();
                        clearOrIncrementFields();
                        resumeFocus();
                    }
                } else if(source.equals(finishButton)) {
                    System.out.println("pressed finish.."); // debug
                    if(isFieldsFilled()) {
                        retrieveInfoAndSendForPrinting();
                        close();
                    }    
                }
            }
        };
    }
}
