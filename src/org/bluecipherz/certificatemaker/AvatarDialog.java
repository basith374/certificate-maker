/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    private CertificateTab imageHolder;
    
    private int imageX;
    private int imageY;
    
    private static CertificateUtils certificateUtils;
    
    private final TextField heightField;
    private final TextField widthField;
    private boolean disallowmultipleimages = !UserDataManager.isMultipleFieldsAllowed();
    
    private Button okButton;
    
    private ImageView prevImage;
    
    EventHandler<ActionEvent> newaction = new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent t) {
            if(!"".equals(widthField.getText()) || !"".equals(heightField.getText())) {
                int width = Integer.parseInt(widthField.getText());
                int height = Integer.parseInt(heightField.getText());
                if(width > 0 && height > 0) {
                    ImageView image = window.createAvatarImage(imageX, imageY, width, height);

                    Group group = (Group) ((ScrollPane)imageHolder.getContent()).getContent();

                    group.getChildren().add(image);
                    imageHolder.setAvatarFieldAdded(true);
                    close();
                } // else give positive number
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
                    ImageView image = window.createAvatarImage(imageX, imageY, width, height);
                    Group group = (Group) ((ScrollPane)imageHolder.getContent()).getContent();
                    group.getChildren().remove(prevImage);
                    group.getChildren().add(image);
                    imageHolder.setAvatarFieldAdded(true);
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
        this.imageHolder = imageHolder;
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
    
    public void newImage(Point2D point) {
        imageX = (int) point.getX();
        imageY = (int) point.getY();
        widthField.setText("");
        heightField.setText("");
//        getScene().setCursor(Cursor.DEFAULT);
        okButton.setOnAction(newaction);
        show();
    }
    
    public void newImage(Point2D point, int width, int height) {
        imageX = (int) point.getX();
        imageY = (int) point.getY();
        System.out.println("adding image : x" + imageX + ", y" + imageY + ", widht" + width + ", height" + height);
//        if(!imageHolder.isAvatarFieldAdded()) { // done at an upper level
            ImageView image = window.createAvatarImage(imageX, imageY, width, height);
            Group group = (Group) ((ScrollPane)imageHolder.getContent()).getContent();
            group.getChildren().add(image);
            if(disallowmultipleimages)
                imageHolder.setAvatarFieldAdded(true);
//        }
    }
    
}
