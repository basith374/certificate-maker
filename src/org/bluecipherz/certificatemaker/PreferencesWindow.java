/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author bazi
 */
public class PreferencesWindow extends Stage {

    public PreferencesWindow(Stage primaryStage) {
        initOwner(primaryStage);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Preferences");
        
        sizeToScene();
        show();
    }
    
    
    
}
