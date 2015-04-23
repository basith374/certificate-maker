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
import java.awt.GraphicsEnvironment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.xml.bind.UnmarshalException;

/**
 * This class is the main thing with over 1k LOC. technically speaking. however i think there are more comments than
 * code. Im thinking of breaking this apart into smaller pieces but not until this thing does what it says it
 * does. Feel free to do it if want to. :), NEW UPDATE : have broken it into several small pieces but it still
 * can be broken further.
 * Created by bazi on 22/3/15.
 */
public class Window  {

    /*
     * GLOBAL CONSTANTS
     */
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 500;
    private static final String COMPANY_NAME = "BCZ";
    private static final String APP_NAME = "Certificate Maker";

    private static Stage PRIMARY_STAGE;
    
    /*
     * ALLOW ONLY ONE INSTANCE OF DIALOG BOXES
     */
    private static NewTemplateDialog NEWTEMPLATE_DIALOG;
    private static LabelDialog LABEL_DIALOG;
    private static CreateCertificateDialog CREATECERTIFICATE_DIALOG;
    private static AvatarDialog AVATAR_DIALOG;
    private static AboutDialog ABOUT_DIALOG;
    private static LoadingBox LOADINGBOX;
    private static NewCertificateDialog NEWCERTIFICATE_DIALOG;
    private static FieldsDialog FIELDS_DIALOG;
    
    /*
     * MAIN WINDOW GUI COMPONENTS
     */
    private BorderPane borderPane;
    private static TabPane tabPane;
    
    /*
     * A SMALL OVERHEAD ADDED BY THE TERM 'OBJECT COMPOSITION OVER CLASS INHERITANCE'
     */
    public static HashMap<Tab, CertificateTab> tabMap = new HashMap<>();
    
    // getters only
    private ObservableList<String> fontFamilyList; // System specific
    private ObservableList<String> fontStyleList;
    private ObservableList<Integer> fontSizeList;
    
    private CertificateUtils certificateUtils;
    
    private ProgressBar progressBar;
    private Label statusLabel;
    private HBox statusBar;
    private Label messageLabel;
//    private boolean disallowmultiplefields = !UserDataManager.isMultipleFieldsAllowed();
    private Menu openRecentMenu;
    private BooleanProperty showProgressProperty;

    private void saveRecent(File file) {
        MenuItem item = new MenuItem(file.getAbsolutePath());
        String path = file.getAbsolutePath();
        boolean contains = false;
        for(MenuItem _item : openRecentMenu.getItems()){
            if(path.equals(_item.getText())) contains = true;
        }
        if(!contains) {
            openRecentMenu.getItems().add(item);
            Debugger.log("[Window] adding item to openRecentMenu : " + file.getAbsolutePath()); // debug
        } else {
            Debugger.log("[Window] item already in openRecentMenu : " + file.getAbsolutePath()); // debug
        }
    }

    private enum DIALOG_TYPE {
        OPEN,
        SAVE
    }

    /*
     * MOUSE MODES
     */
    public static final int MODE_ADD = 0;
    public static final int MODE_ADDIMAGE = 1;
    public static final int MODE_MOVE = 2;
    public static final int MODE_DELETE = 3;
    public static final int MODE_EDIT = 4;
    
    private enum MouseMode {
        ADD, ADD_IMAGE, MOVE, DELETE, EDIT
    }
    
    public static int getMouseMode() {
        return MOUSEMODE;
    }

    public static void setMouseMode(int mode) {
        MOUSEMODE = mode;
    }

    private static int MOUSEMODE;

//    
//    @Override
//    public void start(Stage primaryStage) {
//    }
//    
    public Window(){
        
        PRIMARY_STAGE = new Stage();
        
        ResourceManger.getInstance().loadAppResources();
        certificateUtils = new CertificateUtils();
        LOADINGBOX = new LoadingBox(PRIMARY_STAGE);
        
        PRIMARY_STAGE.getIcons().add(ResourceManger.getInstance().iconx48);
        setMouseMode(MODE_MOVE);
        PRIMARY_STAGE.setTitle("Certificate Maker BCZ.");
        Group root = new Group();
        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT, Color.WHITE);

        borderPane = new BorderPane();

        borderPane.setTop(getTopBars());
        borderPane.setLeft(getLeftBar());
        borderPane.setCenter(getTabPane());
        borderPane.setBottom(getStatusBar());

        borderPane.prefWidthProperty().bind(scene.widthProperty());
        borderPane.prefHeightProperty().bind(scene.heightProperty());

        root.getChildren().add(borderPane);

