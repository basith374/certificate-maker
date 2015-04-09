package org.bluecipherz.certificatemaker;
//import com.google.gson.Gson;
import com.sun.deploy.resources.ResourceManager;
import java.awt.GraphicsEnvironment;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.scene.*;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;

/**
 * This class is the main thing with over 1k LOC. technically speaking. however i think there are more comments than
 * code. Im thinking of breaking this apart into smaller pieces but not until this thing does what it says it
 * does. Feel free to do it if want to. :)
 * Created by bazi on 22/3/15.
 */
public class Window  {

    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 500;

    private static Stage PRIMARY_STAGE;
    
    private static NewFileDialog NEWFILE_DIALOG;
    private static LabelDialog LABEL_DIALOG;
    private static NewCertificateDialog NEWCERTIFICATE_DIALOG;
    private static ImageSizeDialog IMAGESIZEDIALOG;
    private static AboutDialog ABOUT_DIALOG;
    private static LoadingBox LOADING;
    
    private BorderPane borderPane;
    private static TabPane tabPane;
    
    // getters only
    private ObservableList<String> fontFamilyList; // System specific
    private ObservableList<String> fontStyleList;
    private ObservableList<Integer> fontSizeList;
    
    private ComboBox recentTemplatesBox; // recent templates holder
    private CertificateUtils certificateUtils;
    
    private ProgressBar progressBar;
    private Label statusLabel;
    private HBox statusBar;
    private Label messageLabel;

    private TextField getTextField(FileChooser fileChooser) {
        LinkedList<Object> queue = new LinkedList<>();
        queue.add(fileChooser);
        TextField tf = new TextField();
        // no idea
        return null;
    }
    

    private enum DIALOG_TYPE {
        OPEN,
        SAVE
    }

    public static MouseMode getMouseMode() {
        return MOUSEMODE;
    }

    public static void setMouseMode(MouseMode mode) {
        MOUSEMODE = mode;
    }

