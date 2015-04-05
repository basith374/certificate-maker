/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author bazi
 */
public class AboutDialog extends Stage {
    
    public AboutDialog(Stage owner) {
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        setOpacity(.90);
        setTitle("About Certificate Maker...");
        Pane splashLayout = new VBox();
        ImageView imageView = new ImageView(ResourceManger.getInstance().splash);
        splashLayout.setEffect(new DropShadow());
        splashLayout.getChildren().add(imageView);
        Scene scene = new Scene(splashLayout, Color.TRANSPARENT);
        setScene(scene);
        initStyle(StageStyle.TRANSPARENT);
        show();
    }
    
}
