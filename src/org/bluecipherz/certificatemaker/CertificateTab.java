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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import static org.bluecipherz.certificatemaker.Window.getMouseMode;

/**
 *
 * @author bazi
 */
public final class CertificateTab extends Tab {

    private static Stage PRIMARY_STAGE;
    private static Window WINDOW;
    
    private final CertificateWrapper wrapper;
    private final ScrollPane scrollPane;
    private final Group fieldContainer;
    private final ImageView imageView;;
    
    private BooleanProperty dateFieldAdded;
    private BooleanProperty regnoFieldAdded;
    private BooleanProperty avatarFieldAdded;
    
    private BooleanProperty changed;
    
    private File certificateFile;
    
    private static boolean disallowmultiplefields = !UserDataManager.isMultipleFieldsAllowed();

    public CertificateTab(CertificateWrapper wrapper) {
        this.wrapper = wrapper;
        setText(wrapper.getName());
        scrollPane = new ScrollPane();
        fieldContainer = new Group();
        
        imageView = new ImageView();
        
        avatarFieldAdded = new SimpleBooleanProperty(false);
        dateFieldAdded = new SimpleBooleanProperty(false);
        regnoFieldAdded = new SimpleBooleanProperty(false);
        changed  = new SimpleBooleanProperty(false);
        
        EventHandler<MouseEvent> handler = getImageMouseHandler();
        imageView.setOnMousePressed(handler);
        imageView.setOnMouseDragged(handler);
        imageView.setOnMouseReleased(handler);
        
        fieldContainer.getChildren().add(imageView);
        
        // create gui components from wrapper fields
        if(!wrapper.getCertificateFields().isEmpty()) {   
            for (CertificateField certificateField : wrapper.getCertificateFields()) {
                if(certificateField.getFieldType() == FieldType.IMAGE) {
                    ImageView avatarImage = createAvatarImage(certificateField);
                    addImage(avatarImage, certificateField);
                    setAvatarFieldAdded(true);
                } else {
                    CertificateText certificateText = createText(certificateField);
                    certificateText.setX(certificateField.getX() - certificateText.getLayoutBounds().getWidth() / 2); // alignment
                    fieldContainer.getChildren().add(certificateText);
                    if(disallowmultiplefields) {
                        if(certificateField.getFieldType() == FieldType.DATE) setDateFieldAdded(true);
                        if(certificateField.getFieldType() == FieldType.REGNO) setRegnoFieldAdded(true);
                    }
                }
            }
        }
        
        scrollPane.setContent(fieldContainer);
        setContent(scrollPane);
    }