        PRIMARY_STAGE.setScene(scene);
//        PRIMARY_STAGE.show();
    }
    
    public void show() {
        PRIMARY_STAGE.show();
    }
    
    /******************************************
     *        GUI INITIALIZER METHODS         *
     ******************************************/
    
    /**
     * initialize and return the left vertical tool bar
     * @return 
     */
    private Node getLeftBar() {
        ToolBar toolBar = new ToolBar();

        final ToggleButton button1 = new ToggleButton(); // MOVE
        final ToggleButton button2 = new ToggleButton(); // insert IMAGE
        final ToggleButton button3 = new ToggleButton(); // insert TEXT
        final ToggleButton button4 = new ToggleButton(); // delete
        final ToggleButton button5 = new ToggleButton(); // edit
        final ToggleGroup group = new ToggleGroup();
        group.getToggles().addAll(button1, button2, button3, button4, button5);
        group.selectToggle(button1);
        // make one button selected at all times
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, final Toggle oldValue, Toggle newValue) {
                if((newValue == null)) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            group.selectToggle(oldValue);
                        }
                    });
                }
            }
        });

        button1.setGraphic(new ImageView(ResourceManger.getInstance().movex32));
        button2.setGraphic(new ImageView(ResourceManger.getInstance().addimgx32));
        button3.setGraphic(new ImageView(ResourceManger.getInstance().addx32));
        button4.setGraphic(new ImageView(ResourceManger.getInstance().delx32));
        button5.setGraphic(new ImageView(ResourceManger.getInstance().editx32));

        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ToggleButton button = (ToggleButton) event.getSource();
                if (button1.equals(button)) { // MOVE
                    MOUSEMODE = MODE_MOVE;
//                    setCursorIconForAllTextAtAllTab(Cursor.MOVE); // revert
//                    resetCursorIconForAllTab();
                } else if(button2.equals(button)) { // ADD IMAGE
                    MOUSEMODE = MODE_ADDIMAGE;
//                    setCursorIconForAllTab(Cursor.CROSSHAIR);
//                    setCursorIconForAllTextAtAllTab(Cursor.CROSSHAIR);
                } else if(button3.equals(button)) { // ADD TEXT
                    MOUSEMODE = MODE_ADD;
//                    setCursorIconForAllTab(Cursor.CROSSHAIR);
//                    setCursorIconForAllTextAtAllTab(Cursor.CROSSHAIR); // TODO remove moving capability while MODE = ADD
                } else if(button4.equals(button)) { // DELETE
                    MOUSEMODE = MODE_DELETE;
                    // TODO IMPORTANT set entry deletion mouse cursor
//                    setCursorIconForAllTextAtAllTab(Cursor.cursor(""));  // revert
//                    setCursorIconForAllTextAtAllTab(new ImageCursor(ResourceManger.getInstance().crossx1));
//                    resetCursorIconForAllTab();
                } else if(button5.equals(button)) { // EDIT
                    MOUSEMODE = MODE_EDIT;
//                    setCursorIconForAllTextAtAllTab(Cursor.TEXT);
//                    resetCursorIconForAllTab();
                }
            }
        };

        button1.setOnAction(eventHandler);
        button2.setOnAction(eventHandler);
        button3.setOnAction(eventHandler);
        button4.setOnAction(eventHandler);
        button5.setOnAction(eventHandler);

        button1.setTooltip(new Tooltip("Move"));
        button2.setTooltip(new Tooltip("Add Image"));
        button3.setTooltip(new Tooltip("Add Text"));
        button4.setTooltip(new Tooltip("Delete"));
        button5.setTooltip(new Tooltip("Edit"));

        toolBar.getItems().add(button1);
        toolBar.getItems().add(button2);
        toolBar.getItems().add(button3);
        toolBar.getItems().add(button4);
        toolBar.getItems().add(button5);


        toolBar.setOrientation(Orientation.VERTICAL);
        return toolBar;
    }

    /*
     * returns both menubar and toolbar, both wrapped into a gridpane
     */
    private VBox getTopBars() {
        VBox box = new VBox();
        box.getChildren().add(getMenuBar());
        box.getChildren().add(getToolBar());
        return box;
    }

    /**
     * method to initialized menu bar. called only once
     * @return 
     */
    private Node getMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(borderPane.widthProperty()); // TODO dependency

        // FILE MENU
        Menu fileMenu = new Menu("File");

        MenuItem newMenu = new MenuItem("New Template");
        MenuItem newCertificateMenu = new MenuItem("New Certificate");
        MenuItem openMenu = new MenuItem("Open Template");
        openRecentMenu = new Menu("Open Recent");
        MenuItem saveMenu = new MenuItem("Save Template");
        MenuItem saveAsMenu = new MenuItem("Save As Template");
        MenuItem exitMenu = new MenuItem("Exit");
        
        // mnemonic parsing
        newMenu.setMnemonicParsing(true);
        newCertificateMenu.setMnemonicParsing(true);
        openMenu.setMnemonicParsing(true);
        openRecentMenu.setMnemonicParsing(true);
        saveMenu.setMnemonicParsing(true);
        saveAsMenu.setMnemonicParsing(true);
        exitMenu.setMnemonicParsing(true);        
        
        fileMenu.getItems().addAll(
                newCertificateMenu,
                new SeparatorMenuItem(),
                newMenu,
                openMenu,
                openRecentMenu,
                saveMenu,
                saveAsMenu,
                exitMenu
        );
        
        EventHandler<ActionEvent> menuhandler = getMenuEventHandler();
        // action listeners
        newMenu.setOnAction(menuhandler);
        newCertificateMenu.setOnAction(menuhandler);
        openMenu.setOnAction(menuhandler);
        saveMenu.setOnAction(menuhandler);
        saveAsMenu.setOnAction(menuhandler);
        exitMenu.setOnAction(menuhandler);
        // icons
        newMenu.setGraphic(new ImageView(ResourceManger.getInstance().newtempx16));
        newCertificateMenu.setGraphic(new ImageView(ResourceManger.getInstance().newx16));
        openMenu.setGraphic(new ImageView(ResourceManger.getInstance().openex16));
        openRecentMenu.setGraphic(new ImageView(ResourceManger.getInstance().opentempx16));
        saveMenu.setGraphic(new ImageView(ResourceManger.getInstance().savex16));
        saveAsMenu.setGraphic(new ImageView(ResourceManger.getInstance().saveasx16));
        exitMenu.setGraphic(new ImageView(ResourceManger.getInstance().exitx16));
        // accelerators
        newMenu.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN));
        newCertificateMenu.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        openMenu.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        saveMenu.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        saveAsMenu.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN));
        exitMenu.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
        
        EventHandler<ActionEvent> recentHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                MenuItem source = (MenuItem) t.getSource();
                File file = new File(source.getText());
                Debugger.log("[Window] Opening file : " + file.getAbsolutePath()); // debug
                if(file.exists()) {
                    int index = getTabIndex(file);
                    if(index == -1) {
                        openTemplateInGui(file);
                        tabPane.getSelectionModel().select(index);
                    } else {
                        tabPane.getSelectionModel().select(index);
                    }
                } else {
                    Debugger.log("file doesnt exists. removing entry..."); // debug
                    openRecentMenu.getItems().remove(source);
                }
            }
        };
        List<String> recent = UserDataManager.getRecentTemplates(); // TODO make global
        if(recent != null) {
            Debugger.log("[Window] Loading recent templates"); // debug
            for(String path : recent) {
                MenuItem recentMenu = new MenuItem(path);
                recentMenu.setOnAction(recentHandler);
                openRecentMenu.getItems().add(recentMenu);
            }
        } else {
            Debugger.log("[Window] No recent templates"); // debug
            MenuItem empty = new MenuItem("No recent files");
            empty.setDisable(true);
            openRecentMenu.getItems().add(empty);
        }
        openRecentMenu.getItems().addListener(new ListChangeListener<MenuItem>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends MenuItem> change) {
                ObservableList<? extends MenuItem> menuItems = change.getList();
                List<String> list = new ArrayList<>();
                for(MenuItem item : menuItems) {
                    list.add(item.getText());    
                }
                UserDataManager.setRecentTemplates(list);
            }
        });
        
        menuBar.getMenus().add(fileMenu);

        // EDIT MENU
        Menu editMenu = new Menu("Edit");
        
        final MenuItem undoMenu = new MenuItem("Undo");
        final MenuItem redoMenu = new MenuItem("Redo");
        MenuItem fieldsMenu = new MenuItem("Fields");
        
        
        undoMenu.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        redoMenu.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
        
        EventHandler<ActionEvent> a = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                Tab tab = tabPane.getSelectionModel().getSelectedItem();
                CertificateTab ct = tabMap.get(tab);
                MenuItem source = (MenuItem) t.getSource();
                if(ct != null) {
                    CommandManager manager = ct.getCommandManager();
                    if(source.equals(undoMenu)) {
//                        Debugger.log("undoing");
                        manager.undo();
                    } else {
                        // redo
//                        Debugger.log("redoing");
                        manager.redo();
                    }
                } else {
                    Debugger.log("certificate tab is null");
                }
            }
        };
        undoMenu.setOnAction(a);
        redoMenu.setOnAction(a);
        fieldsMenu.setOnAction(menuhandler);
        
        editMenu.getItems().addAll(undoMenu, redoMenu); // removed fields menu
