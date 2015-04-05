/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author bazi
 */
public class ConfirmBox extends Stage {
    
    private static final ImageView confirmImage = new ImageView();
    
    public static enum Response {
        YES,
        NO,
        CANCEL
    }

    public ConfirmBox(Stage primaryStage, String title, String message) {
        initOwner(primaryStage);
        initModality(Modality.NONE);
        setTitle(title);
        confirmImage.setImage(ResourceManger.getInstance().questionx32);
        
        Button button1 = new Button("Yes");
        Button button2 = new Button("No");
        Button button3 = new Button("Cancel");
    }
    
    private EventHandler<ActionEvent> getCloseAction() {
        return new EventHandler<ActionEvent>() {
          @Override
          public void handle(ActionEvent t) {
              close();
          }
        };
    }
    
}
