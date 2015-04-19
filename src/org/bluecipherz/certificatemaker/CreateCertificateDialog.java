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

import com.sun.javafx.scene.traversal.Direction;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
        ImageUtils.setCu(certificateUtils);
        
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
        dataHolders.addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Node> change) {
                Debugger.log("Data holders changes : " + change.getList().size() + "list :" + change.getList()); // debug
            }
        });

        buttonAction = getButtonActions();
        nextButton = new Button("Next");
        GridPane.setHalignment(nextButton, HPos.RIGHT);
        nextButton.setOnAction(buttonAction);
        finishButton = new Button("Finish");
        GridPane.setHalignment(finishButton, HPos.RIGHT);
        finishButton.setOnAction(buttonAction);
        
        finishButton.setMaxWidth(Double.MAX_VALUE);
    }

    public void openFor(final CertificateWrapper newWrapper) {
        Debugger.log("[CreateCertificateDialog]Received Wrapper : " + newWrapper);
        dataHolders.clear(); // bug fix
        iws.setDefaultExtension(UserDataManager.getDefaultImageFormat()); // update changes
        iws.setA3Output(UserDataManager.isA3Output()); // update changes
        if(newWrapper.getCertificateFields().size() > 0) {
            // check if regno is specified
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    boolean noregno = true;
                    for(CertificateField field : newWrapper.getCertificateFields()) {
                        if(field.getFieldType() == FieldType.REGNO) noregno = false;
                    }
                    if(noregno) Alert.showAlertWarning(primaryStage, "Warning", "There is no regno field specified, you may not receive an output");
                }
            });
            // the real thing
            this.wrapper = newWrapper;
            gridPane = createEntryFieldsandLabels(wrapper); // LOAD ALL STUFF
            iws.setCertificateImage(new Image(this.wrapper.getCertificateImage().toURI().toString()));
            scene.setRoot(gridPane);
            setScene(scene);
            sizeToScene();
            show();
