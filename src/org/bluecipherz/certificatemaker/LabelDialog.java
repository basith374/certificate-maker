/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author bazi
 */
class LabelDialog extends Stage {
    private final EventHandler<ActionEvent> editOkAction;
    private final EventHandler<ActionEvent> newOkAction;
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
                if (!"".equalsIgnoreCase(textField.getText())) {
                    CertificateField certificateField = generateCertificateField();
                    //                        subjectText = org.bluecipherz.certificatemaker.Window.this.createText(newX, newY, textField.getText());
                    subjectText = window.createText(certificateField); // dependency
                    changeSubjectText(subjectText, certificateField);
                    textHolder.getChildren().add(subjectText);
                    close();
                }
            }
        };
        Group root = new Group();
        Scene scene = new Scene(root, Color.WHITE);
        setScene(scene);
        final GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        asklabel = new Label();
        gridPane.add(asklabel, 0, 0, 2, 1);
        Label textLabel = new Label("Text : ");
        gridPane.add(textLabel, 0, 1);
        textField = new TextField();
        gridPane.add(textField, 1, 1);
        // new
        Label textLabel1 = new Label("Font name : ");
        gridPane.add(textLabel1, 0, 2);
        Label textLabel2 = new Label("Font size : ");
        gridPane.add(textLabel2, 0, 3);
        Label textLabel3 = new Label("Font style : ");
        gridPane.add(textLabel3, 0, 4);
        fontFamilyBox = new ComboBox(window.getFontFamilyList()); // dependency
        gridPane.add(fontFamilyBox, 1, 2);
        fontFamilyBox.getSelectionModel().select(0);
        fontSizeBox = new ComboBox(window.getFontSizeList()); // dependency
        gridPane.add(fontSizeBox, 1, 3);
        fontSizeBox.getSelectionModel().select(11);
        fontStyleBox = new ComboBox(window.getFontStyleList()); // dependency
        gridPane.add(fontStyleBox, 1, 4);
        fontStyleBox.getSelectionModel().select(0);
        button = new Button("OK");
        GridPane.setHalignment(button, HPos.RIGHT);
        gridPane.add(button, 1, 5);
        root.getChildren().add(gridPane);
    }

    private void changeSubjectText(Text subjectText, CertificateField certificateField) {
        subjectText.setText(textField.getText());
        String fontFamily = certificateField.getFontFamily();
        FontWeight fontWeight = (certificateField.isBoldText()) ? FontWeight.BOLD : FontWeight.NORMAL;
        int fontSize = certificateField.getFontSize();
        subjectText.setFont(Font.font(fontFamily, fontWeight, fontSize));
    }

    private CertificateField generateCertificateField() {
        String fontFamily = fontFamilyBox.getSelectionModel().getSelectedItem().toString();
        //            System.out.println(fontFamily); // debug
        int fontSize = Integer.valueOf(fontSizeBox.getSelectionModel().getSelectedItem().toString());
        //            System.out.println(fontSize); // debug
        String fontStyle = fontStyleBox.getSelectionModel().getSelectedItem().toString();
        //            System.out.println(fontStyle); // debug
        return new CertificateField(textField.getText(), newX, newY, fontFamily, fontSize, "BOLD".equalsIgnoreCase(fontStyle));
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
        String fontFamily = window.getDefaultFontFamily(); // dependency
        if (fontFamily != null) {
            fontFamilyBox.getSelectionModel().select(window.getFontFamilyList().indexOf(fontFamily));
        }
        String fontSize = window.getDefaultFontSize(); // dependency
        if (fontSize != null) {
            fontSizeBox.getSelectionModel().select(window.getFontSizeList().indexOf(Integer.valueOf(fontSize)));
        }
        String fontStyle = window.getDefaultFontStyle(); // dependency
        if (fontStyle != null) {
            fontStyleBox.getSelectionModel().select(window.getFontStyleList().indexOf(fontStyle));
        }
    }
    
}