//        editMenu.getItems().addAll(undoMenu, redoMenu, new SeparatorMenuItem(), fieldsMenu);
        menuBar.getMenus().add(editMenu);
        
        // OUTPUT MENU
        Menu outputMenu = new Menu("Output");
        
        
        CheckMenuItem a3outputMenu = new CheckMenuItem("A3 Output");
        a3outputMenu.setSelected(UserDataManager.isA3Output());
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioMenuItem jpgMenu = RadioMenuItemBuilder.create()
                .text("JPG Format")
                .toggleGroup(toggleGroup)
                .build();
        RadioMenuItem pngMenu = RadioMenuItemBuilder.create()
                .text("PNG Format")
                .toggleGroup(toggleGroup)
                .build();
        if("jpg".equalsIgnoreCase(UserDataManager.getDefaultImageFormat())) {
            jpgMenu.setSelected(true);
        } else {
            pngMenu.setSelected(true);
        }
        
        
        a3outputMenu.setOnAction(menuhandler);
        jpgMenu.setOnAction(menuhandler);
        pngMenu.setOnAction(menuhandler);
        
        outputMenu.getItems().add(a3outputMenu);
        outputMenu.getItems().add(new SeparatorMenuItem());
        outputMenu.getItems().add(jpgMenu);
        outputMenu.getItems().add(pngMenu);
        
        menuBar.getMenus().add(outputMenu);
        
        // HELP MENU
        Menu helpMenu = new Menu("Help");
        
        MenuItem aboutMenu = new MenuItem("About");
        aboutMenu.setGraphic(new ImageView(ResourceManger.getInstance().iconx16));
        helpMenu.getItems().add(aboutMenu);
        aboutMenu.setOnAction(menuhandler);
        
        menuBar.getMenus().add(helpMenu);

        // finish
        return menuBar;
    }

    /**
     * get event handler for menubar items
     * @return 
     */
    private EventHandler<ActionEvent> getMenuEventHandler() {
        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                MenuItem mItem = (MenuItem) event.getSource();
                String action = mItem.getText();
                if ("new template".equalsIgnoreCase(action)) {
                    if (NEWTEMPLATE_DIALOG == null) {
                        NEWTEMPLATE_DIALOG = new NewTemplateDialog(PRIMARY_STAGE, Window.this);
                    }
                    NEWTEMPLATE_DIALOG.show();
                } else if ("new certificate".equalsIgnoreCase(action)) {
                    if(NEWCERTIFICATE_DIALOG == null) {
                        NEWCERTIFICATE_DIALOG = new NewCertificateDialog(PRIMARY_STAGE, Window.this);
                    }
                    NEWCERTIFICATE_DIALOG.show();
                } else if ("open template".equalsIgnoreCase(action)) {
                    openTemplateByDialog(PRIMARY_STAGE);
                } else if ("save template".equalsIgnoreCase(action)) {
                    saveFile(PRIMARY_STAGE);
                } else if ("save as template".equalsIgnoreCase(action)) {
                    saveAsFile(PRIMARY_STAGE);
                } else if ("exit".equalsIgnoreCase(action)) {
                    shutdown();
                } else if ("about".equalsIgnoreCase(action)) {
//                    showCreator();
//                    showTheRealThing();
//                    showThemWhatBCZisReallyAbout();
//                    blowupcomputer();
                    if(ABOUT_DIALOG == null) {
                        ABOUT_DIALOG = new AboutDialog(PRIMARY_STAGE);
                    }
                    ABOUT_DIALOG.show();
                } else if("a3 output".equalsIgnoreCase(action)) {
//                    Debugger.log("a3output " + a3outputMenu.isSelected()); // debug
                    CheckMenuItem checkMenuItem = (CheckMenuItem) event.getSource();
                    UserDataManager.setA3Output(checkMenuItem.isSelected());
                } else if("jpg format".equalsIgnoreCase(action)) {
                    UserDataManager.setDefaultImageFormat("jpg");
                } else if("png format".equalsIgnoreCase(action)) {
                    UserDataManager.setDefaultImageFormat("png");
                } else if("fields".equalsIgnoreCase(action)) {
                    if(FIELDS_DIALOG == null) {
                        FIELDS_DIALOG = new FieldsDialog(PRIMARY_STAGE);
                    }
                    Tab tab = tabPane.getSelectionModel().getSelectedItem();
                    CertificateTab ct = tabMap.get(tab);
                    if(ct != null) {
                        FIELDS_DIALOG.showDialog(ct.createNodeList());
                    } else {
                        Debugger.log("tab is null...");
                    }
                }
            }
        };
    }
    
    /**
     * Exit the application
     */
    private void shutdown() {
        // TODO before shutdown actions
//        System.exit(0); // not good
        Debugger.log("Bye...");
        Platform.exit();
    }
    
    /**
     * initializes and returns the tab pane
     * @return 
     */
    private Node getTabPane() {
        tabPane = new TabPane();
        // status label changing listeners
//        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
//            @Override
//            public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
//                CertificateTab tab = (CertificateTab) ov.getValue();
//                if(tab != null) {
//                    File imageFile = tab.getCertificateWrapper().getCertificateImage(); // null pointer
//                    statusLabel.setText(imageFile.getName() + "(" + convertToStringSize(imageFile.length()) + ")");
//                }
//                Debugger.log("tab selection changed");
//            }
//        });
//        tabPane.setTabClosingPolicy(); // TODO tab close combobox reset
        return tabPane;
    }

    /**
     * initializes and returns the toolbar(top)
     * @return 
     */
    private ToolBar getToolBar() {
        // TOOL BAR

        ToolBar toolBar = new ToolBar();

        Button newCertificateBtn = new Button("New Certificate", new ImageView(ResourceManger.getInstance().newx16));
        toolBar.getItems().add(newCertificateBtn);
        newCertificateBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                org.bluecipherz.certificatemaker.CertificateWrapper certificateWrapper = retrieveWrapper(recentTemplatesBox.getSelectionModel().getSelectedItem().toString());
//                createNewCertficateDialog(PRIMARY_STAGE, certificateWrapper);
                openCreateCertificateDialog();
            }
        });

        // seperator
        Separator separator = new Separator(Orientation.VERTICAL);
        toolBar.getItems().add(separator);

        Label fontsLbl = new Label("Default Font : ");
        fontFamilyList = FXCollections.observableArrayList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()
        );
