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

import java.io.File;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author bazi
 */
class NewTemplateDialog extends Stage {
    private File imageFile;
    private FileChooser fileChooser;
    private final TextField imagePathFld;
    private final TextField fileNameFld;
    private static CertificateUtils certificateUtils;

    public NewTemplateDialog(final Stage owner, final Window window) {
        super();
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
//        setOpacity(.90);
        setTitle("Create new template");
        
        certificateUtils = new CertificateUtils();
        
        Group root = new Group();
        fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JPEG & PNG Files", "*.jpg", "*.jpeg", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        final GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        Label mainLabel = new Label("Enter certificate name and certificate image location : ");
        gridPane.add(mainLabel, 0, 0, 2, 1);
        Label fileNameLabel = new Label("Certificate name : ");
        gridPane.add(fileNameLabel, 0, 1);
        Label imagePathLabel = new Label("Certificate image path : ");
        gridPane.add(imagePathLabel, 0, 2);
        // text fields
        fileNameFld = new TextField();
        gridPane.add(fileNameFld, 1, 1);
        imagePathFld = new TextField();
        gridPane.add(imagePathFld, 1, 2);
        Button browseButton = new Button("Browse...");
        browseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                File file = fileChooser.showOpenDialog(Window.PRIMARY_STAGE); // dependency
                File file = fileChooser.showOpenDialog(owner); // dependency
                if (file != null) {
                    imageFile = file;
                    imagePathFld.setText(file.getAbsolutePath());
                }
            }
        });
        gridPane.add(browseButton, 2, 2);
        Button finishButton = new Button("Finish");
        finishButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String fileName = fileNameFld.getText();
                String imagePath = imagePathFld.getText();
                if (!"".equalsIgnoreCase(fileName) && !"".equalsIgnoreCase(imagePath)) {
                    close();
                    final CertificateWrapper certificateWrapper = certificateUtils.createEmptyWrapper(fileName, imagePath);
//                    newFileDialog.createNewTab(certificateWrapper).setFile(new File(fileName)); // create new tab and save path for later saving
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            window.createNewTab(certificateWrapper);
                        }
                    });
                    //                        createNewTab(fileName, imagePath);
                } else {
                    Alert.showAlertError(owner, "Error", "Please give a valid filename and image location");
                    // TODO error message
                    Debugger.log("please give a filename and image location...");
                }
            }
        });
        gridPane.add(finishButton, 2, 3);
        GridPane.setHalignment(finishButton, HPos.RIGHT);
        root.getChildren().add(gridPane);
        Scene scene = new Scene(root, Color.WHITE);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if(t.getCode() == KeyCode.ESCAPE) {
                    close();
                }
            }
        });
        setScene(scene);
        setResizable(false);
        sizeToScene();
    }
    
    public NewTemplateDialog reset() {
        fileNameFld.setText("");
        imagePathFld.setText("");
        return this;
    }
    
}
