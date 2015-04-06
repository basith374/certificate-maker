/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author bazi
 */
public class Alert extends Stage {
    
    public static enum AlertType {
        INFO,
        WARN,
        ERROR
    }
    
    
    private static final ImageView infoImage = new ImageView();
    private static final ImageView warnImage = new ImageView();
    private static final ImageView errorImage = new ImageView();
    
    /**
     *
     * @param primaryStage
     * @param title
     * @param message
     * @param alertType
     */
    public Alert(Stage primaryStage, String title, String message, AlertType alertType) {
        initOwner(primaryStage);
        initModality(Modality.WINDOW_MODAL);
        setTitle(title);
        
        infoImage.setImage(ResourceManger.getInstance().infox32);
        warnImage.setImage(ResourceManger.getInstance().warnx32);
        errorImage.setImage(ResourceManger.getInstance().errorx32);
        
//        BorderPane borderPane = new BorderPane();
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(15));
        
//        HBox hbox = new HBox();
//        hbox.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        if(alertType == AlertType.INFO) {
            gridPane.add(infoImage, 0, 0);
            getIcons().add(ResourceManger.getInstance().infox16);
//            hbox.getChildren().add(infoImage);
        } else if(alertType == AlertType.WARN) {
            gridPane.add(warnImage, 0, 0);
            getIcons().add(ResourceManger.getInstance().warnx16);
//            hbox.getChildren().add(infoImage);
        } else if(alertType == AlertType.ERROR) {
            gridPane.add(errorImage, 0, 0);
            getIcons().add(ResourceManger.getInstance().errorx16);
//            hbox.getChildren().add(infoImage);
        }
        
        Label label = new Label(message);
        gridPane.add(label, 1, 0); // column, row, colspan, rowspan
        GridPane.setValignment(label, VPos.CENTER);
//        hbox.getChildren().add(label);
//        hbox.setAlignment(Pos.CENTER);
//        HBox.setMargin(label, new Insets(15));
        
//        GridPane.setHalignment(label, HPos.CENTER);
        Button okButton = new Button("OK");
        gridPane.add(okButton, 0, 1, 2, 1); // column, row, colspan, rowspan
        GridPane.setHalignment(okButton, HPos.RIGHT);
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                close();
            }
        });
        
//        borderPane.setTop(hbox);
//        borderPane.setCenter(okButton);
        
        
//        Scene scene = new Scene(borderPane, Color.WHITE);
        Scene scene = new Scene(gridPane, Color.WHITE);
        setScene(scene);
        sizeToScene();
        show();
    }
    
    /**
     *
     * @param primaryStage
     * @param title
     * @param alertType
     */
    public static Alert showAlert(Stage primaryStage, String title, String message, AlertType alertType) {
        return new Alert(primaryStage, title, message, alertType);
    }
    
    public static Alert showAlertInfo(Stage primaryStage, String title, String message) {
        return new Alert(primaryStage, title, message, AlertType.INFO);
    }
    
    public static Alert showAlertWarning(Stage primaryStage, String title, String message) {
        return new Alert(primaryStage, title, message, AlertType.WARN);
    }
    
    public static Alert showAlertError(Stage primaryStage, String title, String message) {
        return new Alert(primaryStage, title, message, AlertType.ERROR);
    }
}
