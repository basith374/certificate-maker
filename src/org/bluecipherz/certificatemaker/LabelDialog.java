/*
 * Copyright (c) 2012-2015 BCZ Inc.
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

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
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
    
    private CertificateText subjectText;
    private final TextField textField;
    private CertificateTab tab;
    
    private static final String NEW_TEXT = "Enter a name for the newly added field:";
    private static final String EDIT_TEXT = "Edit the attributes of the selected field";
    private final Label asklabel;
    private final Button button;
    private int newX;
    private int newY;
    private final ComboBox<String> fontFamilyBox;
    private final ComboBox<Integer> fontSizeBox;
    private final ComboBox<String> fontStyleBox;
    private final Window window;
    
    private static final ObservableList<FieldType> fieldTypes = FXCollections.observableArrayList(
                FieldType.TEXT,
                FieldType.DATE,
                FieldType.REGNO,
                FieldType.ARRAY
            );
    private GridPane gridPane;
    private ComboBox fieldTypeBox;
    
    FieldType prevCategory;
    private Label textLabel;
    private ListView<String> listView;
    private Label coursesLabel;
    private Timeline animation;
    private Button addButton;
    private Button removeButton;
    private CertificateUtils certificateUtils;
    private String previousText;
    
    private EventHandler<ActionEvent> listViewEventHandler = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
            Button b = (Button) t.getSource();
            if(b.equals(addButton)) {
                listView.getItems().add("");
                listView.getSelectionModel().selectLast();
                animation.play();
            } else if(b.equals(removeButton)) {
                listView.getItems().remove(listView.getSelectionModel().getSelectedItem());
            }
        }
    };
    private CheckBox repeatCheckBox;
    private Label repeatingLabel;

    private boolean disallowmultiplefields =  !UserDataManager.isMultipleFieldsAllowed();
    
    EventHandler<KeyEvent> escaction = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent t) {
            if(t.getCode() == KeyCode.ESCAPE) {
                Debugger.log("[LabelDialog] ESC pressed"); // debug
                close();
            }
        }
    };
    
    public LabelDialog(final Stage owner, final Window window) {
        super();
        this.window = window;
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        certificateUtils = new CertificateUtils();
        
        editOkAction = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int x = (int) subjectText.getX();
                int y = (int) subjectText.getY();
                CertificateField field = generateCertificateField(x, y); // generate edited text
                EditCommand command = new EditCommand(subjectText, field);
                CertificateTab tab = subjectText.getContainer();
                if(field.getFieldType() == FieldType.TEXT || field.getFieldType() == FieldType.ARRAY) {
                    if(!"".equalsIgnoreCase(textField.getText())) {
                        tab.getCommandManager().add(command);
                        close();
                    } else Alert.showAlertError(owner, "ERROR", "Field name must not be empty");
                } else {
                    tab.getCommandManager().add(command);
                    close();
                }
            }
        };
        newOkAction = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CertificateField field = generateCertificateField(newX, newY);
                Debugger.log("[LabelDialog] generated field" + field); // debug
                subjectText = tab.createText(field); // generate new text
                AddCommand command = new AddCommand(subjectText, tab);
//                Debugger.log("Adding " + field.getFieldType().getName() + ", contents : " + (group.getChildren().size() - 1)); // debug
                if(field.getFieldType() == FieldType.TEXT || field.getFieldType() == FieldType.ARRAY) {
                    if(!"".equalsIgnoreCase(subjectText.getText())) {
                        tab.getCommandManager().add(command);
                        close();
                    } else Alert.showAlertError(owner, "ERROR", "Field name must not be empty");
                } else {
                    boolean entryisvalid = true;
                    if(field.getFieldType() == FieldType.DATE) if(tab.isDateFieldAdded()) entryisvalid = false;
                    if(field.getFieldType() == FieldType.REGNO) if(tab.isRegnoFieldAdded()) entryisvalid = false;
                    if(entryisvalid) {
                        tab.getCommandManager().add(command);
                        close();
                    } else Alert.showAlertError(owner, "Error", field.getFieldType().toString() + " already added");
                }
            }
        };
        
        /*
         * HUGE SPAGETTI CODE! HANDLE WITH CARE!, UPDATE: NOW COMPRESSED AND DYNAMIC :)
         */
        categoryAction = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                ComboBox combobox = (ComboBox) t.getSource();
                String typestring = combobox.getSelectionModel().getSelectedItem().toString().toUpperCase();
                FieldType type = FieldType.valueOf(typestring.replaceAll("\\s+", "")); // removes whitespaces
                // the following index values are according to FieldType enum. if FieldType values changes, so should here.
                switch(type) {
                    case TEXT:
                        addTextItems();
                        break;
                    case DATE:
                        addMinimalItems();
                        prevCategory = FieldType.DATE; // the above method wont do this
                        break;
                    case REGNO:
                        addMinimalItems();
                        prevCategory = FieldType.REGNO; // the above method wont do this
                        break;
                    case ARRAY:
                        addArrayItems();
                        break;
                    default:
                }
                sizeToScene();
            }
        };
        Group root = new Group();
        Scene scene = new Scene(root, Color.WHITE);
        scene.setOnKeyPressed(escaction); // close window on ESC press
        setScene(scene);
        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        
        asklabel = new Label();
        
        Label fieldTypeLabel = new Label("Category");
        textLabel = new Label("Text : ");
        coursesLabel = new Label("Values : ");
        listView = createNewListView();
        addButton = new Button("Add");
        addButton.setOnAction(listViewEventHandler);
        GridPane.setValignment(addButton, VPos.BOTTOM);
        removeButton = new Button("Remove");
        removeButton.setOnAction(listViewEventHandler);
        
        animation = new Timeline(new KeyFrame(Duration.seconds(.1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                listView.edit(listView.getSelectionModel().getSelectedIndex());
            }
        }));
        animation.setCycleCount(1);
        
        Label fontFamilyLabel = new Label("Font family : ");
        Label fontSizeLabel = new Label("Font size : ");
        Label fontStyleLabel = new Label("Font style : ");
        
        fieldTypeBox = new ComboBox(fieldTypes);
        fieldTypeBox.getSelectionModel().select(0); // select first
        prevCategory = FieldType.TEXT;
        fieldTypeBox.setOnAction(categoryAction);
        textField = new TextField();
        repeatingLabel = new Label("Repeating");
        repeatCheckBox = new CheckBox();
        fontFamilyBox = new ComboBox(window.getFontFamilyList());
        fontFamilyBox.getSelectionModel().select(0);
