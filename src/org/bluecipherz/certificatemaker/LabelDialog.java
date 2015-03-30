/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author bazi
 */
class LabelDialog extends Stage {
    private final EventHandler<ActionEvent> editOkAction;
    private final EventHandler<ActionEvent> newOkAction;
    private final EventHandler<ActionEvent> categoryAction;
    private Text subjectText;
    private final TextField textField;
    private Group textHolder;
    private static final String NEW_TEXT = "Enter a name for the newly added field:";
    private static final String EDIT_TEXT = "Edit the name of the selected field";
    private final Label asklabel;
    private final Button button;
    private int newX;
    private int newY;
    private final ComboBox fontFamilyBox;
    private final ComboBox fontSizeBox;
    private final ComboBox fontStyleBox;
    private final Window window;
    
    private static final ObservableList<String> categories = FXCollections.observableArrayList("Text", "Date", "Regno", "Course");
    private GridPane gridPane;
    private ComboBox fieldTypeBox;
    
    FieldType category;
    private Label textLabel;
    private ListView<String> list;
    private Label coursesLabel;
    private Timeline animation;
    private Button addButton;
    private Button removeButton;

    public LabelDialog(Stage owner, final Group textHolder, final Window window) {
        super();
        this.window = window;
        this.textHolder = textHolder;
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        editOkAction = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!"".equalsIgnoreCase(textField.getText())) {
                    CertificateField certificateField = generateCertificateField();
                    changeSubjectText(subjectText, certificateField);
                    //                        subjectText.setText(textField.getText());
                    close();
                }
            }
        };
        newOkAction = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CertificateField certificateField = generateCertificateField();
                System.out.println("Adding " + certificateField.getFieldType().toString()); // debug
                if(certificateField.getFieldType() == FieldType.TEXT) {
                    if (!"".equalsIgnoreCase(textField.getText())) {
                        subjectText = window.createText(certificateField); // dependency
                        changeSubjectText(subjectText, certificateField);
                        textHolder.getChildren().add(subjectText);
                        close();
                    }
                } else if(certificateField.getFieldType() == FieldType.COURSE) {
                    subjectText = window.createText(certificateField);
                    textHolder.getChildren().add(subjectText);
                    certificateField.setCourses(list.getItems());
                    close();
                } else {
                    subjectText = window.createText(certificateField); // dependency
//                    changeSubjectText(subjectText, certificateField);
                    textHolder.getChildren().add(subjectText);
                    System.out.println("DEBUG : " + subjectText.getText() + ", x:" + subjectText.getX() + ", y:" + subjectText.getY() + ", parent:" + subjectText.getParent().toString() + ", font:" + subjectText.getFont().getFamily() + subjectText.getFont().getSize()); // debug
                    close();
                }
            }
        };
        categoryAction = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                ComboBox combobox = (ComboBox) t.getSource();
                int index = combobox.getSelectionModel().getSelectedIndex();
                System.out.println("combobox index : " + index);
                // the following index values are according to FieldType enum. if FieldType values changes, so should here.
                if(index == 0) { // text
                    if(category == FieldType.DATE || category == FieldType.REGNO) {
                        gridPane.add(textLabel, 0, 2, 1, 2); // col, row, colspan, rowspan
                        gridPane.add(textField, 1, 2, 1, 2); // col, row, colspan, rowspan
                    } else {
                        // remove course stuff
                        gridPane.getChildren().remove(coursesLabel);
                        gridPane.getChildren().remove(list);
                        gridPane.getChildren().remove(addButton);
                        gridPane.getChildren().remove(removeButton);
                        // add text stuff
                        gridPane.add(textLabel, 0, 2, 1, 2); // col, row, colspan, rowspan
                        gridPane.add(textField, 1, 2, 1, 2); // col, row, colspan, rowspan
                    }
                    category = FieldType.TEXT;
                    sizeToScene();
                } else if(index == 1) { // date
                    if(category == FieldType.TEXT) {
                        gridPane.getChildren().remove(textLabel);
                        gridPane.getChildren().remove(textField);
                    } else if(category == FieldType.COURSE) {
                        gridPane.getChildren().remove(coursesLabel);
                        gridPane.getChildren().remove(list);
                        gridPane.getChildren().remove(addButton);
                        gridPane.getChildren().remove(removeButton);
                    }
                    category = FieldType.DATE;
                    sizeToScene();
                } else if(index == 2) { // regno
                    if(category == FieldType.TEXT) {
                        gridPane.getChildren().remove(textLabel);
                        gridPane.getChildren().remove(textField);
                    } else if(category == FieldType.COURSE) {
                        gridPane.getChildren().remove(coursesLabel);
                        gridPane.getChildren().remove(list);
                        gridPane.getChildren().remove(addButton);
                        gridPane.getChildren().remove(removeButton);
                    }
                    category = FieldType.REGNO;
                    sizeToScene();
                } else if(index == 3) { // course
                    if(category == FieldType.DATE || category == FieldType.REGNO) {
                        gridPane.add(coursesLabel, 0, 2, 1, 2); // col, row, colspan, rowspan
                        gridPane.add(list, 1, 2, 1, 2); // col, row, colspan, rowspan
                        gridPane.add(addButton, 2, 2);
                        gridPane.add(removeButton, 2, 3);
                    } else {
                        gridPane.getChildren().remove(textLabel);
                        gridPane.getChildren().remove(textField);
                        gridPane.add(coursesLabel, 0, 2, 1, 2); // col, row, colspan, rowspan
                        gridPane.add(list, 1, 2, 1, 2); // col, row, colspan, rowspan
                        gridPane.add(addButton, 2, 2);
                        gridPane.add(removeButton, 2, 3);
                    }
                    category = FieldType.COURSE;
                    sizeToScene();
                }
            }
        };
        Group root = new Group();
        Scene scene = new Scene(root, Color.WHITE);
        setScene(scene);
        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        
        asklabel = new Label();
        gridPane.add(asklabel, 0, 0, 2, 1);
        
        Label categoryLabel = new Label("Category");
        gridPane.add(categoryLabel, 0, 1);
        textLabel = new Label("Text : ");
        coursesLabel = new Label("Courses : ");
        list = new ListView();