    private static MouseMode MOUSEMODE;

//    
//    @Override
//    public void start(Stage primaryStage) {
//    }
//    
    public Window(){
        
        PRIMARY_STAGE = new Stage();
        
        ResourceManger.getInstance().loadAppResources();
        certificateUtils = new CertificateUtils();
        LOADING = new LoadingBox(PRIMARY_STAGE);
        
        PRIMARY_STAGE.getIcons().add(ResourceManger.getInstance().iconx48);
        setMouseMode(MouseMode.MOVE);
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
                    MOUSEMODE = MouseMode.MOVE;
                    setCursorIconForAllTextAtAllTab(Cursor.MOVE); // revert
                    resetCursorIconForAllTab();
                } else if(button2.equals(button)) { // ADD IMAGE
                    MOUSEMODE = MouseMode.ADD_IMAGE;
                    setCursorIconForAllTab(Cursor.CROSSHAIR);
                    setCursorIconForAllTextAtAllTab(Cursor.CROSSHAIR);
                } else if(button3.equals(button)) { // ADD TEXT
                    MOUSEMODE = MouseMode.ADD;
                    setCursorIconForAllTab(Cursor.CROSSHAIR);
                    setCursorIconForAllTextAtAllTab(Cursor.CROSSHAIR); // TODO remove moving capability while MODE = ADD
                } else if(button4.equals(button)) { // DELETE
                    MOUSEMODE = MouseMode.DELETE;
                    // TODO IMPORTANT set entry deletion mouse cursor
//                    setCursorIconForAllTextAtAllTab(Cursor.cursor(""));  // revert
                    resetCursorIconForAllTab();
                } else if(button5.equals(button)) { // EDIT
                    MOUSEMODE = MouseMode.EDIT;
                    setCursorIconForAllTextAtAllTab(Cursor.TEXT);
                    resetCursorIconForAllTab();
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

//        button1.setGraphic(new ImageView(new File("icon/movex32.png").toURI().toString()));
//        button2.setGraphic(new ImageView(new File("icon/addimgx32.png").toURI().toString()));
//        button3.setGraphic(new ImageView(new File("icon/addx32.png").toURI().toString()));
//        button4.setGraphic(new ImageView(new File("icon/delx32.png").toURI().toString()));
//        button5.setGraphic(new ImageView(new File("icon/edit32.png").toURI().toString()));
//        System.out.println("Icons set");

        // icons
//        button1.set

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
    private GridPane getTopBars() {
        GridPane gridPane = new GridPane();
        gridPane.add(getMenuBar(), 0, 0);
        gridPane.add(getToolBar(), 0, 1);
        return gridPane;
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
        MenuItem saveMenu = new MenuItem("Save Template");
        MenuItem saveAsMenu = new MenuItem("Save As Template");
        MenuItem exitMenu = new MenuItem("Exit");

        fileMenu.getItems().add(newCertificateMenu);
        fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(newMenu);
        fileMenu.getItems().add(openMenu);
        fileMenu.getItems().add(saveMenu);
        fileMenu.getItems().add(saveAsMenu);
        fileMenu.getItems().add(exitMenu);
        
        EventHandler<ActionEvent> handler = getMenuEventHandler();
        
        newMenu.setOnAction(handler); // dependency
        newCertificateMenu.setOnAction(handler); // dependency
        openMenu.setOnAction(handler); // dependency
        saveMenu.setOnAction(handler); // dependency
        saveAsMenu.setOnAction(handler); // dependency
        exitMenu.setOnAction(handler); // dependency

        // icons
//        newMenu.setGraphic();
//        if (new File("icons/newtempx16.png").exists()) {
//            System.out.println("ookay");
//        }
        
        newMenu.setGraphic(new ImageView(ResourceManger.getInstance().newtempx16));
        newCertificateMenu.setGraphic(new ImageView(ResourceManger.getInstance().newx16));
        openMenu.setGraphic(new ImageView(ResourceManger.getInstance().opentempx16));
        saveMenu.setGraphic(new ImageView(ResourceManger.getInstance().savex16));
        saveAsMenu.setGraphic(new ImageView(ResourceManger.getInstance().saveasx16));
        exitMenu.setGraphic(new ImageView(ResourceManger.getInstance().exitx16));
//        System.out.println(new File("icons/newtempx16.png").getAbsolutePath());
//        new Image(new File("icons/newtempx16.png").getAbsolutePath());
//        new ImageView(new File("icons/newtempx16.png").getAbsolutePath());
        menuBar.getMenus().add(fileMenu);

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
        
        
        a3outputMenu.setOnAction(handler);
        jpgMenu.setOnAction(handler);
        pngMenu.setOnAction(handler);
        
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
        aboutMenu.setOnAction(handler);
        
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
                    if (NEWFILE_DIALOG == null) {
                        NEWFILE_DIALOG = createNewfileDialog(PRIMARY_STAGE);
                    }
                    NEWFILE_DIALOG.sizeToScene();
                    NEWFILE_DIALOG.show();
                } else if ("new certificate".equalsIgnoreCase(action)) {
                    File file = getFileByDialog(PRIMARY_STAGE, DIALOG_TYPE.OPEN, "Set template for certificate", null);
                    if(file != null) {
                        CertificateWrapper certificateWrapper = certificateUtils.openTemplate(file);
                        if(!isOpenedInGui(file)) {
                            openTemplateInGui(file);
                            tabPane.getSelectionModel().select(getTabIndex(file));
                        }
                        else tabPane.getSelectionModel().select(getTabIndex(file)); // select tab containing file if not selected
                        
                        // end
                        if(NEWCERTIFICATE_DIALOG == null) {
                            NEWCERTIFICATE_DIALOG = new NewCertificateDialog(PRIMARY_STAGE, Window.this);
                        }
                        NEWCERTIFICATE_DIALOG.openFor(certificateWrapper);
                    }
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
//                    System.out.println("a3output " + a3outputMenu.isSelected()); // debug
                    CheckMenuItem checkMenuItem = (CheckMenuItem) event.getSource();
                    UserDataManager.setA3Output(checkMenuItem.isSelected());
                } else if("jpg format".equalsIgnoreCase(action)) {
                    UserDataManager.setDefaultImageFormat("jpg");
                } else if("png format".equalsIgnoreCase(action)) {
                    UserDataManager.setDefaultImageFormat("png");
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
        System.out.println("Bye...");
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
//                System.out.println("tab selection changed");
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


        Label recentTemplatesLbl = new Label("Recent templates :");
        toolBar.getItems().add(recentTemplatesLbl);
        
        List<String> recent = UserDataManager.getRecentTemplates();
//        ObservableList<String> recentFiles = FXCollections.observableArrayList(UserDataManager.getRecentTemplates()); // null pointer
        // fetch recent files and add them to the combobox
        if(recent != null) {
            System.out.println("Loading recent templates"); // debug
            recentTemplatesBox = new ComboBox(FXCollections.observableArrayList(recent));
        } else {
            System.out.println("No recent templates"); // debug
            recentTemplatesBox = new ComboBox();
        }
//        final ComboBox recentTemplatesBox = new ComboBox(getRecentTemplates()); // null pointer
        recentTemplatesBox.setOnAction(new EventHandler() {
            @Override
            public void handle(Event t) {
                System.out.println("recent box selected"); // debug
                File file = new File(((ComboBox)t.getSource()).getSelectionModel().getSelectedItem().toString());
                System.out.println("File : " + file.getAbsolutePath()); // debug
                if(!isOpenedInGui(file))
                    if(file.exists()) {
                        openTemplateInGui(file);
                        tabPane.getSelectionModel().select(getTabIndex(file));
                    } else {
                        System.out.println("file doesnt exists. removing entry...");
                        ((ComboBox)t.getSource()).getItems().remove(file.getAbsolutePath());
                    }
                else
                    // select tab containing file if not selected
                    tabPane.getSelectionModel().select(getTabIndex(file));
            }
        });
        // event listeners for automatic data update
        recentTemplatesBox.getItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                System.out.println("recent items updated"); // debug
                UserDataManager.setRecentTemplates(change.getList());
            }
        });
        if(!recentTemplatesBox.getItems().contains("")) recentTemplatesBox.getItems().add(0, "");
        toolBar.getItems().add(recentTemplatesBox);

        Button newCertificateBtn = new Button("New Certificate", new ImageView(ResourceManger.getInstance().newx16));
        toolBar.getItems().add(newCertificateBtn);
        newCertificateBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                org.bluecipherz.certificatemaker.CertificateWrapper certificateWrapper = retrieveWrapper(recentTemplatesBox.getSelectionModel().getSelectedItem().toString());
