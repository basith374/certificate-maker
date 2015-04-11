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