//        list.setContextMenu(getListContextMenu());
        list.setEditable(true);
        list.setCellFactory(TextFieldListCell.forListView());
        list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        animation = new Timeline(new KeyFrame(Duration.seconds(.1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                list.edit(list.getSelectionModel().getSelectedIndex());
            }
        }));
        animation.setCycleCount(1);
        list.setPrefHeight(150);
        list.setOnEditCommit(new EventHandler<ListView.EditEvent<String>>() {
            @Override
            public void handle(ListView.EditEvent<String> t) {
                list.getItems().set(t.getIndex(), t.getNewValue());
//                list.getItems().add("");
//                list.getSelectionModel().select(t.getIndex()+1);
//                animation.play();
            }
        });
        addButton = new Button("Add");
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if(list.getEditingIndex() > -1) {
//                    list.fireEvent(new ListView.EditEvent(list, ListView.EditEvent.ANY, t, newX));
                    System.out.println(list.getItems().get(list.getEditingIndex()));
                }
                list.getItems().add("");
                list.getSelectionModel().selectLast();
                animation.play();
            }
        });
        GridPane.setValignment(addButton, VPos.BASELINE);
        removeButton = new Button("Remove");
        removeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                list.getItems().remove(list.getSelectionModel().getSelectedItem());
            }
        });
        gridPane.add(textLabel, 0, 2, 1, 2); // col, row, colspan, rowspan
        Label textLabel1 = new Label("Font name : ");
        gridPane.add(textLabel1, 0, 4);
        Label textLabel2 = new Label("Font size : ");
        gridPane.add(textLabel2, 0, 5);
        Label textLabel3 = new Label("Font style : ");
        gridPane.add(textLabel3, 0, 6);
        fieldTypeBox = new ComboBox(categories);
        fieldTypeBox.getSelectionModel().select(0); // select first
        category = FieldType.TEXT;
        fieldTypeBox.setOnAction(categoryAction);
        gridPane.add(fieldTypeBox, 1, 1);
        textField = new TextField();
        gridPane.add(textField, 1, 2, 1, 2); // col, row, colspan, rowspan
        fontFamilyBox = new ComboBox(window.getFontFamilyList()); // dependency
        gridPane.add(fontFamilyBox, 1, 4);
        fontFamilyBox.getSelectionModel().select(0);
        fontSizeBox = new ComboBox(window.getFontSizeList()); // dependency
        gridPane.add(fontSizeBox, 1, 5);
        fontSizeBox.getSelectionModel().select(5);
        fontStyleBox = new ComboBox(window.getFontStyleList()); // dependency
        gridPane.add(fontStyleBox, 1, 6);
        fontStyleBox.getSelectionModel().select(0);
        
        button = new Button("OK");
        GridPane.setHalignment(button, HPos.RIGHT);
        gridPane.add(button, 1, 7);
        
        root.getChildren().add(gridPane);
    }

    private void changeSubjectText(Text subjectText, CertificateField certificateField) {
        if(certificateField.getFieldType() == FieldType.TEXT) {
            subjectText.setText(textField.getText());
        } else {
            subjectText.setText(certificateField.getFieldType().toString());
        }
        String fontFamily = certificateField.getFontFamily();
        FontWeight fontWeight = (certificateField.getFontStyle() == java.awt.Font.BOLD) ? FontWeight.BOLD : FontWeight.NORMAL;
        int fontSize = certificateField.getFontSize();
        subjectText.setFont(Font.font(fontFamily, fontWeight, fontSize));
    }

    private CertificateField generateCertificateField() {
        FieldType field_type = FieldType.valueOf(fieldTypeBox.getSelectionModel().getSelectedItem().toString().toUpperCase());
        String fontFamily = fontFamilyBox.getSelectionModel().getSelectedItem().toString();
        //            System.out.println(fontFamily); // debug
        int fontSize = Integer.valueOf(fontSizeBox.getSelectionModel().getSelectedItem().toString());
        //            System.out.println(fontSize); // debug
        String fontStyle = fontStyleBox.getSelectionModel().getSelectedItem().toString();
        //            System.out.println(fontStyle); // debug
        CertificateField certificateField;
        if(field_type == FieldType.TEXT) {
            certificateField = new CertificateField(newX, newY, textField.getText(), fontSize, fontFamily, "Bold".equalsIgnoreCase(fontStyle)?java.awt.Font.BOLD:java.awt.Font.PLAIN);
        } else if(field_type == FieldType.COURSE) {
            certificateField = new CertificateField(newX, newY, field_type.toString(), fontSize, fontFamily, "Bold".equalsIgnoreCase(fontStyle)?java.awt.Font.BOLD:java.awt.Font.PLAIN);
            certificateField.setCourses(list.getItems());
        } else {
            certificateField = new CertificateField(newX, newY, field_type.toString(), fontSize, fontFamily, "Bold".equalsIgnoreCase(fontStyle)?java.awt.Font.BOLD:java.awt.Font.PLAIN); // new fix!!! fix of the year!
        }        
        certificateField.setFieldType(field_type);
        return certificateField;
    }

    public void prepareAndShowNewTextDialog(double x, double y) {
        setTitle("New entry");
        asklabel.setText(NEW_TEXT);
        newX = (int) x;
        newY = (int) y;
        textField.setText("");
        setDefaultFieldValues();
        button.setOnAction(newOkAction);
        sizeToScene();
        show();
    }

    public void prepareAndShowEditTextDialog(Text text) {
        setTitle("Edit entry");
        asklabel.setText(EDIT_TEXT);
        subjectText = text;
        textField.setText(subjectText.getText());
        setDefaultFieldValues();
        button.setOnAction(editOkAction);
        sizeToScene();
        show();
    }

    public void setDefaultFieldValues() {
        String fontFamily = UserDataManager.getDefaultFontFamily();
        if (fontFamily != null) {
            fontFamilyBox.getSelectionModel().select(window.getFontFamilyList().indexOf(fontFamily));
        }
        String fontSize = UserDataManager.getDefaultFontSize();
        if (fontSize != null) {
            fontSizeBox.getSelectionModel().select(window.getFontSizeList().indexOf(Integer.valueOf(fontSize)));
        }
        String fontStyle = UserDataManager.getDefaultFontStyle();
        if (fontStyle != null) {
            fontStyleBox.getSelectionModel().select(window.getFontStyleList().indexOf(fontStyle));
        }
    }

    private void showButtons() {
        
    }
    
    private void hideButtons() {
        
    }
    
    private ContextMenu getListContextMenu() {
        ContextMenu menu = new ContextMenu();
        
        MenuItem addMenu = new MenuItem("Add");
        menu.getItems().add(addMenu);
        
        MenuItem removeMenu = new MenuItem("Remove");
        menu.getItems().add(removeMenu);
        
        addMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                list.getItems().add("");
                list.getSelectionModel().selectLast();
                animation.play();
            }
        });
        removeMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                list.getItems().remove(list.getSelectionModel().getSelectedItem());
            }
        });
        
        return menu;
    }
    
}
