/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
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