//        ComboBox templates = new ComboBox(templateList);
        final ComboBox<String> systemFonts = new ComboBox<>(fontFamilyList);
        // reduce load on gui
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
//                String fontFamily = UserDataManager.getDefaultFontFamily();
                String fontFamily = UserDataManager.getDefaultFontFamily();
                if(fontFamily != null) {
                    if(systemFonts.getItems().contains(fontFamily)) systemFonts.getSelectionModel().select(fontFamily);
                    else systemFonts.getSelectionModel().select(0);
                } else {
                    systemFonts.getSelectionModel().select(0);
                }
            }
        });
        
        systemFonts.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                Debugger.log("Changed");
                UserDataManager.setDefaultFontFamily(systemFonts.getSelectionModel().getSelectedItem().toString());
            }
        });

        fontSizeList = FXCollections.observableArrayList(
                8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26,
                27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50,
                51, 52, 53, 54, 56, 57, 58, 59, 60, 61, 62, 63, 64
        );
        final ComboBox<Integer> fontSizes = new ComboBox<>(fontSizeList);
        // reduce load on gui
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Integer fontSize = Integer.parseInt(UserDataManager.getDefaultFontSize());
                fontSizes.getSelectionModel().select(fontSize);
            }
        });
        fontSizes.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                Debugger.log("changed");
                UserDataManager.setDefaultFontSize(fontSizes.getSelectionModel().getSelectedItem().toString());
            }
        });

        fontStyleList = FXCollections.observableArrayList(
                "Regular", "Bold"
        );
        final ComboBox<String> fontStyles = new ComboBox<>(fontStyleList);
        // reduce load on gui
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                String fontStyle = UserDataManager.getDefaultFontStyle();
                if(fontStyle != null) {
                    fontStyles.getSelectionModel().select(fontStyle);
                } else {
                    fontStyles.getSelectionModel().select(0);
                }
            }
        });
        fontStyles.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                Debugger.log("changed");
                UserDataManager.setDefaultFontStyle(fontStyles.getSelectionModel().getSelectedItem());
            }
        });

        toolBar.getItems().add(fontsLbl);
        toolBar.getItems().add(systemFonts);
        toolBar.getItems().add(fontSizes);
        toolBar.getItems().add(fontStyles);

        // set defaults
        String fontFamily = UserDataManager.getDefaultFontFamily();
        if (fontFamily != null) {
            systemFonts.getSelectionModel().select(fontFamilyList.indexOf(fontFamily));
        }
        String fontSize = UserDataManager.getDefaultFontSize();
        if (fontSize != null) {
            fontSizes.getSelectionModel().select(fontSizeList.indexOf(Integer.valueOf(fontSize)));
        }
        String fontStyle = UserDataManager.getDefaultFontStyle();
        if (fontStyle != null) {
            fontStyles.getSelectionModel().select(fontStyleList.indexOf(fontStyle));
        }

        return toolBar;
    }

    /*
     * STATUS BAR
     */
    
    private Node getStatusBar() {
        statusBar = new HBox();
        statusBar.setStyle("-fx-background-color: gainsboro;");
//        statusBar.setStyle("-fx-background-color: linear-gradient(to bottom, -fx-base, derive(-fx-base, 100%));");
        statusBar.setPadding(new Insets(2));
        statusBar.setSpacing(2);
        statusBar.setAlignment(Pos.CENTER_LEFT);
        
        progressBar = new ProgressBar();
        showProgressProperty = new SimpleBooleanProperty(false);
        showProgressProperty.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                if(t1.booleanValue()) {
                    hideProgressBar();
                } else {
                    showProgressBar();
                }
            }
        });
        statusLabel = new Label("Status");
        statusBar.getChildren().add(statusLabel);
        statusBar.getChildren().add(new Separator(Orientation.VERTICAL));
        messageLabel = new Label();
        
        return statusBar;
    }
    /*** END GUI INITIALIZER METHODS ***/
    
    public Label getMessageLabel() {
        return messageLabel;
    }
        
    public void showProgressBar() {
        statusBar.getChildren().add(0, progressBar);
    }
    
    public ProgressBar getProgressBar() {
        return progressBar;
    }
    
    public void hideProgressBar() {
        statusBar.getChildren().remove(progressBar);
    }

    public Label getStatusLabel() {
        return statusLabel;
    }
    
    public BooleanProperty showProgressProperty() {
        return showProgressProperty;
    }
    
    /*** cursor related methods ***/