//        fontFamilyBox.setOnKeyPressed(escaction); // esc key close fix
        fontSizeBox = new ComboBox(window.getFontSizeList());
        fontSizeBox.getSelectionModel().select(5);
//        fontSizeBox.setOnKeyPressed(escaction); // esc key close fix
        fontStyleBox = new ComboBox(window.getFontStyleList());
        fontStyleBox.getSelectionModel().select(0);
//        fontStyleBox.setOnKeyPressed(escaction); // esc key close fix
        
        // params : col, row, colspan, rowspan
        gridPane.add(asklabel, 0, 0, 2, 1); // 1 row
        gridPane.add(fieldTypeLabel, 0, 1); // 2 row
        gridPane.add(fieldTypeBox, 1, 1); // 2 row
        gridPane.add(new Separator(Orientation.HORIZONTAL), 0, 2, 4, 1); // 3 row
        gridPane.add(textLabel, 0, 3, 1, 3); // 4 row
        gridPane.add(textField, 1, 3, 1, 3); // 4 row
        gridPane.add(repeatingLabel, 2, 3, 1, 3); // 4 row
        gridPane.add(repeatCheckBox, 3, 3, 1, 3); // 4 row
        gridPane.add(fontFamilyLabel, 0, 6); // 5 row
        gridPane.add(fontFamilyBox, 1, 6); // 5 row
        gridPane.add(fontSizeLabel, 0, 7); // 6 row
        gridPane.add(fontSizeBox, 1, 7); // 6 row
        gridPane.add(fontStyleLabel, 0, 8); // 7 row
        gridPane.add(fontStyleBox, 1, 8); // 7 row
        
        button = new Button("OK");
        GridPane.setHalignment(button, HPos.RIGHT);
        gridPane.add(button, 3, 9);
        
        root.getChildren().add(gridPane);
    }
    
    
    private void addMinimalItems() {
//        if(prevCategory == FieldType.TEXT) removeTextItems();
//        if(prevCategory == FieldType.ARRAY) removeArrayItems();
        removeCommonExtras();
    }
    
    private void addArrayItems() {
        if(prevCategory == FieldType.TEXT) removeTextItems();
        if(prevCategory != FieldType.ARRAY) {
            gridPane.add(textLabel, 0, 3, 1, 1); // col, row, colspan, rowspan
            gridPane.add(textField, 1, 3, 1, 1); // col, row, colspan, rowspan
            gridPane.add(coursesLabel, 0, 4, 1, 2); // col, row, colspan, rowspan
            gridPane.add(listView, 1, 4, 1, 2); // col, row, colspan, rowspan
            gridPane.add(addButton, 2, 4);
            gridPane.add(removeButton, 2, 5);
        }
        prevCategory = FieldType.ARRAY;
    }
    
    private void addTextItems() {
        if(prevCategory == FieldType.ARRAY) removeArrayItems();
        if(prevCategory != FieldType.TEXT) {
            gridPane.add(textLabel, 0, 3, 1, 3); // col, row, colspan, rowspan
            gridPane.add(textField, 1, 3, 1, 3); // col, row, colspan, rowspan
            gridPane.add(repeatingLabel, 2, 3, 1, 3);
            gridPane.add(repeatCheckBox, 3, 3, 1, 3);
        }
        prevCategory = FieldType.TEXT;
    }
    
    private void removeArrayItems() {
        gridPane.getChildren().remove(coursesLabel);
        gridPane.getChildren().remove(listView);
        gridPane.getChildren().remove(addButton);
        gridPane.getChildren().remove(removeButton);
        gridPane.getChildren().remove(textLabel);
        gridPane.getChildren().remove(textField);
    }
    
    
    private void removeTextItems() {
        gridPane.getChildren().remove(textLabel);
        gridPane.getChildren().remove(textField);
        gridPane.getChildren().remove(repeatingLabel);
        gridPane.getChildren().remove(repeatCheckBox);
    }
    
    private void removeCommonExtras() {
        gridPane.getChildren().remove(textLabel);
        gridPane.getChildren().remove(textField);
        gridPane.getChildren().remove(repeatingLabel);
        gridPane.getChildren().remove(repeatCheckBox);
        gridPane.getChildren().remove(coursesLabel);
        gridPane.getChildren().remove(listView);
        gridPane.getChildren().remove(addButton);
        gridPane.getChildren().remove(removeButton);
    }
    
    /**
     * FRONT LINE CERTIFICATE FIELD GENERATION METHOD, has made converttocertificatefield() method
     * in certificateutils outdated
     * Retrieves data from LabelDialog gui components and generates a new corresponding CertificateField 
     * object and returns it.
     * @return 
     */
    private CertificateField generateCertificateField(int x, int y) {
        // datas : x, y, fieldtype, fontfamily, fontsize, fontstyle, other conditions
        String typestring = fieldTypeBox.getSelectionModel().getSelectedItem().toString().toUpperCase();
        FieldType field_type = FieldType.valueOf(typestring.replaceAll("\\s+", "")); // same thing at another place in this file
        String fontFamily = fontFamilyBox.getSelectionModel().getSelectedItem().toString();
        Integer fontSize = Integer.valueOf(fontSizeBox.getSelectionModel().getSelectedItem().toString());
        String fontStyle = fontStyleBox.getSelectionModel().getSelectedItem().toString();
        
        if(field_type == FieldType.TEXT) {
            return new CertificateField(x, y, FieldType.TEXT, fontFamily, fontSize, fontStyle, textField.getText(), repeatCheckBox.isSelected());
        } else if(field_type == FieldType.ARRAY) { // ARRAY
            ObservableList<String> array = FXCollections.observableArrayList(listView.getItems()); // bug fix
            return new CertificateField(x, y, FieldType.ARRAY, fontFamily, fontSize, fontStyle, textField.getText(), array);
        } else {
            return new CertificateField(x, y, field_type, fontFamily, fontSize, fontStyle);
        }
    }

    public void prepareAndShowNewTextDialog(CertificateTab tab, Point2D point) {
        this.tab = tab;
        
        setTitle("New entry");
        asklabel.setText(NEW_TEXT);
        newX = (int) point.getX();
        newY = (int) point.getY();
        textField.setText("");
        setDefaultFieldValues();
        fieldTypeBox.setDisable(false);
        button.setOnAction(newOkAction);
        sizeToScene();
        setResizable(false);
        show();
    }

    public void prepareAndShowEditTextDialog(CertificateTab tab, CertificateText text) {
        this.tab = tab;
        
        setTitle("Edit field");
        asklabel.setText(EDIT_TEXT);
        subjectText = text;
        FieldType TYPE = subjectText.fieldTypeProperty().get();
        
        fieldTypeBox.getSelectionModel().select(TYPE);
        
        fontFamilyBox.getSelectionModel().select(subjectText.getFontFamily()); // new 
//        Debugger.log("" + (int) subjectText.getFont().getSize()); // debug
        fontSizeBox.getSelectionModel().select(subjectText.getFontSize()); // avoid decimal, cast to int
        fontStyleBox.getSelectionModel().select(subjectText.getFontStyle());
        
        if(TYPE == FieldType.TEXT) {
            textField.setText(subjectText.getText());
            repeatCheckBox.setSelected(subjectText.repeatingProperty().get());
        } else if(TYPE == FieldType.ARRAY) {
            textField.setText(subjectText.getText());
            listView.getItems().setAll(subjectText.arrayProperty());
        }
        
        fieldTypeBox.setDisable(true);
        button.setOnAction(editOkAction);
        sizeToScene();
        setResizable(false);
        show();
    }

    public void setDefaultFieldValues() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                fieldTypeBox.getSelectionModel().select(0);
            }
        });
        repeatCheckBox.setSelected(false); // dont know if this is necessary. just to make sure
        String fontFamily = UserDataManager.getDefaultFontFamily();
        if (fontFamily != null) fontFamilyBox.getSelectionModel().select(window.getFontFamilyList().indexOf(fontFamily));
        String fontSize = UserDataManager.getDefaultFontSize();
        if (fontSize != null) fontSizeBox.getSelectionModel().select(window.getFontSizeList().indexOf(Integer.valueOf(fontSize)));
        String fontStyle = UserDataManager.getDefaultFontStyle();
        if (fontStyle != null) fontStyleBox.getSelectionModel().select(window.getFontStyleList().indexOf(fontStyle));
        listView.getItems().clear();
    }

    private ListView createNewListView() {
        ListView<String> list = new ListView();
        list.setEditable(true);
        list.setCellFactory(TextFieldListCell.forListView());
        list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        list.setPrefHeight(150);
        return list;
    }
        
}
