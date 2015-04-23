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

import com.sun.javafx.event.RedirectedEvent;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
public class CoverStage extends Stage {

    public CoverStage(Stage stage) {
        initOwner(stage);
        initModality(Modality.WINDOW_MODAL);
//        setOpacity(.90);
        setTitle("About Certificate Maker...");
        Pane splashLayout = new VBox();
        ImageView imageView = new ImageView(ResourceManger.getInstance().splash);
        splashLayout.setEffect(new DropShadow()); // drop shadow
        splashLayout.getChildren().add(imageView);
        Scene scene = new Scene(splashLayout, Color.TRANSPARENT);
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if(t.getCode() == KeyCode.ESCAPE) {
                    close();
                }
            }
        });
        setScene(scene);
        initStyle(StageStyle.TRANSPARENT);
        scene.getWindow().setEventDispatcher(new EventDispatcher() {
            @Override
            public Event dispatchEvent(Event event, EventDispatchChain edc) {
                if(event.getEventType() == RedirectedEvent.REDIRECTED) Debugger.log("about dialog just recieved a redirected event..."); // stupid debug
                if(event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    hide();
                }
                return null;
            }
        });
    }
    
}