//    private void setCursorIconForAllTextAtTab(Tab tab, Cursor imageCursor) {
//        Group group = ((CertificateTab)tab).getFieldContainer();
//        for (Node node : group.getChildren()) {
//            if (node instanceof CertificateText) {
//                node.setCursor(imageCursor);
//            }
//        }
//    }
//
//    private void setCursorIconForTab(Tab tab, Cursor cursor) {
//        Group group = ((CertificateTab)tab).getFieldContainer();
//        group.setCursor(Cursor.CROSSHAIR);
//    }

//    private void setCursorIconForAllTextAtAllTab(Cursor cursor) {
//        for (Tab tab : tabPane.getTabs()) {
//            setCursorIconForAllTextAtTab(tab, cursor);
//        }
//    }
//
//    private void setCursorIconForAllTab(Cursor cursor) {
//        for (Tab tab : tabPane.getTabs()) {
//            setCursorIconForTab(tab, cursor);
//        }
//    }
//
//    private void resetCursorIconForAllTextAtTab(Tab tab) {
//        Group group = ((CertificateTab)tab).getFieldContainer();
//        for (Node node : group.getChildren()) {
//            if (node instanceof CertificateText) {
//                node.setCursor(Cursor.DEFAULT);
//            }
//        }
//    }
//
//    private void resetCursorIconForTab(Tab tab) {
//        Group group = ((CertificateTab)tab).getFieldContainer();
//        group.setCursor(Cursor.DEFAULT);
//    }

