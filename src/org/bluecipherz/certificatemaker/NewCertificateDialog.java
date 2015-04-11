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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.Mnemonic;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author bazi
 */
public class NewCertificateDialog extends Stage {
    private final Window window;
    private final FileChooser fileChooser;
    private final EventHandler<ActionEvent> action;

    public NewCertificateDialog(final Stage primaryStage, final Window window) {
        super();
        this.window = window;
        initOwner(primaryStage);
        initModality(Modality.APPLICATION_MODAL);
        
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        setTitle("New Certificate");
        
        Label label = new Label("Specify template : ");
        
        final RadioButton radioButton1 = new RadioButton();
        final RadioButton radioButton2 = new RadioButton();
        final ToggleGroup radiogroup = new ToggleGroup();
        radiogroup.getToggles().add(radioButton1);
        radiogroup.getToggles().add(radioButton2);
        radiogroup.selectToggle(radioButton1);
        Label label2 = new Label("Create from current template");
        Label label3 = new Label("Open template");
        final TextField pathField = new TextField();
        pathField.setEditable(false);
        pathField.setMinWidth(200);
        final Button browseButton = new Button("_Browse...");
        browseButton.setDisable(true);
        final Button cancelButton = new Button("_Cancel");
        Button okButton = new Button("  _OK  ");
        
//        browseButton.getScene().addMnemonic(new Mnemonic(browseButton, new KeyCodeCombination(KeyCode.B, KeyCombination.ALT_DOWN)));
//        cancelButton.getScene().addMnemonic(new Mnemonic(cancelButton, new KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN)));
//        okButton.getScene().addMnemonic(new Mnemonic(okButton, new KeyCodeCombination(KeyCode.O, KeyCombination.ALT_DOWN)));
        
        gridPane.add(label, 0, 0, 1, 3); // col, row, colspan, rowspan
        GridPane.setValignment(label, VPos.TOP);
        gridPane.add(radioButton1, 1, 0);
        gridPane.add(label2, 2, 0);
        gridPane.add(radioButton2, 1, 1);
        gridPane.add(label3, 2, 1);
        gridPane.add(pathField, 2, 2);
        gridPane.add(browseButton, 4, 2);
        gridPane.add(cancelButton, 3, 3);
        gridPane.add(okButton, 4, 3);
        
        CertificateUtils cu = new CertificateUtils();
        fileChooser = cu.getXMLFileChooser();
        fileChooser.setTitle("Open template for certificate");
        
        action = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                Button source = (Button) t.getSource();
                if(source.equals(browseButton)) {
                    File template = fileChooser.showOpenDialog(primaryStage);
                    if(template != null) {
                        pathField.setText(template.getAbsolutePath());
                    }
                } else if(source.equals(cancelButton)) {
                    close();
                } else {
                    if(radiogroup.getSelectedToggle().equals(radioButton1)) {
                        window.openCreateCertificateDialog();
                        close();
                    } else {
                        File file = new File(pathField.getText());
                        if(file != null) {
                            window.selectTab(file); // this method also opens the file if not opened
                            close();
                        } else {
                            Alert.showAlertError(primaryStage, "Error", "Please give a valid template path");
                        }
                    }
                }
            }
        };
        
        browseButton.setOnAction(action);
        cancelButton.setOnAction(action);
        okButton.setOnAction(action);
        radiogroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle oldToggle, Toggle newToggle) {
//                System.out.println("radio selection changed"); // debug
                RadioButton selected = (RadioButton) newToggle;
                if(selected.equals(radioButton1)) {
//                    System.out.println("selected 1");
//                    pathField.setEditable(false); // not right
                    pathField.setDisable(true);
                    browseButton.setDisable(true);
                } else {
//                    System.out.println("selected 2");
//                    pathField.setEditable(true); // not right
                    pathField.setDisable(false);
                    browseButton.setDisable(false);
                }
            }
        });
        
        Scene scene = new Scene(gridPane, Color.WHITE);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if(t.getCode() == KeyCode.ESCAPE) {
                    close();
                }
            }
        });
        setScene(scene);
        sizeToScene();
    }
    
}
