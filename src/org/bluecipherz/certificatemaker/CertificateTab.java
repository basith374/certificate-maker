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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import static org.bluecipherz.certificatemaker.Window.getMouseMode;

/**
 *
 * @author bazi
 */
public final class CertificateTab {

    private static Stage PRIMARY_STAGE; // backreference
    private static Window WINDOW; // backreference
    
     // very loose implementation, do something abt it.
    private static HashMap<Tab, CertificateTab> tabMap = Window.tabMap;
    
    private final ScrollPane scrollPane; // pannable, zoomable in the future :)
    private final Group fieldContainer;
    private final ImageView certificateImageView;
    
    private BooleanProperty dateFieldAdded; // dont know if this should be a wrapper type
    private BooleanProperty regnoFieldAdded; // dont know if this should be a wrapper type
    private BooleanProperty avatarFieldAdded; // dont know if this should be a wrapper type
    
    private BooleanProperty changed; // this will get obsolete in the future
    
    private File certificateFile;
    private ObservableList<CertificateField> fields; // this can be downgraded to ArrayList.
    private ReadOnlyStringWrapper name;
    private ReadOnlyObjectWrapper<File> certificateImage;
    
    private final Tab tab;
    
    private CommandManager commandManager = new CommandManager();
    
    /* used for action listeners, poor thing, they dont know what a certificate node is
     * totally waste of resources but necessary. :( maybe find something better in the future
     */
    private HashMap<Node, CertificateNode> nodeMap = new HashMap<>();
    
    public CertificateNode getCertificateNode(Node node) {
//        if(node instanceof ImageView) {
//            ImageView image = (ImageView) node;
//            return certificateContents.get(image);
//        } else if(node instanceof Text) {
//            Text text = (Text) node;
//            return certificateContents.get(text);
//        }
//        return null;
        return nodeMap.get(node);
    }
    
    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ObservableList<CertificateField> getFields() {
        return fields;
    }

