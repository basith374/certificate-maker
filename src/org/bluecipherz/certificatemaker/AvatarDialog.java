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

import com.sun.javafx.scene.traversal.Direction;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author HaCkErZzZ
 */
public class AvatarDialog extends Stage {

    private Stage primaryStage;
    private Window window;
    private CertificateTab tab;
    
    private int imageX;
    private int imageY;
    
    private static CertificateUtils certificateUtils;
    
    private final TextField heightField;
    private final TextField widthField;
    
    private Button okButton;
    
    private CertificateAvatar subjectImage;
    
    EventHandler<ActionEvent> newaction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
            if(!"".equals(widthField.getText()) || !"".equals(heightField.getText())) {
                try {
                    int width = Integer.parseInt(widthField.getText());
                    int height = Integer.parseInt(heightField.getText());
                    if(width > 0 && height > 0) {
                        CertificateField field = generateCertificateField(imageX, imageY, width, height);
                        subjectImage = tab.createAvatarImage(field);
                        AddCommand command = new AddCommand(subjectImage, tab);
                        tab.getCommandManager().add(command);
                        close();
                    } // else give positive number
                } catch(NumberFormatException ex) {
                    Alert.showAlertError(primaryStage, "Error", "Please enter numbers, not alphabets");
                }
            } else {
                Alert.showAlertError(primaryStage, "Error", "Please fill in the fields");
            }
        }
    };
    
    EventHandler<ActionEvent> editaction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
            if(!"".equals(widthField.getText()) || !"".equals(heightField.getText())) {
                Integer width = Integer.parseInt(widthField.getText());
                Integer height = Integer.parseInt(heightField.getText());
                if(width > 0 && height > 0) {
                    CertificateField changes = new CertificateField(imageX, imageY, FieldType.IMAGE, width, height);
                    EditCommand command = new EditCommand(subjectImage, changes);
                    tab.getCommandManager().add(command);
                    close();
                } // else give positive number
            } else {
                Alert.showAlertError(primaryStage, "Error", "Please fill in the fields");
            }
        }
    };
    
    EventHandler<KeyEvent> traverse = new EventHandler<KeyEvent>() {

        @Override
        public void handle(KeyEvent t) {
            if(t.getCode() == KeyCode.ENTER) {
                Node node = (Node) t.getSource();
                node.impl_traverse(Direction.NEXT);
            }
        }
        
    };
    
    public AvatarDialog(Stage parent, final Window window) {
        primaryStage = parent;
        this.window = window;
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Set image dimensions");
        certificateUtils = new CertificateUtils();
        
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        
        Label messageLabel = new Label("Please give the image width and height");
        gridPane.add(messageLabel, 0, 0, 2, 1); // col, row, colspan, rowspan
        Label widthLabel = new Label("Width : ");
        gridPane.add(widthLabel, 0, 1);
        widthField = new TextField();
        widthField.setOnKeyPressed(traverse);
        gridPane.add(widthField, 1, 1);
        Label heightLabel = new Label("Height : ");
        gridPane.add(heightLabel, 0, 2);
        heightField = new TextField();
        heightField.setOnKeyPressed(traverse);
        gridPane.add(heightField, 1, 2);
        okButton = new Button("OK");
        
        okButton.setOnAction(newaction);
        GridPane.setHalignment(okButton, HPos.RIGHT);
        gridPane.add(okButton, 1, 3);
        
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
        setResizable(false);
        sizeToScene();
    }
    
    public void setImageHolder(CertificateTab imageHolder) {
        this.tab = imageHolder;
    }
    
    public void editImage(CertificateAvatar avatarImage) {
        this.subjectImage = avatarImage;
        imageX = (int) avatarImage.getX();
        imageY = (int) avatarImage.getY();
        String w = String.valueOf(subjectImage.getWidth());
        String h = String.valueOf(subjectImage.getHeight());
        widthField.setText(w);
        heightField.setText(h);
        okButton.setOnAction(editaction);
        show();
    }
    
    /*
     * click to show dialog
     */
    public void newImage(Point2D point) {
        imageX = (int) point.getX();
        imageY = (int) point.getY();
        widthField.setText("");
        heightField.setText("");
//        getScene().setCursor(Cursor.DEFAULT);
        okButton.setOnAction(newaction);
        show();
    }
    
    /*
     * click and drag to draw image
     */
    public void newImage(Point2D point, int width, int height) {
        imageX = (int) point.getX();
        imageY = (int) point.getY();
        Debugger.log("[AvatarDialog] adding image : x" + imageX + ", y" + imageY + ", widht" + width + ", height" + height);
//        if(!imageHolder.isAvatarFieldAdded()) { // done at an upper level
            CertificateField field = generateCertificateField(imageX, imageY, width, height);
            CertificateAvatar image = tab.createAvatarImage(field);
            AddCommand command = new AddCommand(image, tab);
            tab.getCommandManager().add(command);
//        }
    }
    
    public CertificateField generateCertificateField(int x, int y, int width, int height) {
        Integer _width = width; // new property implemetation
        Integer _height = height; // new property implemetation
        CertificateField field = new CertificateField(x, y, FieldType.IMAGE, _width, _height);
        return field;
    }
    
}
