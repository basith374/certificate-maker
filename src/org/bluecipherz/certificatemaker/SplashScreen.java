/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import com.sun.scenario.effect.DropShadow;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author bazi
 */
public class SplashScreen extends Application {

    @Override
    public void start(Stage stage) throws Exception {
//        Pane splashLayout = new VBox();
        ResourceManger.getInstance().loadSplashResource();
//        if(ResourceManger.getInstance().splash == null) {
//            System.out.println("Whoops!");
//        }
//        ImageView splash = new ImageView(ResourceManger.getInstance().splash);
//        splashLayout.getChildren().add(splash);
//        Scene scene = new Scene(splashLayout);
////        Scene scene = new Scene(splashLayout, Color.TRANSPARENT);
////        stage.initStyle(StageStyle.TRANSPARENT);
//        stage.setScene(scene);
//        stage.show();
//        Thread.sleep(3000);
        Window window = new Window();
//        stage.hide();
        window.show();
    }
    
    public static void main(String[] args) { launch(args); }
}
