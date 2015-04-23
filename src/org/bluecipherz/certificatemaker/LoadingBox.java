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

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author bazi
 */
public class LoadingBox {
    private Stage stage;
    private ProgressIndicator progress;
    
    public LoadingBox(Stage primaryStage) {
        stage = new Stage();
        stage.initOwner(primaryStage);
        VBox vbox = new VBox();
        progress = new ProgressIndicator();
        progress.progressProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldVal, Number newVal) {
                if(newVal.intValue()==1) {
                    stage.hide();
                }
            }
        });
        vbox.getChildren().add(progress);
        Scene scene = new Scene(vbox, Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
    }
    
    public void showProgressing(ReadOnlyDoubleProperty doubleProperty) {
        progress.progressProperty().bind(doubleProperty);
        stage.show();
    }
    
}