//                createNewCertficateDialog(PRIMARY_STAGE, certificateWrapper);
                int index = tabPane.getSelectionModel().getSelectedIndex();
                if(index != -1){
                    CertificateWrapper wrapper = ((CertificateTab)tabPane.getSelectionModel().getSelectedItem()).getCertificateWrapper();
                    if(NEWCERTIFICATE_DIALOG == null) {
                        NEWCERTIFICATE_DIALOG = new NewCertificateDialog(PRIMARY_STAGE, Window.this);
                    }
                    NEWCERTIFICATE_DIALOG.openFor(wrapper);
                } else {
                    System.out.println("unknown condition : tab index out of bounds"); // debug
                }
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
                    if(systemFonts.getItems().contains(this)) systemFonts.getSelectionModel().select(fontFamily);
                    else systemFonts.getSelectionModel().select(0);
                } else {
                    systemFonts.getSelectionModel().select(0);
                }
            }
        });
        
        systemFonts.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                System.out.println("Changed");
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
                if(fontSize != null) {
                    fontSizes.getSelectionModel().select(fontSize);
                } else {
                    fontSizes.getSelectionModel().select(5);
                }
            }
        });
        fontSizes.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                System.out.println("changed");
                UserDataManager.setDefaultFontSize(fontSizes.getSelectionModel().getSelectedItem().toString());
            }
        });

        fontStyleList = FXCollections.observableArrayList(
                "Plain", "Bold"
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
//                System.out.println("changed");
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
    
    public void addMessageLabel() {
        statusBar.getChildren().add(messageLabel);
    }
        
    public void addProgressBar() {
        statusBar.getChildren().add(0, progressBar);
    }
    
    public boolean isProgressBarAdded() {
        if(statusBar.getChildren().get(0) instanceof ProgressBar) return true;
        else return false;
    }
    
    public ProgressBar getProgressBar() {
        return progressBar;
    }
    
    public void removeProgressBar() {
        statusBar.getChildren().remove(progressBar);
    }

    public Label getStatusLabel() {
        return statusLabel;
    }
    
    /*** cursor related methods ***/
    private void setCursorIconForAllTextAtTab(Tab tab, Cursor imageCursor) {
        ScrollPane scrollPane = (ScrollPane) tab.getContent();
        Group group = (Group) scrollPane.getContent();
        for (Node node : group.getChildren()) {
            if (node instanceof CertificateText) {
                node.setCursor(imageCursor);
            }
        }
    }

    private void setCursorIconForTab(Tab tab, Cursor cursor) {
        ScrollPane scrollPane = (ScrollPane) tab.getContent();
        Group group = (Group) scrollPane.getContent();
        group.setCursor(Cursor.CROSSHAIR);
    }

    private void setCursorIconForAllTextAtAllTab(Cursor cursor) {
        for (Tab tab : tabPane.getTabs()) {
            setCursorIconForAllTextAtTab(tab, cursor);
        }
    }

    private void setCursorIconForAllTab(Cursor cursor) {
        for (Tab tab : tabPane.getTabs()) {
            setCursorIconForTab(tab, cursor);
        }
    }

    private void resetCursorIconForAllTextAtTab(Tab tab) {
        ScrollPane scrollPane = (ScrollPane) tab.getContent();
        Group group = (Group) scrollPane.getContent();
        for (Node node : group.getChildren()) {
            if (node instanceof CertificateText) {
                node.setCursor(Cursor.DEFAULT);
            }
        }
    }

    private void resetCursorIconForTab(Tab tab) {
        ScrollPane scrollPane = (ScrollPane) tab.getContent();
        Group group = (Group) scrollPane.getContent();
        group.setCursor(Cursor.DEFAULT);
    }

    private void resetCursorIconForAllTextAtAllTab() {
        for (Tab tab : tabPane.getTabs()) {
            resetCursorIconForAllTextAtTab(tab);
        }
    }

    private void resetCursorIconForAllTab() {
        for (Tab tab : tabPane.getTabs()) {
            resetCursorIconForTab(tab);
        }
    }
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
        CertificateTab tab = (CertificateTab) tabPane.getSelectionModel().getSelectedItem();
        int index = tabPane.getSelectionModel().getSelectedIndex();
        if(index != -1) {// do the rest
            if (tab.getFile() == null) {
                File file = getFileByDialog(stage, DIALOG_TYPE.SAVE, "Save template", tab.getText());
                certificateUtils.saveFileAtTab(tab, file);
                tab.setFile(file); // TODO change filepath to tabs
                if(!recentTemplatesBox.getItems().contains(file.getAbsolutePath())) recentTemplatesBox.getItems().add(file.getAbsolutePath()); // save recent
            } else {
                System.out.println("Saving file : " + tab.getFile().getAbsolutePath()); // DEBUG
                certificateUtils.saveFileAtTab(tab, tab.getFile());
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
        CertificateTab tab = (CertificateTab) tabPane.getSelectionModel().getSelectedItem();
        int index = tabPane.getSelectionModel().getSelectedIndex();
        if(index != -1){
            File file = getFileByDialog(PRIMARY_STAGE, DIALOG_TYPE.SAVE, "Save template", null);
            certificateUtils.saveFileAtTab(tab, file);
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
        File file = getFileByDialog(PRIMARY_STAGE, DIALOG_TYPE.OPEN, "Open certificate template", null);
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
    private File getFileByDialog(Stage stage, DIALOG_TYPE dialog_type, String title, String name) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.setTitle(title);
        File lastActivityPath = UserDataManager.getLastActivityPath();
        if(lastActivityPath != null) fileChooser.setInitialDirectory(lastActivityPath);
        // String name related
        if(name != null) {
            TextField field = getTextField(fileChooser);
            if(field != null) { // just for safety
                field.setText(name);
            }
        }
        // end
        File file = null;
        if(dialog_type == DIALOG_TYPE.OPEN) {
            file = fileChooser.showOpenDialog(stage);
//            System.out.println("opening file by dialog"); // debug
        } else if(dialog_type == DIALOG_TYPE.SAVE) {
//            System.out.println("saving file by dialog"); // debug
            file = fileChooser.showSaveDialog(stage);
//            System.out.println("previois path : " + file.getAbsolutePath()); // debug
            file = certificateUtils.correctXmlExtension(file);
//            System.out.println("current path : " + file.getAbsolutePath()); // debug
        }
        return file;
    }
    
    /**
     * Opens certificate template in a new tab and saves the path for future file chooser use.
     * @param file
     */
    private void openTemplateInGui(File file) {
        try {
            //        System.out.println("certificate fields : ");
            //        for (org.bluecipherz.certificatemaker.CertificateField certificateField : wrapper.getCertificateFields()) {
            //            System.out.println(certificateField.getFieldName()); // debug
            //        }
                    // open in new tab
                    if(!recentTemplatesBox.getItems().contains(file.getAbsolutePath())) recentTemplatesBox.getItems().add(file.getAbsolutePath()); // save recent
                    
                    createNewTab(file);
                    // save the file path to the registry
                    UserDataManager.setLastActivityPath(file);
                    UserDataManager.setCertificateFilePath(file); // TODO broken, no usage yet
        } catch (Exception ex) {
            Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
            Alert.showAlertError(PRIMARY_STAGE, "Error", ex.toString());
        }
    }

    /********* END HIGH LEVEL METHODS *********/
    
    private boolean isOpenedInGui(File file) {
        boolean opened = false;
        for(Tab tab : tabPane.getTabs()) {
            File openedFile = ((CertificateTab)tab).getFile();
            if(openedFile.equals(file)) opened = true;
        }
        return opened;
    }
    
    private int getTabIndex(File file) {
        int index = -1;
        for(Tab tab : tabPane.getTabs()) {
            File file2 = ((CertificateTab)tab).getFile();
            if(file.equals(file2)) index = tabPane.getTabs().indexOf(tab);
        }
        return index;
    }
    
    
    /*****************
     * EVENT HANDLERS
     ****************/
    
    /**
     * get mouse handlers fort the image view
     * @param dialog
     * @return 
     */
    private EventHandler<MouseEvent> getImageMouseHandler(){
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                ImageView imageView = (ImageView) t.getSource();
                System.out.println("clicked : x" + t.getX() + ", y" + t.getY());
                if(getMouseMode() == MouseMode.ADD) {
                    LABEL_DIALOG.setTextHolder((Group) imageView.getParent()); // tab bug fix
                    LABEL_DIALOG.prepareAndShowNewTextDialog(t.getX(), t.getY());
                } else if(getMouseMode() == MouseMode.ADD_IMAGE) {
                    int x = (int) t.getX();
                    int y = (int) t.getY();
                    
                    CertificateTab tab = (CertificateTab) tabPane.getSelectionModel().getSelectedItem();
//                    Node group = ((ScrollPane)tab.getContent()).getContent();
                    if(!tab.isAvatarFieldAdded()){
                        IMAGESIZEDIALOG.setImageHolder((Group) imageView.getParent()); // possible bug fix
                        IMAGESIZEDIALOG.newImage(x, y);
                        tab.setAvatarFieldAdded(true);
                    }
//                    CertificateTab tab = (CertificateTab) tabPane.getSelectionModel().getSelectedItem();
//                    if(!tab.isAvatarFieldAdded()) {
//                        
//                        // middle align shit
//                        int middlex = (int) (x - ResourceManger.getInstance().avatarx160.getWidth() / 2);
//                        int middley = (int) (y - ResourceManger.getInstance().avatarx160.getHeight() / 2);
//                        
//                        // add to tab
//                        ImageView imageView = createAvatarImage(middlex, middley);
////                        ImageView imageView = createAvatarImage(x, y);
//                        ((Group)((ScrollPane)tab.getContent()).getContent()).getChildren().add(imageView);
//                        tab.setAvatarFieldAdded(true);
//                    }
                }
            }
        };
    }
    
    public static EventHandler<MouseEvent> getAvatarMouseHandler() {
        return new EventHandler<MouseEvent>() {
            public double initialEventX;
            public double initialEventY;
            public double initialComponentX;
            public double initialComponentY;
            @Override
            public void handle(MouseEvent event) {
                System.out.println("clicked : x" + event.getX() + ", y" + event.getY());
                EventType<? extends Event> eventType = event.getEventType();
                ImageView imageView = (ImageView) event.getSource();
                
                if (eventType.equals(MouseEvent.MOUSE_PRESSED)) {
                    if(Window.getMouseMode() == MouseMode.MOVE) {
                        initialComponentX = imageView.getX();
                        initialComponentY = imageView.getY();
                        initialEventX = event.getX();
                        initialEventY = event.getY();
//                        System.out.println("Mode : move"); // debug
                    } else if (Window.getMouseMode() == MouseMode.DELETE) {
                        Group parent = (Group) imageView.getParent();
                        parent.getChildren().remove(imageView);
                        
                        ((CertificateTab)tabPane.getSelectionModel().getSelectedItem()).setAvatarFieldAdded(false);
//                        System.out.println("Mode : delete"); // debug
                    }
                } else if (eventType.equals(MouseEvent.MOUSE_DRAGGED)) {
                    if(Window.getMouseMode() == MouseMode.MOVE) {
                    double currentX = event.getX();
                        double currentY = event.getY();
                        double x = currentX - initialEventX + initialComponentX;
                        double y = currentY - initialEventY + initialComponentY;
                        imageView.setX(x);
                        imageView.setY(y);
                    }
                } else if (eventType.equals(MouseEvent.MOUSE_RELEASED)) {
                    if(Window.getMouseMode() == MouseMode.MOVE) {
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
    
    public static EventHandler<MouseEvent> getTextMouseHandler() {
         return new EventHandler<MouseEvent>() {
            public double initialEventX;
            public double initialEventY;
            public double initialComponentX;
            public double initialComponentY;
            @Override
            public void handle(MouseEvent event) {
                EventType<? extends Event> eventType = event.getEventType();
                CertificateText text = (CertificateText) event.getSource();
                if (eventType.equals(MouseEvent.MOUSE_PRESSED)) {
                    if(Window.getMouseMode() == MouseMode.MOVE) {
                        initialComponentX = text.getX();
                        initialComponentY = text.getY();
                        initialEventX = event.getX();
                        initialEventY = event.getY();
//                        System.out.println("Mode : move"); // debug
                    } else if (Window.getMouseMode() == MouseMode.DELETE) {
                        Group parent = (Group) text.getParent();
                        parent.getChildren().remove(text);
//                        System.out.println("Mode : delete"); // debug
                    } else if (Window.getMouseMode() == MouseMode.EDIT) {
                        Window.showEditFieldDialog(text);
//                        System.out.println("Mode : edit"); // debug
                    }
                } else if (eventType.equals(MouseEvent.MOUSE_DRAGGED)) {
                    if(Window.getMouseMode() == MouseMode.MOVE) {
                        double currentX = event.getX();
                        double currentY = event.getY();
                        double x = currentX - initialEventX + initialComponentX;
                        double y = currentY - initialEventY + initialComponentY;
                        text.setX(x);
                        text.setY(y);
                    }
                } else if (eventType.equals(MouseEvent.MOUSE_RELEASED)) {
                    if(Window.getMouseMode() == MouseMode.MOVE) {
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
    

    public void createNewTab(final File file) throws Exception {
        CertificateWrapper certificateWrapper = certificateUtils.openTemplate(file);
        CertificateTab tab = createNewTab(certificateWrapper);
        tab.setFile(file);
    }
    
    /**
     * used by outsiders, hence public
     * Create a new tab using a certificate wrapper. the template will be
     * loaded into and tab and the image will also be displayed
     * @param certificateWrapper
     */
    public CertificateTab createNewTab(final CertificateWrapper wrapper) {
        final CertificateWrapper certificateWrapper = wrapper;
        //tab children heirarchy
        //tab -> scrollpane -> group -> imageview,text
        final CertificateTab tab = new CertificateTab();
        final File imageFile = certificateWrapper.getCertificateImage();
        tab.setText(certificateWrapper.getName());
        final ScrollPane scrollPane = new ScrollPane();
        // dont know the real use of this,
        scrollPane.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> ov, Bounds t, Bounds t1) {
                Node content = scrollPane.getContent(); // content is group
                scrollPane.setFitToWidth(content.prefWidth(-1)<t1.getWidth());
                scrollPane.setFitToHeight(content.prefHeight(-1)<t1.getHeight());
            }
        });
        
        Group group = new Group();
        
        final ImageView imageView = new ImageView(); // fixture
        final Image image = new Image(imageFile.toURI().toString(), true);
//        progressBar.progressProperty().bind(image.progressProperty());
//        addProgressBar();
        LOADING.showProgressing(image.progressProperty());
        image.progressProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
//                System.out.println("oldvalue " + oldValue.toString() + ", newvalue " + newValue.toString());
//                statusLabel.setText("Loading : " + (int) newValue.doubleValue() + "%");
                if(newValue.intValue() == 1) {
                    imageView.setImage(image);
//                    statusLabel.setText(imageFile.getName() + "(" + convertToStringSize(imageFile.length()) + ")");
//                    removeProgressBar();
                }
            }

        });
        
        if (LABEL_DIALOG == null) {
            LABEL_DIALOG = createLabelDialog(PRIMARY_STAGE);
        }
        if(IMAGESIZEDIALOG == null) {
            IMAGESIZEDIALOG = new ImageSizeDialog(PRIMARY_STAGE, this);
        }
        // label dialog initialized
        imageView.setOnMouseClicked(getImageMouseHandler());
        
        group.getChildren().add(imageView);
        // after adding the imageview, add the fields to the opened certificate if there are any
        if(!certificateWrapper.getCertificateFields().isEmpty()) {
            
            for (CertificateField certificateField : wrapper.getCertificateFields()) {
                if(certificateField.getFieldType() != FieldType.IMAGE) { // its a text object as long as its not an image
                    CertificateText certificateText = certificateUtils.createText(certificateField);
                    certificateText.setX(certificateField.getX() - certificateText.getLayoutBounds().getWidth() / 2); // alignment
                    group.getChildren().add(certificateText);
                } else {
                    // add avatar image
                    ImageView avatarImage = certificateUtils.createAvatarImage(certificateField.getX(), certificateField.getY(), certificateField.getWidth(), certificateField.getHeight());
                    group.getChildren().add(avatarImage);
                }
            }
        }
//        imageView.setFitHeight(scrollPane.getHeight());
//        imageView.setFitWidth(scrollPane.getWidth());        
        scrollPane.setContent(group);
        System.out.println("Adding to scrollpane " + scrollPane.toString()); // debug
        tab.setContent(scrollPane);
//        System.out.println("adding tab to tabpane"); // debug
        tabPane.getTabs().add(tab);
        
        // TODO tab change action
        // TODO before tab close action
//        refreshTabPaneEventHandlers();

        tab.setCertificateWrapper(certificateWrapper); // fixture
        return tab;
    }
    
//    public void refreshTabPaneEventHandlers() {
//        Set<Node> nodes = tabPane.lookupAll(".tab-close-button");
//        for(final Node node : nodes) {
//            node.setUserData(node.getOnMouseReleased());
//            node.setOnMouseReleased(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent t) {
////                    System.out.println("whoah!");
//                    ((EventHandler<MouseEvent>)node.getUserData()).handle(t);
//                }
//            });
//        }
//    }

    
    /**
     * method used to show the new certificate dialog
     * @param parent
     * @return 
     */
    private NewFileDialog createNewfileDialog(Stage parent) {
        if (NEWFILE_DIALOG != null) {
            NEWFILE_DIALOG.close();
        }
        return new NewFileDialog(parent, "Create new Certificate", this).reset();
    }
    
    /**
     * initializes(if null) and returns a LabelDialog object
     * @param primaryStage
     * @param parent
     * @return 
     */
    private LabelDialog createLabelDialog(Stage primaryStage) {
        if (LABEL_DIALOG != null) {
            LABEL_DIALOG.close();
        }
        return new LabelDialog(primaryStage, this);
    }

    
    public static void showEditFieldDialog(CertificateText text) {
        LABEL_DIALOG.prepareAndShowEditTextDialog(text);
    }

//    public static void main(String[] args) { launch(args); }
    
    
    private String convertToStringSize(long length) {
        String result = null;
        if(length != 0) {
            if(length < 1000) { // Bytes
                result = String.valueOf(length);
                result = result.substring(0, result.lastIndexOf(".") + 3) + "B";
            } else if(length < 1000 * 1000) { // kilo bytes
                result = String.valueOf(length / 1000f);
                result = result.substring(0, result.lastIndexOf(".") + 3) + "KB";
            } else if(length < 1000 * 1000 * 1000) { // mega bytes
                result = String.valueOf(length / 1000000f);
                result = result.substring(0, result.lastIndexOf(".") + 3) + "MB";
            }
        }
        return result;
    }
    
    public static double round(double value, int places) {
        if(places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
