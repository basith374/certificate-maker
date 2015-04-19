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
        Debugger.log("[Launcher] Launching BCZ Certificate Maker(TM)...");
        ResourceManger.getInstance().loadSplashResource();
        ImageView splashimage = new ImageView(ResourceManger.getInstance().splash);
        splashLayout = new VBox();
        splashLayout.getChildren().add(splashimage);
        splashLayout.setEffect(new DropShadow());
    }

    @Override public void start(final Stage initStage) throws Exception {
        showSplash(initStage);
//        try {
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
//        } catch(Exception e) {
//            Alert.showAlertError(initStage, "ERROR", e.getLocalizedMessage());
//        }
    }

    private void showSplash(Stage initStage) {
        Scene splashScene = new Scene(splashLayout, Color.TRANSPARENT);
        initStage.initStyle(StageStyle.TRANSPARENT);
        initStage.setTitle("Starting Certificate Maker...");
        initStage.setScene(splashScene);
        initStage.show();
    }

}