//    private void resetCursorIconForAllTextAtAllTab() {
//        for (Tab tab : tabPane.getTabs()) {
//            resetCursorIconForAllTextAtTab(tab);
//        }
//    }
//
//    private void resetCursorIconForAllTab() {
//        for (Tab tab : tabPane.getTabs()) {
//            resetCursorIconForTab(tab);
//        }
//    }
    // end cursor methods
    
    
    
    /**
     * 
     * @return 
     */
    private ObservableList getRecentTemplates() {
//        Gson a = new Gson();
        return null;
    }

    /*************************
     *  HIGH LEVEL METHODS   *
     ************************/

    /**
     * Top Level "Save" method
     * @param stage
     * @param index
     */
    private void saveFile(Stage stage) {
//        CertificateWrapper wrapper = certificateList.get(index);
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        CertificateTab ct = tabMap.get(tab);
        int index = tabPane.getSelectionModel().getSelectedIndex();
        if(index != -1) {// do the rest
            if (ct.getFile() == null) {
                File file = getFileByDialog(stage, DIALOG_TYPE.SAVE, "Save template");
                certificateUtils.saveFileAtTab(ct, file);
                ct.setFile(file); // TODO change filepath to tabs
                // TODO save recent
                saveRecent(file);
            } else {
                Debugger.log("Saving file : " + ct.getFile().getAbsolutePath()); // DEBUG
                certificateUtils.saveFileAtTab(ct, ct.getFile());
            }
        } else {
            Alert.showAlertError(PRIMARY_STAGE, "Error", "Nothing to save");
        }
    }

    /**
     * Top level "Save As" method.
     * @param stage
     * @param index
     */
    private void saveAsFile(Stage stage) {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        CertificateTab ct = tabMap.get(tab);
        int index = tabPane.getSelectionModel().getSelectedIndex();
        if(index != -1){
            File file = getFileByDialog(PRIMARY_STAGE, DIALOG_TYPE.SAVE, "Save template");
            certificateUtils.saveFileAtTab(ct, file);
        } else {
            Alert.showAlertError(PRIMARY_STAGE, "Error", "Nothing to save");
        }
    }

    /**
     * Middle level interface between getFileByDialog and openFileInGui. Acts as a mediator
     * to conduct file opening in a uniform way using these two methods.
     * @param stage
     */
    private void openTemplateByDialog(Stage stage) {
        File file = getFileByDialog(PRIMARY_STAGE, DIALOG_TYPE.OPEN, "Open certificate template");
        if (file != null) {
            openTemplateInGui(file);
        } else {
            // debug
        }
    }

    /**
     * Multi purpose dialog method. use file chooser to open or save certificate image or templates.
     * @param stage
     * @param dialog_type
     * @param title
     * @return
     */
    private File getFileByDialog(Stage stage, DIALOG_TYPE dialog_type, String title) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.setTitle(title);
        File lastActivityPath = UserDataManager.getLastActivityPath();
        if(lastActivityPath != null) fileChooser.setInitialDirectory(lastActivityPath);
        File file = null;
        if(dialog_type == DIALOG_TYPE.OPEN) {
            file = fileChooser.showOpenDialog(stage);
//            Debugger.log("opening file by dialog"); // debug
        } else if(dialog_type == DIALOG_TYPE.SAVE) {
//            Debugger.log("saving file by dialog"); // debug
            file = fileChooser.showSaveDialog(stage);
//            Debugger.log("previois path : " + file.getAbsolutePath()); // debug
            file = certificateUtils.correctXmlExtension(file);
//            Debugger.log("current path : " + file.getAbsolutePath()); // debug
        }
        return file;
    }
    
    /**
     * Opens certificate template in a new tab and saves the path for future file chooser use.
     * @param file
     */
    private CertificateTab openTemplateInGui(File file) {
        try {
            // TODO save recent
            saveRecent(file);
            CertificateTab tab = createNewTab(file);
            // save the file path to the registry
            UserDataManager.setLastActivityPath(file);
            UserDataManager.setCertificateFilePath(file); // TODO broken, no usage yet
            return tab;
        } catch (UnmarshalException ex) {
            Alert.showAlertError(PRIMARY_STAGE, "Error", "The specified file is not recognized as a template file. Please try another one.");            
        } catch (NullPointerException ex) {
            Alert.showAlertError(PRIMARY_STAGE, "Error", "There seems to be a problem with the template xml file.\nEither it was used by an older version of Certificate Maker or it is not a template file.");
        }catch (OutOfMemoryError ex) {
            Alert.showAlertError(PRIMARY_STAGE, "Error", "The image file size is too much");
        } catch (Exception ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex); // java.util.logging
//            Alert.showAlertError(PRIMARY_STAGE, "Error", "An unknown error occured. Please contact BCZ.");
            Alert.showAlertError(PRIMARY_STAGE, "Error", ex.toString());
        }
        return null;
    }

    /********* END HIGH LEVEL METHODS *********/
    
    public boolean isOpenedInGui(File file) {
        boolean opened = false;
        for(Tab tab : tabPane.getTabs()) {
            CertificateTab ct = tabMap.get(tab);
            File openedFile = ct.getFile();
            if(openedFile.equals(file)) opened = true;
        }
        return opened;
    }
    
    private int getTabIndex(File file) {
        int index = -1;
        for(Tab tab : tabPane.getTabs()) {
            CertificateTab ct = tabMap.get(tab);
            File _file = ct.getFile();
            if(file.equals(_file)) index = tabPane.getTabs().indexOf(tab);
        }
        return index;
    }
    
    public boolean selectTab(File file) {
        tabPane.getSelectionModel().select(getTabIndex(file));
        int index = getTabIndex(file);
        if(index != -1) {
            tabPane.getSelectionModel().select(index);
            openCreateCertificateDialog();
            return true;
        } else {
            CertificateTab tab = openTemplateInGui(file);
            tabPane.getSelectionModel().select(tab.get());
            openCreateCertificateDialog();
            return false;
        }
    }
    
    public CertificateTab getSelectedTab() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        return tabMap.get(tab);
    }    
    
    /*************************
     * OTHER PUBLIC METHODS
     ************************/
        
    public ObservableList<String> getFontStyleList() {
        return fontStyleList;
    }

    public ObservableList<Integer> getFontSizeList() {
        return fontSizeList;
    }

    public ObservableList<String> getFontFamilyList() {
        return fontFamilyList;
    }
    

    public CertificateTab createNewTab(final File file) throws Exception {
        if(CertificateTab.getPRIMARY_STAGE() == null) CertificateTab.setPRIMARY_STAGE(PRIMARY_STAGE);
        if(CertificateTab.getWINDOW() == null) CertificateTab.setWINDOW(this);
        // unmarshall xml file to wrapper
        CertificateWrapper wrapper = certificateUtils.openTemplate(file);
        // pass the unmarshaller wrapper to the tab
        CertificateTab tab = new CertificateTab(wrapper);
        LOADINGBOX.showProgressing(tab.loadImage()); // show progress indicator while loading image
        tabPane.getTabs().add(tab.get()); // add the tab to tabpane
        tabMap.put(tab.get(), tab); // new implementation
        tab.setFile(file); // for resaving file later
        return tab;
    }
    
    public CertificateTab createNewTab(CertificateWrapper wrapper) {
        if(CertificateTab.getPRIMARY_STAGE() == null) CertificateTab.setPRIMARY_STAGE(PRIMARY_STAGE);
        if(CertificateTab.getWINDOW() == null) CertificateTab.setWINDOW(this);
        //tab children heirarchy
        //tab -> scrollpane -> group -> imageview,text
        CertificateTab tab = new CertificateTab(wrapper);
        LOADINGBOX.showProgressing(tab.loadImage());
        tabPane.getTabs().add(tab.get());
        tabMap.put(tab.get(), tab); // new implementation
        return tab;
    }
    
    public void showEditAvatarDialog(CertificateTab tab, CertificateAvatar imageView) {
        if(AVATAR_DIALOG == null) {
            AVATAR_DIALOG = new AvatarDialog(PRIMARY_STAGE, Window.this);
        }
        AVATAR_DIALOG.setImageHolder(tab);
        AVATAR_DIALOG.editImage(imageView);
    }
    
    public void showAvatarAddDialog(CertificateTab tab, Point2D point) {
        if(AVATAR_DIALOG == null) {
            AVATAR_DIALOG = new AvatarDialog(PRIMARY_STAGE, Window.this);
        }
        AVATAR_DIALOG.setImageHolder(tab); // possible bug fix
        AVATAR_DIALOG.newImage(point);
    }
    
    public void addAvatar(CertificateTab tab, Point2D start, Point2D end) {
        if(AVATAR_DIALOG == null) {
            AVATAR_DIALOG = new AvatarDialog(PRIMARY_STAGE, Window.this);
        }
        AVATAR_DIALOG.setImageHolder(tab);
        
        int x = (int) start.getX();
        int y = (int) start.getY();
        if(start.getX() > end.getX()) x = (int) end.getX();
        if(start.getY() > end.getY()) y = (int) end.getY();
        int width = (int) Math.abs(start.getX() - end.getX());
        int height = (int) Math.abs(start.getY() - end.getY());
        AVATAR_DIALOG.newImage(new Point2D(x, y), width, height);
    }
    
    public void showNewFieldDialog(CertificateTab tab, Point2D point) {
        if(LABEL_DIALOG == null) {
            LABEL_DIALOG = new LabelDialog(PRIMARY_STAGE, Window.this);
        }
        LABEL_DIALOG.prepareAndShowNewTextDialog(tab, point);
    }
    
    public void showEditFieldDialog(CertificateTab tab, CertificateText text) {
        if(LABEL_DIALOG == null) {
            LABEL_DIALOG = new LabelDialog(PRIMARY_STAGE, Window.this);
        }
        LABEL_DIALOG.prepareAndShowEditTextDialog(tab, text);
    }

    
    public void openCreateCertificateDialog() {
        Tab tab = tabPane.getSelectionModel().getSelectedItem();
        CertificateTab ct = tabMap.get(tab);
        if(ct != null){
//            CertificateWrapper wrapper = ct.getSerializableWrapper();
            CertificateWrapper wrapper = ct.getDisplayableWrapper();
            if(CREATECERTIFICATE_DIALOG == null) {
                CREATECERTIFICATE_DIALOG = new CreateCertificateDialog(PRIMARY_STAGE, Window.this);
            }
//            LOADINGBOX.showProgressing(CREATECERTIFICATE_DIALOG.populatingProgressProperty());
            CREATECERTIFICATE_DIALOG.openFor(wrapper);
        } else {
            Debugger.log("[Window] unknown condition : tab index out of bounds"); // debug
            Alert.showAlertError(PRIMARY_STAGE, "Error", "No opened templates");
        }
    }
    
    
//    public static void main(String[] args) { launch(args); } // this was first the application class
    
}
