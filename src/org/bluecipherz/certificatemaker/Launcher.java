/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 *
 * @author bazi
 */
public class Launcher extends Application {

    private Pane splashLayout;

    public static void main(String[] args) throws Exception { launch(args); }

    @Override public void init() {
        ResourceManger.getInstance().loadSplashResource();
        ImageView splash = new ImageView(ResourceManger.getInstance().splash);
        splashLayout = new VBox();
        splashLayout.getChildren().add(splash);
        splashLayout.setEffect(new DropShadow());
    }

    @Override public void start(final Stage initStage) throws Exception {
        showSplash(initStage);

        // wait for two seconds and show the main stage
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                Window window = new Window();
                window.show();
                initStage.hide();
            }
        });
        pause.play();
    }

    private void showSplash(Stage initStage) {
        Scene splashScene = new Scene(splashLayout, Color.TRANSPARENT);
        initStage.initStyle(StageStyle.TRANSPARENT);
        initStage.setTitle("Starting Certificate Maker...");
        initStage.setScene(splashScene);
        initStage.show();
    }

}
