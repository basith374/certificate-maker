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

import com.sun.javafx.event.BasicEventDispatcher;
import com.sun.javafx.event.EventRedirector;
import com.sun.javafx.event.RedirectedEvent;
import com.sun.javafx.scene.EnteredExitedHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author bazi
 */
public final class AboutDialog extends Stage {
    private VBox aboutBox;
    private TextArea creditsTextArea;
    private TextArea licenseTextArea;
    
    private static CoverStage cover;
    
    private BorderPane borderPane;
    
    public AboutDialog(final Stage owner) {
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        
        borderPane = new BorderPane();
        borderPane.setPadding(new Insets(20));
        
        final ToggleGroup tg = new ToggleGroup();
        final ToggleButton tb1 = new ToggleButton("About");
        final ToggleButton tb2 = new ToggleButton("Credits");
        final ToggleButton tb3 = new ToggleButton("License");
        tg.getToggles().addAll(tb1, tb2, tb3);
        tg.selectToggle(tb1);
        tg.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, final Toggle oldValue, Toggle newValue) {
                if((newValue == null)) {
//                    Debugger.log("resuming");
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            tg.selectToggle(oldValue);
                        }
                    });
                } else {
//                    Debugger.log("continueing");
//                    if(oldValue.equals(tb1)) removeAboutItems();
//                    if(oldValue.equals(tb2)) removeCreditsItems();
//                    if(oldValue.equals(tb3)) removeLicenseItems();
                    if(!newValue.equals(oldValue)) {
//                        Debugger.log("add something");
                        if(newValue.equals(tb1)) addAboutItems();
                        if(newValue.equals(tb2)) addCreditsItems();
                        if(newValue.equals(tb3)) addLicenseItems();
                    } else {
//                        Debugger.log("learn something");
                    }
                }
            }
        });
        HBox hbox = new HBox();
        hbox.getChildren().addAll(tb1, tb2, tb3);
        hbox.setAlignment(Pos.CENTER);
        HBox.setMargin(tb1, new Insets(5));
        HBox.setMargin(tb2, new Insets(5));
        HBox.setMargin(tb3, new Insets(5));
        
        creditsTextArea = new TextArea();
        String creditsText = readFile("ext/CREDITS.txt");
        creditsTextArea.setText(creditsText);
        creditsTextArea.setEditable(false);
//        creditsTextArea.setMinWidth(500);
//        creditsTextArea.setMinHeight(300);
        aboutBox = new VBox();
        ImageView imageView = new ImageView(ResourceManger.getInstance().iconx100);
        Label descLabel = new Label("BCZ Certificate Maker");
        descLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 40));
        Label versionLabel = new Label("v2.0.3");
        Label copyLabel = new Label("Copyright (c) 2012-2015 Blue Cipherz Solutions Inc");
        Button splash = new Button("Cover");
        splash.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if(cover == null) cover = new CoverStage(owner);
                cover.show();
            }
        });
        aboutBox.getChildren().addAll(imageView, descLabel, versionLabel, copyLabel, splash);
        aboutBox.setAlignment(Pos.CENTER);
        VBox.setMargin(imageView, new Insets(15));
        VBox.setMargin(splash, new Insets(5));
        
        licenseTextArea = new TextArea();
        String licenseText = readFile("ext/LICENSE.txt");
        licenseTextArea.setText(licenseText);
        licenseTextArea.setEditable(false);
//        licenseTextArea.setMinWidth(500);
//        licenseTextArea.setMinHeight(300);
        
        Button okbutton = new Button("OK");
        okbutton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                close();
            }
        });
        okbutton.setMaxWidth(Double.MAX_VALUE);
        
//        BorderPane.setAlignment(hbox, Pos.CENTER);
        BorderPane.setAlignment(aboutBox, Pos.CENTER);
        BorderPane.setMargin(okbutton, new Insets(5));
        
        borderPane.setTop(hbox);
        borderPane.setCenter(aboutBox);
        borderPane.setBottom(okbutton);
        
        Scene scene = new Scene(borderPane, 600, 400, Color.GRAY);
        
        initStyle(StageStyle.UNDECORATED);
        setScene(scene);
        sizeToScene();
    }
    
    public void addAboutItems() {
//        gridPane.add(imageView, 0, 1, 3, 1);
        borderPane.setCenter(aboutBox);
    }
    
    public void addCreditsItems() {
//        gridPane.add(creditsTextArea, 0, 1, 3, 1);
        borderPane.setCenter(creditsTextArea);
    }
    
    public void addLicenseItems() {
//        gridPane.add(licenseTextArea, 0, 1, 3, 1);
        borderPane.setCenter(licenseTextArea);
    }
    
    public String readFile(String s) {
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fin = new FileInputStream(s);
            int i;
            do {
                i = fin.read();
                if(i != -1) sb.append((char) i);
            } while(i != -1);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AboutDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AboutDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }
    
}
