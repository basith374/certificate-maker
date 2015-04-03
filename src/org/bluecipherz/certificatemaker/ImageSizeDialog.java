/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import java.awt.geom.Rectangle2D;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author HaCkErZzZ
 */
public class ImageSizeDialog extends Stage {

    private Stage primaryStage;
    private Window window;
    private Group group;
    
    private int imageX;
    private int imageY;
    
    private static CertificateUtils certificateUtils;
    
    private final TextField heightField;
    private final TextField widthField;
    
    public ImageSizeDialog(Stage parent, final Window window, final Group group) {
        primaryStage = parent;
        this.window = window;
        this.group = group;
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Set image dimensions");
        certificateUtils = new CertificateUtils();
        
        GridPane gridPane = new GridPane();
        Label messageLabel = new Label("Please give the image width and height");
        gridPane.add(messageLabel, 0, 0, 2, 1); // col, row, colspan, rowspan
        Label widthLabel = new Label("Width : ");
        gridPane.add(widthLabel, 0, 1);
        widthField = new TextField();
        gridPane.add(widthField, 1, 1);
        Label heightLabel = new Label("Height : ");
        gridPane.add(heightLabel, 0, 2);
        heightField = new TextField();
        gridPane.add(heightField, 1, 2);
        
        Button onButton = new Button("OK");
        onButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if(!"".equals(widthField.getText()) || !"".equals(heightField.getText())) {
                    int width = Integer.parseInt(widthField.getText());
                    int height = Integer.parseInt(heightField.getText());
                    System.out.println("image added : x" + imageX + " y" + imageY + " width" + width + " height" + height + ", group childerns " + group.getChildren().size()); // debug
                    ImageView image = certificateUtils.createAvatarImage(imageX, imageX, width, height);
                    group.getChildren().add(image);
                    close();
                }
            }
        });
        gridPane.add(onButton, 0, 3, 2, 1);
        
        Scene scene = new Scene(gridPane, Color.WHITE);
        setScene(scene);
    }
    
    
    public void newImage(int x, int y) {
        imageX = x;
        imageY = y;
        widthField.setText("");
        heightField.setText("");
        sizeToScene();
        show();
    }    
    
}
