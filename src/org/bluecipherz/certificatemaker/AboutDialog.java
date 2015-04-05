/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import com.sun.javafx.event.BasicEventDispatcher;
import com.sun.javafx.event.EventRedirector;
import com.sun.javafx.event.RedirectedEvent;
import com.sun.javafx.scene.EnteredExitedHandler;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
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
public class AboutDialog extends Stage {
    
    public AboutDialog(Stage owner) {
        initOwner(owner);
        initModality(Modality.WINDOW_MODAL);
//        setOpacity(.90);
        setTitle("About Certificate Maker...");
        Pane splashLayout = new VBox();
        ImageView imageView = new ImageView(ResourceManger.getInstance().splash);
        splashLayout.setEffect(new DropShadow()); // drop shadow
        splashLayout.getChildren().add(imageView);
        Scene scene = new Scene(splashLayout, Color.TRANSPARENT);
        setScene(scene);
        initStyle(StageStyle.TRANSPARENT);
        scene.getWindow().setEventDispatcher(new EventDispatcher() {
            @Override
            public Event dispatchEvent(Event event, EventDispatchChain edc) {
                /*
                 * code meant for external press dialog close. you know what i mean. but apparently it 
                 * doesnt work.(from stackoverflow)
                 */
//                System.out.println("Event dispatched..." + event.getEventType().toString()); // debug
//                if(event.getEventType() == RedirectedEvent.REDIRECTED) {
//                    System.out.println("Event redirected..."); // debug
//                    RedirectedEvent re = (RedirectedEvent) event;
//                    System.out.println("redirectedevent + " + re.getOriginalEvent().getEventType().toString());
//                    if(re.getOriginalEvent().getEventType() == MouseEvent.MOUSE_PRESSED) {
//                        hide();
//                    }
//                } else {
//                    System.out.println("Event bounced..."); // debug
//                    edc.dispatchEvent(event);
//                }
                if(event.getEventType() == RedirectedEvent.REDIRECTED) System.out.println("about dialog just recieved a redirected event..."); // stupid debug
                if(event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    hide();
                }
                return null;
            }
        });
    }
    
}