    public void setFields(ObservableList<CertificateField> fields) {
        this.fields = fields;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public File getCertificateImage() {
        return certificateImage.get();
    }

    public void setCertificateImage(ReadOnlyObjectWrapper<File> certificateImage) {
        this.certificateImage = certificateImage;
    }
    
    private static boolean disallowmultiplefields = !UserDataManager.isMultipleFieldsAllowed();

    public Tab get() {
        return tab;
    }
    
    public CertificateTab(CertificateWrapper wrapper) {
        this.tab = new Tab();
        tab.tabPaneProperty().addListener(new ChangeListener<TabPane>() {
            @Override
            public void changed(ObservableValue<? extends TabPane> ov, TabPane t, TabPane t1) {
                if(t1 == null) {
                    tabMap.remove(tab);
                }
            }
        });
        fields = FXCollections.observableArrayList(wrapper.getCertificateFields());
        fields.addListener(new ListChangeListener<CertificateField>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends CertificateField> change) {
                Debugger.log("[CertificateTab] no of fields changed : " + fields.size()
                        + "\n[CertificateTab] fields : " + change.getList());
            }
        });
        name = new ReadOnlyStringWrapper();
        name.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                tab.setText(t1);
            }
        });
        name.set(wrapper.getName());
        certificateImage = new ReadOnlyObjectWrapper<>(wrapper.getCertificateImage());
        
        scrollPane = new ScrollPane();
        scrollPane.setPannable(true);
        fieldContainer = new Group();
        fieldContainer.getChildren().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends Node> change) {
//                Debugger.log("field container changed, total contents :" + (fieldContainer.getChildren().size() - 1));
//                if(change.wasAdded()) { // something added 
//                    for(Node node : change.getAddedSubList()) {
//                        if(node instanceof CertificateText) {
//                            CertificateText text = (CertificateText) node;
//                            if(text.getObserver() == null) Debugger.log("Illegal field container addition...");
//                        } else { // imageview
//                            
//                        }
//                    }
//                } else if(change.wasRemoved()) { // something removed
//                    for(Node node : change.getRemoved()) {
//                        
//                    }
//                }
            }
        });
        
        certificateImageView = new ImageView();
        
        avatarFieldAdded = new SimpleBooleanProperty(false);
        dateFieldAdded = new SimpleBooleanProperty(false);
        regnoFieldAdded = new SimpleBooleanProperty(false);
        changed  = new SimpleBooleanProperty(false);
        changed.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                Debugger.log("file changed :s" + t1);
            }
        });
        
        EventHandler<MouseEvent> handler = getImageMouseHandler();
        certificateImageView.setOnMousePressed(handler);
        certificateImageView.setOnMouseDragged(handler);
        certificateImageView.setOnMouseReleased(handler);
        
        fieldContainer.getChildren().add(certificateImageView);
        
        // create gui components from wrapper fields
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                loadGuiComponents();
            }
        });
        
        scrollPane.setContent(fieldContainer);
        tab.setContent(scrollPane);
    }
    
    private void loadGuiComponents() {
        if(!fields.isEmpty()) {
            for (CertificateField field : fields) {
                if(field.getFieldType() == FieldType.IMAGE) {
                    CertificateAvatar avatarImage = createAvatarImage(field);
                    addNode(avatarImage); // add just gui components
                } else {
                    CertificateText certificateText = createText(field);
                    addNode(certificateText); // add just gui components
                }
            }
        }
    }

    public CertificateWrapper getDisplayableWrapper() {
        CertificateWrapper wrapper = new CertificateWrapper();
        wrapper.setCertificateFields(fields);
        wrapper.setCertificateImage(certificateImage.get());
        wrapper.setName(name.get());
        Debugger.log("[CertificateTab]Retrieving wrapper : " + wrapper);
        return wrapper;
    }
    
    public CertificateWrapper getSerializableWrapper() {
        CertificateWrapper wrapper = new CertificateWrapper();
//        List<CertificateField> _fields = fields;
//        wrapper.setCertificateFields(_fields);
        ArrayList<CertificateField> _fields = new ArrayList<>(fields);
        wrapper.setCertificateFields(_fields);
        wrapper.setCertificateImage(certificateImage.get());
        wrapper.setName(name.get());
        return wrapper;
    }
    
    /**
     * used by fields dialog
     * @return 
     */
    public ObservableList<CertificateNode> createNodeList() {
        ObservableList<CertificateNode> nodes = FXCollections.observableArrayList();
        for(Map.Entry<Node, CertificateNode> node : nodeMap.entrySet()) {
            nodes.add(node.getValue());
        }
        return nodes;
    }
    
    public ReadOnlyDoubleProperty loadImage() {
        Debugger.log("[CertificateTab] Opening certificate image : " + certificateImage.get().toURI().toString()); // debug
        final Image image = new Image(certificateImage.get().toURI().toString(), true);
        image.progressProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                if(newValue.intValue() == 1) {
                    Debugger.log("[CertificateTab] loaded image..."); // debug
                    certificateImageView.setImage(image);
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
                Debugger.log("[CeritificateTab] clicked : x" + event.getX() + ", y" + event.getY()); // debug
                EventType<? extends Event> eventType = event.getEventType();
                MouseButton button = event.getButton();
                ImageView imageView =  (ImageView) event.getSource();
                CertificateAvatar ca = (CertificateAvatar) getCertificateNode(imageView);
                if (eventType.equals(MouseEvent.MOUSE_PRESSED)) {
                    if(button == MouseButton.PRIMARY) {
                        if(Window.getMouseMode() == Window.MODE_EDIT) { // pan bug fix
                            Debugger.log("[CertificateTab] opening image edit dialog"); // debug
                            WINDOW.showEditAvatarDialog(CertificateTab.this, ca);
                        } else {
                            scrollPane.setPannable(false);
                            if(Window.getMouseMode() == Window.MODE_MOVE) {
                                initialComponentX = imageView.getX();
                                initialComponentY = imageView.getY();
                                initialEventX = event.getX();
                                initialEventY = event.getY();
                            } else if (Window.getMouseMode() == Window.MODE_DELETE) {
                                // new command pattern
                                DeleteCommand command = new DeleteCommand(ca);
                                commandManager.add(command);
                            }
                        }
                    }
                } else if (eventType.equals(MouseEvent.MOUSE_DRAGGED)) {
                    if(button == MouseButton.PRIMARY) {
                        if(Window.getMouseMode() == Window.MODE_MOVE) {
                        double currentX = event.getX();
                            double currentY = event.getY();
                            double x = currentX - initialEventX + initialComponentX;
                            double y = currentY - initialEventY + initialComponentY;
                            imageView.setX(x);
                            imageView.setY(y);
                        }
                    }
                } else if (eventType.equals(MouseEvent.MOUSE_RELEASED)) {
                    if(button == MouseButton.PRIMARY) {
                        scrollPane.setPannable(true); // scrollpane pan
                        if(Window.getMouseMode() == Window.MODE_MOVE) {
                            double currentX = event.getX();
                            double currentY = event.getY();
                            double x = currentX - initialEventX + initialComponentX;
                            double y = currentY - initialEventY + initialComponentY;
                            // save command history
                            Point2D start = new Point2D(initialComponentX, initialComponentY);
                            Point2D end = new Point2D(x, y);
                            MoveCommand command = new MoveCommand(ca, start, end);
                            commandManager.add(command);
                            // move
//                            imageView.setX(x);
//                            imageView.setY(y);
                        }
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
                MouseButton button = event.getButton();
                Text text = (Text) event.getSource();
                CertificateText ct = (CertificateText) getCertificateNode(text);
                if (eventType.equals(MouseEvent.MOUSE_PRESSED)) {
                    if(button == MouseButton.PRIMARY) {
                        if(Window.getMouseMode() == Window.MODE_EDIT) { // pan bug fix
                            WINDOW.showEditFieldDialog(CertificateTab.this, ct);
                        } else {
                            scrollPane.setPannable(false); // scrollpane pan
                            if(Window.getMouseMode() == Window.MODE_MOVE) {
                                initialComponentX = text.getX();
                                initialComponentY = text.getY();
                                initialEventX = event.getX();
                                initialEventY = event.getY();
                                Debugger.log("[CertificateTab] moving component start : " + initialEventX + "," + initialEventY); // debug
                            } else if (Window.getMouseMode() == Window.MODE_DELETE) {
                                // new command pattern
                                DeleteCommand command = new DeleteCommand(ct);
                                commandManager.add(command);
    //                            CertificateTab tab = ct.getContainer();
    //                            tab.removeNode(ct); // highly controlled remove action, very safe
                                Debugger.log("[CertificateTab] Deleting : " + text.getText() + ", contents :" + nodeMap.size()); // new debug
                            }
                        }
                    }
                } else if (eventType.equals(MouseEvent.MOUSE_DRAGGED)) {
                    if(button == MouseButton.PRIMARY) {
                        if(Window.getMouseMode() == Window.MODE_MOVE) {
                            double currentX = event.getX();
                            double currentY = event.getY();
                            double x = currentX - initialEventX + initialComponentX;
                            double y = currentY - initialEventY + initialComponentY;
                            text.setX(x);
                            text.setY(y);
                        }
                    }
                } else if (eventType.equals(MouseEvent.MOUSE_RELEASED)) {
                    if(button == MouseButton.PRIMARY) {
                        scrollPane.setPannable(true); // scrollpane pan
                        if(Window.getMouseMode() == Window.MODE_MOVE) {
                            double currentX = event.getX();
                            double currentY = event.getY();
                            double x = currentX - initialEventX + initialComponentX;
                            double y = currentY - initialEventY + initialComponentY;
                            Debugger.log("[CertificateTab] moving component end : " + currentX + "," + currentY); // debug
                            // new command pattern
                            Point2D start = new Point2D(initialComponentX, initialComponentY);
                            Point2D end = new Point2D(x, y);
                            MoveCommand command = new MoveCommand(ct, start, end);
                            commandManager.add(command);
                            // real move
//                            text.setX(x);
//                            text.setY(y);
                        }
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
                MouseButton button = event.getButton();
                
                if(type.equals(MouseEvent.MOUSE_PRESSED)) {
                    initialX = event.getX();
                    initialY = event.getY();
                    if(button == MouseButton.PRIMARY) {
                        Debugger.log("[CertificateTab] clicked : x" + event.getX() + ", y" + event.getY() + ", " + type.toString()); // debug
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
                            
                            scrollPane.setPannable(false); //  disable scrollpane pan
                        } else {
                            scrollPane.setPannable(false); // disable scrollpane pan
                        }
                    }
                    
                } else if(type.equals(MouseEvent.MOUSE_DRAGGED)) {
                    if(button == MouseButton.PRIMARY) {
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
                    }
                } else if(type.equals(MouseEvent.MOUSE_RELEASED)) {
                    if(button == MouseButton.PRIMARY) {
                        scrollPane.setPannable(true);
                        if(getMouseMode() == Window.MODE_ADDIMAGE) {
                            if(!isAvatarFieldAdded()) {
                                if(initialX == event.getX() && initialY == event.getY()) {
                                    Debugger.log("[CertificateTab] clicked, showing avatar dialog"); // debug
                                    WINDOW.showAvatarAddDialog(CertificateTab.this, new Point2D(event.getX(), event.getY()));
                                } else {
                                    Debugger.log("[CertificateTab] released, calculating avatar size"); // debug
                                    WINDOW.addAvatar(CertificateTab.this, new Point2D(initialX, initialY), new Point2D(event.getX(), event.getY()));
                                }
                            } else {
                                Alert.showAlertInfo(PRIMARY_STAGE, "Info", "Avatar already added"); // temporarily commented
                            }
                        } else { // works for add, delete, edit, move
                            Debugger.log("[CertificateTab] mouse released at : " + event.getX() + "," + event.getY()); // debug
                        }
                        fieldContainer.getChildren().remove(adjXline);
                        fieldContainer.getChildren().remove(oppXline);
                        fieldContainer.getChildren().remove(adjYline);
                        fieldContainer.getChildren().remove(oppYline);
                    }
                }
            }
            
            private Line getStrokedLine() {
                Line line = new Line();
                line.getStrokeDashArray().addAll(3d, 3d);
                return line;
            }
        };
    }

    /*
     * used when loading wrapper where CertificateField is already specified
     */
    public CertificateField addNode(CertificateNode node) {
        fieldContainer.getChildren().add(node.get());
        CertificateField field = node.getObserver(); // may be renamed in future
        if(field == null) Debugger.log("[CertificateTab] node' observer is null"); // debug
//        Debugger.log("[CertificateTab]observer field :" + field); // debug
        nodeMap.put(node.get(), node); // latest implementation, used by event handlers
        node.setContainer(this); // backreference
//        field.changedProperty().addListener(new ChangeListener<Boolean>() { // TODO implement this
//            @Override
//            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
//                if(t1) setChanged(true);
//            }
//        });
        FieldType type = node.getFieldType();
        if(type == FieldType.DATE) setDateFieldAdded(true);
        if(type == FieldType.REGNO) setRegnoFieldAdded(true);
        if(type == FieldType.IMAGE) setAvatarFieldAdded(true);
        return field;
    }
    
    /*
     * used when there is no wrapper
     */
    public void addNewNode(CertificateNode node) {
        CertificateField field = addNode(node);
        if(field == null) Debugger.log("[CertificateTab] WARN : adding field that is null");
        fields.add(field);
    }
    
    public boolean removeNode(CertificateNode node) {
        CertificateField field = node.getObserver();
        fields.remove(field);
        nodeMap.remove(node.get()); // latest implementation
        node.setContainer(null); // fix
        FieldType type = node.getFieldType();
        if(type == FieldType.DATE) setDateFieldAdded(false);
        if(type == FieldType.REGNO) setRegnoFieldAdded(false);
        if(type == FieldType.IMAGE) setAvatarFieldAdded(false);
        return fieldContainer.getChildren().remove(node.get());
    }
    
    /**
     * create a CertificateText object according to the field. and couples them together
     * hence synchronizing screen state and session state
     * @param field
     * @return 
     */
    public CertificateText createText(final CertificateField field) {
        CertificateText text = new CertificateText(field);
        text.setX(text.getX() - text.getWidth() / 2); // alignment
        // listeners
        EventHandler<MouseEvent> mouseHandler = getTextMouseHandler();
        text.setOnMousePressed(mouseHandler);
        text.setOnMouseDragged(mouseHandler);
        text.setOnMouseReleased(mouseHandler);
        field.observe(text); // bindings
        return text;
    }

    public CertificateAvatar createAvatarImage(final CertificateField field) {
        if(field == null) Debugger.log("[CertificateTab] ERROR : field is null for creating avatar..."); // debug
        Image image = createImage(field.getWidth(), field.getHeight());
        CertificateAvatar avatar = new CertificateAvatar(image);
        avatar.setX(field.getX().intValue());
        avatar.setY(field.getY().intValue());
//        Debugger.log("image coords : x" + x + " y" + y); // debug
        EventHandler<MouseEvent> mouseHandler = getAvatarMouseHandler();
        avatar.setOnMousePressed(mouseHandler);
        avatar.setOnMouseDragged(mouseHandler);
        avatar.setOnMouseReleased(mouseHandler);
        // as long as this is here we dont have to use two paramaters on addNode() method. eg. addNode(CertificateNode, CertificateField)
        field.observe(avatar); // bindings
        return avatar;
    }
    
    /**
     * Creates and returns a new fx Image object according to the width and height specified.
     * @param width
     * @param height
     * @return 
     */
    public Image createImage(Integer width, Integer height)  {
        Debugger.log("[CertificateTab] creating image with dimensions : " + width + "x" + height); // debug
        return new Image(getClass().getResourceAsStream("icons/avatarx1500.png"), width.doubleValue(), height.doubleValue(), false, true);
    }
    
    public void setCursorIcon(Cursor cursor) {
        
    }
    
    public void setCursonIconForNode(Cursor cursor) {
        
    }
    
    public void setCursorIconForText(Cursor cursor) {
        
    }
    
    public void setCursorIconForImage(Cursor cursor) {
        
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