//            populatingProgress.addListener(new ChangeListener<Number>() {
//                @Override
//                public void changed(ObservableValue<? extends Number> ov, Number t, Number loaded) {
//                    Debugger.log("Populating " + loaded.doubleValue() * 100 + "%");
//                }
//            });
        } else {
            Alert.showAlertError(primaryStage, "Error", "There are no fields in this template");
        }
    }
    
    private GridPane createEntryFieldsandLabels(final CertificateWrapper wrapper) {
        final GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        
        Label savePathLabel = new Label("Save path : ");
        savePathField = new TextField();
        savePathField.setOnKeyPressed(actionTraverse);
        savePathField.setPrefWidth(FIELD_WIDTH);
        Button savePathBtn = new Button("Browse...");
        savePathBtn.setOnKeyPressed(actionTraverse);
        
        gridPane.add(savePathLabel, 0, 0); // col, rows
        gridPane.add(savePathField, 1, 0);
        gridPane.add(savePathBtn, 2, 0);
        gridPane.add(new Separator(Orientation.HORIZONTAL), 0, 1, 3, 1);
        
        savePathBtn.setOnAction(getBrowseAction());
        
        int row = 2;
        /* start populating gui components according to wrapper*/
        Debugger.log("Populating fields");
        Collections.sort(wrapper.getCertificateFields()); // new sorting implementation
        Debugger.log("Sorting box components");
        for (CertificateField certificateField : wrapper.getCertificateFields()) {
            Label label = getSuitableLabel(certificateField);
            gridPane.add(label, 0, row);

            Node node = getSuitableComponent(certificateField);
            // add browse button for image
            if(certificateField.getFieldType() == FieldType.IMAGE) {
                TextField tf = (TextField) node;
                Button browseButton = getBrowseButton(tf);
                browseButton.setOnKeyPressed(actionTraverse);
                gridPane.add(browseButton, 2, row);
            }
            gridPane.add(node, 1, row);
            dataHolders.add(node);

//            indicator.setProgress(row * 100 / wrapper.getCertificateFields().size()); // null pointer
            populatingProgress.set(row * 100 / wrapper.getCertificateFields().size());
//            Debugger.log("populating progress" + populatingProgress.get());
            row++;
        }
        /* populated */
        gridPane.add(new Separator(Orientation.HORIZONTAL), 0, row, 3, 1);
        row++;
        gridPane.add(nextButton, 1 , row); // add before the last column
        gridPane.add(finishButton, 2, row); // add to the last column        
        
        return gridPane;
    }
    
    

    private Button getBrowseButton(final TextField text) {
        Button button = new Button("Browse...");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                Debugger.log("[CreateCertificateDialog] Browse button clicked..."); // debug
                fileChooser.setTitle("Open photo");
                File start = UserDataManager.getAvatarImagePath();
                if(start != null) {
//                    if(!start.getAbsolutePath().endsWith("/")) start = new File(start.getAbsolutePath() + "/"); // fix
                    if(start.isDirectory()) {
                        Debugger.log("[CreateCertificateDialog] filechooser : setting last avatar path - " + start.getAbsolutePath()); // debug
                        fileChooser.setInitialDirectory(start);
                    }
                }
                File file = fileChooser.showOpenDialog(primaryStage);
                Debugger.log("[CreateCertificateDialog] filechooser : file selected"); // debug
                if(file != null) {
                    UserDataManager.setAvatarImagePath(file);
                    text.setText(file.getAbsolutePath());
                }
            }
        });
        return button;
    }
    
    private void retrieveInfoAndSendForPrinting() {
        // Debugger.log("Populating certificate fields :"); // debug
        /*
         * CREATE A HASHMAP AND PUT THE VALUES INTO IT
         */
        HashMap<CertificateField, String> fields = new HashMap<>(); // certificate field and user input
        String savename = "";
        int index = 0;
        for (CertificateField field : wrapper.getCertificateFields()) {
            Debugger.log("[CreateCertificateDialog]Retrieving gui components values : " + index + " " + field.getFieldType()); // debug
            if (field.getFieldType() == FieldType.TEXT) {
                if(field.isRepeating()) {
                    TextField tf = (TextField) dataHolders.get(index);
                    fields.put(field, tf.getText());
                } else {
                    ComboBox<String> cb = (ComboBox) dataHolders.get(index);
                    fields.put(field, cb.getSelectionModel().getSelectedItem());
                }
            } else if(field.getFieldType() == FieldType.ARRAY) {
                ComboBox<String> cb = (ComboBox) dataHolders.get(index);
                fields.put(field, cb.getSelectionModel().getSelectedItem());
            } else { // REGNO, DATE, IMAGE
                TextField tf = (TextField)dataHolders.get(index);
                fields.put(field, tf.getText());
                if(field.getFieldType() == FieldType.REGNO) savename = tf.getText();
//                if(field.getFieldType() == FieldType.IMAGE) Debugger.log("retrieving avatar : " + tf.getText());
            }
            index++;
        }
        
        /*
         * WINDOWS FILESYSTEM DOES NOT SUPPORT SPECIAL CHARACTERS IN FILENAME.
         * REGNO field is known to contain a slash("/") character. we need to remove this character.
         */
        if(savename.contains("/")) {
            // remove slash and every character after slash because windows doesnt support slashes in filenames
            savename =  savename.substring(0, savename.lastIndexOf("/"));
        }
        
        String savePath = savePathField.getText();

//        saveFile = certificateUtils.correctPngExtension(saveFile); // correct file extension, DONT DO THIS NOW, done at a lower level

        /*
         * PASS ON THE ORDER TO THE IMAGE WRITER SERVICE(IWS)
         */
        try {
            iws.takeImageWriteOrder(new ImageWriteOrder(fields, savePath, savename));
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
                    Debugger.log("KeyCode.ENTER : Textfield traverse");
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
                Debugger.log("ActionEvent on combobox");
            }
        };
    }

    
    private boolean isFieldsFilled() {
        boolean filled = true;
        Debugger.log("[CreateCertificateDialog] Data holder size : " + dataHolders.size()); // debug
        for(Node node : dataHolders) {
            if(node instanceof TextField) {
                TextField tf = (TextField) node;
                if("".equalsIgnoreCase(tf.getText())) {
                    filled = false;
                    Debugger.log("textfield not filled : " + tf + "," + tf.getText()) ;
                }
            } else if(node instanceof ComboBox) {
                ComboBox<String> box = (ComboBox<String>) node;
                if("".equalsIgnoreCase(box.getSelectionModel().getSelectedItem())) {
                    filled = false;
                    Debugger.log("combobox not filled : " + box + "," + box.getSelectionModel().getSelectedItem());
                }
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
//                    if(!field.isRepeating()) {
                        if(!box.getItems().contains("")) box.getItems().add(0, "");
                        box.getSelectionModel().select(0);// reset value
//                    } 
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
    
    private Label getSuitableLabel(CertificateField certificateField) {
        Label label = new Label();
        if(certificateField.getFieldType() == FieldType.TEXT || certificateField.getFieldType() == FieldType.ARRAY) {
            label.setText(certificateField.getFieldName() + " :");
//            label = new Label(certificateField.getFieldName() + " : ");
//            Debugger.log("Adding " + certificateField.getFieldType() + " : " + certificateField.getFieldName());
        } else {
            label.setText(certificateField.getFieldType().getName() + " :");
//            label = new Label(certificateField.getFieldType().getName() + " : "); // new enum implementation
//                label = new Label(certificateField.getFieldType().toString() + " : ");
//            Debugger.log("Adding " + certificateField.getFieldType().toString());
        }
        return label;
    }
    
    private Node getSuitableComponent(CertificateField certificateField) {
        if (certificateField.getFieldType() == FieldType.IMAGE) {
            TextField avatarPathField = new TextField();
            avatarPathField.setOnKeyPressed(actionTraverse);
//            Debugger.log("Adding avatar path field for image at : " + certificateField.getX() + "," + certificateField.getY()); // debug
            return avatarPathField;
        } else if(certificateField.getFieldType() == FieldType.ARRAY) {
            Debugger.log("[CreateCertificateDialog] Loading array : " + certificateField.getArray().size()); // debug
            ObservableList<String> list = FXCollections.observableArrayList(certificateField.getArray());
            ComboBox<String> box = new ComboBox(list);
            box.setMaxWidth(Double.MAX_VALUE);
            box.addEventFilter(KeyEvent.KEY_PRESSED, enterKeyAction);
            box.getSelectionModel().select(0);
            return box;
        } else if(certificateField.getFieldType() == FieldType.TEXT) {
            Debugger.log("[CreateCertificateDialog] Text Repeating : " + certificateField.isRepeating()); // debug
            if(!certificateField.isRepeating()) {
                ComboBox<String> box = new ComboBox<>();
                box.setMaxWidth(Double.MAX_VALUE);
                box.setEditable(true);
                box.addEventFilter(KeyEvent.KEY_PRESSED, enterKeyAction);
                return box;
            } else { // repeating text
                TextField text = new TextField();
                text.setOnKeyPressed(actionTraverse);
                return text;
            }
        } else { // REGNO, DATE
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
                    Debugger.log("KeyCode.Enter : traversing to Direction.NEXT"); // next
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
                    if(isFieldsFilled()) {
                        Debugger.log("[CreateCerificateDialog] pressed next...");
                        TextField tf = (TextField) getAvatarPathField(); // debug
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                retrieveInfoAndSendForPrinting();
                                clearOrIncrementFields();
                                resumeFocus();
                            }
                        });
                    } else {
                        Alert.showAlertError(primaryStage, "Error", "Please fill in all the fields");
                    }
                } else if(source.equals(finishButton)) {
                    if(isFieldsFilled()) {
                        Debugger.log("[CreateCertificateDialog] pressed finish...");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                retrieveInfoAndSendForPrinting();
                            }
                        });
                        close();
                    } else {
                        Alert.showAlertError(primaryStage, "Error", "Please fill in all the fields");
                    }
                }
            }
        };
    }
    
    public Node getAvatarPathField() {
        List<CertificateField> fields = wrapper.getCertificateFields();
        int index = 0;
        for(Node node : dataHolders) {
            if(fields.get(index).getFieldType() == FieldType.IMAGE) return node;
            index++;
        }
        return null;
    }
    
    public ReadOnlyDoubleProperty populatingProgressProperty() {
        return populatingProgress.getReadOnlyProperty();
    }
}
