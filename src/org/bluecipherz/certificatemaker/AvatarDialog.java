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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    
    private ImageView prevImage;
    
    EventHandler<ActionEvent> newaction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
            if(!"".equals(widthField.getText()) || !"".equals(heightField.getText())) {
                try {
                    int width = Integer.parseInt(widthField.getText());
                    int height = Integer.parseInt(heightField.getText());
                    if(width > 0 && height > 0) {
                        CertificateField field = generateCertificateField(imageX, imageY, width, height);
                        ImageView image = tab.createAvatarImage(field);
                        tab.addNewImage(image, field);
                        tab.setAvatarFieldAdded(true);
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
                int width = Integer.parseInt(widthField.getText());
                int height = Integer.parseInt(heightField.getText());
                if(width > 0 && height > 0) {
                    Image image = tab.createImage(width, height);
                    prevImage.setImage(image);
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
        GridPane.setHalignment(gridPane, HPos.RIGHT);
        gridPane.add(okButton, 0, 3, 2, 1);
        
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
    
    public void setImageHolder(CertificateTab imageHolder) {
        this.tab = imageHolder;
    }
    
    public void editImage(ImageView imageView) {
        this.prevImage = imageView;
        imageX = (int) imageView.getX();
        imageY = (int) imageView.getY();
        String w = String.valueOf((int)prevImage.getImage().getWidth());
        String h = String.valueOf((int)prevImage.getImage().getHeight());
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
        Debugger.log("adding image : x" + imageX + ", y" + imageY + ", widht" + width + ", height" + height);
//        if(!imageHolder.isAvatarFieldAdded()) { // done at an upper level
            CertificateField field = generateCertificateField(imageX, imageY, width, height);
            ImageView image = tab.createAvatarImage(field);
            tab.addNewImage(image, field);
            tab.setAvatarFieldAdded(true);
//        }
    }
    
    public CertificateField generateCertificateField(int x, int y, int width, int height) {
        CertificateField field = new CertificateField(x, y);
        field.setFieldType(FieldType.IMAGE);
        field.setWidth(width);
        field.setHeight(height);
        return field;
    }
    
}