    public ReadOnlyDoubleProperty loadImage() {
        final Image image = new Image(wrapper.getCertificateImage().toURI().toString(), true);
        image.progressProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                if(newValue.intValue() == 1) {
                    imageView.setImage(image);
                }
            }

        });
        return image.progressProperty();
    }
    
    /*
     * BOOLEANS
     */

    public boolean isAvatarFieldAdded() {
        return avatarFieldAdded.get();
    }

    public void setAvatarFieldAdded(boolean avatarFieldAdded) {
        this.avatarFieldAdded.set(avatarFieldAdded);
    }

    public boolean isDateFieldAdded() {
        return dateFieldAdded.get();
    }

    public void setDateFieldAdded(boolean dateFieldAdded) {
        this.dateFieldAdded.set(dateFieldAdded);
    }

    public boolean isRegnoFieldAdded() {
        return regnoFieldAdded.get();
    }

    public void setRegnoFieldAdded(boolean regnoFieldAdded) {
        this.regnoFieldAdded.set(regnoFieldAdded);
    }
    
    public boolean isChanged() {
        return changed.get();
    }

    public void setChanged(boolean changed) {
        this.changed.set(changed);
    }

    /*
     * PROPERTIES
     */
    
    public BooleanProperty dateFieldProperty() {
        return dateFieldAdded;
    }
    public BooleanProperty regnoFieldProperty() {
        return regnoFieldAdded;
    }
    
    /*
     * OTHER GETTERS AND SETTERS
     */
    
    public File getFile() {
        return certificateFile;
    }

    public void setFile(File file) {
        this.certificateFile = file;
    }

    public CertificateWrapper getWrapper() {
        return wrapper;
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }
    
    public Group getFieldContainer() {
        return fieldContainer;
    }
    
    /*****************
     * EVENT HANDLERS
     ****************/
    
    public EventHandler<MouseEvent> getAvatarMouseHandler() {
        return new EventHandler<MouseEvent>() {
            double initialEventX;
            double initialEventY;
            double initialComponentX;
            double initialComponentY;
            @Override
            public void handle(MouseEvent event) {
                System.out.println("clicked : x" + event.getX() + ", y" + event.getY());
                EventType<? extends Event> eventType = event.getEventType();
                ImageView imageView = (ImageView) event.getSource();
                if (eventType.equals(MouseEvent.MOUSE_PRESSED)) {
                    if(Window.getMouseMode() == Window.MODE_MOVE) {
                        initialComponentX = imageView.getX();
                        initialComponentY = imageView.getY();
                        initialEventX = event.getX();
                        initialEventY = event.getY();
//                        System.out.println("Mode : move"); // debug
                    } else if (Window.getMouseMode() == Window.MODE_DELETE) {
                        Group parent = (Group) imageView.getParent();
                        parent.getChildren().remove(imageView);
                        setAvatarFieldAdded(false);
//                        System.out.println("Mode : delete"); // debug
                    } else if(Window.getMouseMode() == Window.MODE_EDIT) {
                        System.out.println("Editing image"); // debug
                        WINDOW.showEditAvatarDialog(CertificateTab.this, imageView);
                    }
                } else if (eventType.equals(MouseEvent.MOUSE_DRAGGED)) {
                    if(Window.getMouseMode() == Window.MODE_MOVE) {
                    double currentX = event.getX();
                        double currentY = event.getY();
                        double x = currentX - initialEventX + initialComponentX;
                        double y = currentY - initialEventY + initialComponentY;
                        imageView.setX(x);
                        imageView.setY(y);
                    }
                } else if (eventType.equals(MouseEvent.MOUSE_RELEASED)) {
                    if(Window.getMouseMode() == Window.MODE_MOVE) {
                        double currentX = event.getX();
                        double currentY = event.getY();
                        double x = currentX - initialEventX + initialComponentX;
                        double y = currentY - initialEventY + initialComponentY;
                        imageView.setX(x);
                        imageView.setY(y);
                    }
                }
            }
        };
    }
    
    public EventHandler<MouseEvent> getTextMouseHandler() {
         return new EventHandler<MouseEvent>() {
            double initialEventX;
            double initialEventY;
            double initialComponentX;
            double initialComponentY;
            @Override
            public void handle(MouseEvent event) {
                EventType<? extends Event> eventType = event.getEventType();
                CertificateText text = (CertificateText) event.getSource();
                if (eventType.equals(MouseEvent.MOUSE_PRESSED)) {
                    if(Window.getMouseMode() == Window.MODE_MOVE) {
                        initialComponentX = text.getX();
                        initialComponentY = text.getY();
                        initialEventX = event.getX();
                        initialEventY = event.getY();
//                        System.out.println("Mode : move"); // debug
                    } else if (Window.getMouseMode() == Window.MODE_DELETE) {
                        Group parent = (Group) text.getParent();
                        parent.getChildren().remove(text);
                        System.out.println("Deleting : " + text.getText() + ", contents :" + (parent.getChildren().size() - 1)); // new debug
                        FieldType type = text.fieldTypeProperty().get();
                        if(type == FieldType.DATE) setDateFieldAdded(false);
                        if(type == FieldType.REGNO) setRegnoFieldAdded(false);
//                        System.out.println("Mode : delete"); // debug
                    } else if (Window.getMouseMode() == Window.MODE_EDIT) {
                        
                        WINDOW.showEditFieldDialog(CertificateTab.this, text);
//                        System.out.println("Mode : edit"); // debug
                    }
                } else if (eventType.equals(MouseEvent.MOUSE_DRAGGED)) {
                    if(Window.getMouseMode() == Window.MODE_MOVE) {
                        double currentX = event.getX();
                        double currentY = event.getY();
                        double x = currentX - initialEventX + initialComponentX;
                        double y = currentY - initialEventY + initialComponentY;
                        text.setX(x);
                        text.setY(y);
                    }
                } else if (eventType.equals(MouseEvent.MOUSE_RELEASED)) {
                    if(Window.getMouseMode() == Window.MODE_MOVE) {
                        double currentX = event.getX();
                        double currentY = event.getY();
                        double x = currentX - initialEventX + initialComponentX;
                        double y = currentY - initialEventY + initialComponentY;
                        text.setX(x);
                        text.setY(y);
                    }
                }
            }
        };
    }
    
    /**
     * get mouse handlers fort the image view
     * @param dialog
     * @return 
     */
    private EventHandler<MouseEvent> getImageMouseHandler(){
        return new EventHandler<MouseEvent>() {
            double initialX;
            double initialY;
            Line adjXline = getStrokedLine();
            Line adjYline = getStrokedLine();
            Line oppXline = getStrokedLine();
            Line oppYline = getStrokedLine();
            @Override
            public void handle(MouseEvent event) {
                EventType<? extends Event> type = event.getEventType();
                if(type.equals(MouseEvent.MOUSE_CLICKED))
                    System.out.println("clicked : x" + event.getX() + ", y" + event.getY() + ", " + type.toString()); // debug
                
                if(type.equals(MouseEvent.MOUSE_PRESSED)) {
                    initialX = event.getX();
                    initialY = event.getY();
                    if(getMouseMode() == Window.MODE_ADD) {
                        WINDOW.showNewFieldDialog(CertificateTab.this, new Point2D(event.getX(), event.getY()));
                    } else if(getMouseMode() == Window.MODE_ADDIMAGE) { // remove this if if you want selection lines on all modes
                        fieldContainer.getChildren().add(adjXline);
                        fieldContainer.getChildren().add(adjYline);
                        fieldContainer.getChildren().add(oppYline);
                        fieldContainer.getChildren().add(oppXline);
                        
                        adjXline.setStartX(initialX);
                        adjXline.setStartY(initialY);
                        adjXline.setEndX(initialX);
                        adjXline.setEndY(initialY);
                        adjYline.setStartX(initialX);
                        adjYline.setStartY(initialY);
                        adjYline.setEndX(initialX);
                        adjYline.setEndY(initialY);
                        oppXline.setStartX(initialX);
                        oppXline.setStartY(initialY);
                        oppXline.setEndX(initialX);
                        oppXline.setEndY(initialY);
                        oppYline.setStartX(initialX);
                        oppYline.setStartY(initialY);
                        oppYline.setEndX(initialX);
                        oppYline.setEndY(initialY);
                    }
                } else if(type.equals(MouseEvent.MOUSE_DRAGGED)) {
//                    adjXline.setStartX(initialX);
//                    adjXline.setEndX(event.getX());
//                    adjXline.setStartY(initialY);
//                    adjXline.setEndY(event.getY());
                    if(getMouseMode() == Window.MODE_ADDIMAGE) { // remove this if if you want selection lines on all modes
                        adjXline.setStartX(event.getX());
                        adjXline.setEndX(event.getX());
                        adjXline.setStartY(event.getY());
                        adjXline.setEndY(initialY);

                        adjYline.setStartX(initialX);
                        adjYline.setEndX(event.getX());
                        adjYline.setStartY(event.getY());
                        adjYline.setEndY(event.getY());

                        oppXline.setStartX(initialX);
                        oppXline.setEndX(event.getX());
                        oppXline.setStartY(initialY);
                        oppXline.setEndY(initialY);

                        oppYline.setStartX(initialX);
                        oppYline.setEndX(initialX);
                        oppYline.setStartY(initialY);
                        oppYline.setEndY(event.getY());
                    }
                    
                } else if(type.equals(MouseEvent.MOUSE_RELEASED)) {
                    if(getMouseMode() == Window.MODE_ADDIMAGE) {
                        if(!isAvatarFieldAdded()) {
                            if(initialX == event.getX() && initialY == event.getY()) {
                                System.out.println("clicked, showing avatar dialog"); // debug
                                WINDOW.showAvatarAddDialog(CertificateTab.this, new Point2D(event.getX(), event.getY()));
                            } else {
                                System.out.println("released, calculating avatar size"); // debug
                                WINDOW.addAvatar(CertificateTab.this, new Point2D(initialX, initialY), new Point2D(event.getX(), event.getY()));
                            }
                        } else {
                            Alert.showAlertInfo(PRIMARY_STAGE, "Info", "Avatar already added"); // temporarily commented
                        }
                    }
                    fieldContainer.getChildren().remove(adjXline);
                    fieldContainer.getChildren().remove(oppXline);
                    fieldContainer.getChildren().remove(adjYline);
                    fieldContainer.getChildren().remove(oppYline);
                }
            }
            
            private Line getStrokedLine() {
                Line line = new Line();
                line.getStrokeDashArray().addAll(3d, 3d);
                return line;
            }
        };
    }

    
    public void addText(CertificateText text, CertificateField field) {
        fieldContainer.getChildren().add(text);
        wrapper.getCertificateFields().add(field);
        System.out.println("new field added " + field.getFieldType() + ", total wrapper contents : " + wrapper.getCertificateFields().size());
    }
    
    public void addImage(ImageView image, CertificateField field) {
        fieldContainer.getChildren().add(image);
        wrapper.getCertificateFields().add(field);
        System.out.println("new field added " + field.getFieldType() + ", total wrapper contents : " + wrapper.getCertificateFields().size());
    }
    
    /**
     * used by outsiders
     * converts and CertificateField into a Text object
     * @param field
     * @return 
     */
    public CertificateText createText(final CertificateField field) {
        CertificateText text = new CertificateText(field);
        // listeners
        EventHandler<MouseEvent> mouseHandler = getTextMouseHandler();
        text.setOnMousePressed(mouseHandler);
        text.setOnMouseDragged(mouseHandler);
        text.setOnMouseReleased(mouseHandler);
        // bindings
        field.observe(text);
        return text;
    }

    public ImageView createAvatarImage(final CertificateField field) {
        Image image = createImage(field.getWidth(), field.getHeight());
//        System.out.println("image dimensions : " + width + "x" + height); // debug
        ImageView imageView = new ImageView(image);
        imageView.setX(field.getX());
        imageView.setY(field.getY());
//        System.out.println("image coords : x" + x + " y" + y); // debug
        EventHandler<MouseEvent> mouseHandler = getAvatarMouseHandler();
        imageView.setOnMousePressed(mouseHandler);
        imageView.setOnMouseDragged(mouseHandler);
        imageView.setOnMouseReleased(mouseHandler);
        // bindings
        field.observe(imageView);
        return imageView;
    }
    
    public Image createImage(int width, int height)  {
        return new Image(getClass().getResourceAsStream("icons/avatarx1500.png"), width, height, false, true);
    }
    
    /*
     * STATIC GETTERS AND SETTERS
     */
    
    public static Stage getPRIMARY_STAGE() {
        return PRIMARY_STAGE;
    }

    public static void setPRIMARY_STAGE(Stage PRIMARY_STAGE) {
        CertificateTab.PRIMARY_STAGE = PRIMARY_STAGE;
    }

    public static Window getWINDOW() {
        return WINDOW;
    }

    public static void setWINDOW(Window WINDOW) {
        CertificateTab.WINDOW = WINDOW;
    }
    
    
}
