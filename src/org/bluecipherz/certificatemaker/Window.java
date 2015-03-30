package org.bluecipherz.certificatemaker;
//import com.google.gson.Gson;
import com.sun.deploy.resources.ResourceManager;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
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
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.geometry.Bounds;
import javafx.scene.layout.HBox;

/**
 * This class is the main thing with over 1k LOC. technically speaking. however i think there are more comments than
 * code. Im thinking of breaking this apart into smaller pieces but not until this thing does what it says it
 * does. Feel free to do it if want to. :)
 * Created by bazi on 22/3/15.
 */
public class Window extends Application {

    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 500;

    private static Stage PRIMARY_STAGE;
    
    private static NewFileDialog NEWFILE_DIALOG;
    private static LabelDialog LABEL_DIALOG;
    private static NewCertificateDialog NEWCERTIFICATE_DIALOG;
    
    private BorderPane borderPane;
    private TabPane tabPane;
    
//    private final ArrayList<CertificateWrapper> certificateList = new ArrayList<>();
    
    // getters only
    private ObservableList<String> fontFamilyList; // System specific
    private ObservableList<String> fontStyleList;
    private ObservableList<Integer> fontSizeList;

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

    @Override
    public void start(Stage primaryStage) throws Exception {
        ResourceManger.getInstance().loadSplashResource(); // TODO seperate splash
        ResourceManger.getInstance().loadAppResources();
        
        primaryStage.getIcons().add(ResourceManger.getInstance().iconx48);
        PRIMARY_STAGE = primaryStage;
        setMouseMode(MouseMode.MOVE);
        primaryStage.setTitle("Certificate Maker BCZ.");
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

        primaryStage.setScene(scene);
        primaryStage.show();
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

        final Button button1 = new Button(); // MOVE
        final Button button2 = new Button(); // insert IMAGE
        final Button button3 = new Button(); // insert TEXT
        final Button button4 = new Button(); // delete
        final Button button5 = new Button(); // edit

        button1.setGraphic(new ImageView(ResourceManger.getInstance().movex32));
        button2.setGraphic(new ImageView(ResourceManger.getInstance().addimgx32));
        button3.setGraphic(new ImageView(ResourceManger.getInstance().addx32));
        button4.setGraphic(new ImageView(ResourceManger.getInstance().delx32));
        button5.setGraphic(new ImageView(ResourceManger.getInstance().editx32));

        EventHandler<ActionEvent> eventHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Button button = (Button) event.getSource();
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

        // HELP MENU
        Menu helpMenu = new Menu("Help");

        MenuItem aboutMenu = new MenuItem("About");
        
        aboutMenu.setGraphic(new ImageView(ResourceManger.getInstance().iconx16));

        helpMenu.getItems().add(aboutMenu);
        
        aboutMenu.setOnAction(handler); // dependency

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
                        CertificateWrapper certificateWrapper = openTemplate(file);
                        createNewCertficateDialog(PRIMARY_STAGE, certificateWrapper);
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
//                    flaut();
//                    blowupcomputer();
                }
            }
        };
    }
    
    /**
     * Exit the application
     */
    private void shutdown() {
        // TODO before shutdown actions
        System.exit(0);
    }
    
    /**
     * initializes and returns the tab pane
     * @return 
     */
    private Node getTabPane() {
        tabPane = new TabPane();
//        tabPane.addEventHandler(EventType.ROOT, );
        return tabPane;
    }

    /**
     * initializes and returns the toolbar(top)
     * @return 
     */
    private ToolBar getToolBar() {
        // TOOL BAR

        ToolBar toolBar = new ToolBar();


        Label recentTemplatesLbl = new Label("Recent :");
        toolBar.getItems().add(recentTemplatesLbl);

        final ComboBox recentTemplatesBox = new ComboBox();
//        final ComboBox recentTemplatesBox = new ComboBox(getRecentTemplates()); // null pointer
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
                    createNewCertficateDialog(PRIMARY_STAGE, wrapper);
                } else {
                    System.out.println("unknown condition : tab index out of bounds"); // debug
                }
            }
        });

        // seperator
        Separator separator = new Separator(Orientation.VERTICAL);
        toolBar.getItems().add(separator);

        Label fontsLbl = new Label("Font : ");
        fontFamilyList = FXCollections.observableArrayList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()
        );
//        ComboBox templates = new ComboBox(templateList);
        final ComboBox systemFonts = new ComboBox(fontFamilyList);
        systemFonts.getSelectionModel().select(0);
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
        final ComboBox fontSizes = new ComboBox(fontSizeList);
        fontSizes.getSelectionModel().select(11);
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
        final ComboBox fontStyles = new ComboBox(fontStyleList);
        fontStyles.getSelectionModel().select(0);
        fontStyles.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                System.out.println("changed");
                UserDataManager.setDefaultFontStyle(fontStyles.getSelectionModel().getSelectedItem().toString());
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

        Button toolButton1 = new Button("New{beta}"); // test, no use yet
        Button toolButton2 = new Button("Open{beta}"); // test, no use yet
        Button toolButton3 = new Button("Load Image{test}"); // test, no use yet
        Button toolButton4 = new Button("New Certificate{test}"); // test, no use yet

//        toolBar.getItems().add(toolButton1); // temporarily removed
//        toolBar.getItems().add(toolButton2); // temporarily removed
//        toolBar.getItems().add(toolButton3); // temporarily removed
//        toolBar.getItems().add(toolButton4); // temporarily removed

//        toolButton1.setOnAction(handler);
//        toolButton2.setOnAction(handler);
        toolButton3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                createNewTab("Test", "/home/bazi/Pictures/me/0.jpg");
                String title = "Test0";
//                Pattern digitPattern = Pattern.compile("(\\d+)");
//                Matcher matcher;
//                for (Tab tab : tabPane.getTabs()) {
//                    if (title.equals(tab.getText())) {
//                        matcher = digitPattern.matcher(title);
//                        StringBuffer result = new StringBuffer();
//                        matcher.appendReplacement(result, String.valueOf(Integer.parseInt(matcher.group(1)) + 1));
//                        matcher.appendTail(result);
//                        title = result.toString();
//                        System.out.println("title: " + title);
//                    }
//                }
//                CertificateWrapper certificateWrapper = createCertificateWrapper(title, "/home/bazi/Pictures/me/0.jpg");
                CertificateWrapper certificateWrapper = createCertificateWrapper(title, "C:\\Documents and Settings\\All Users\\Documents\\My Pictures\\Sample Pictures\\Sunset.jpg");
//                org.bluecipherz.certificatemaker.CertificateWrapper certificateWrapper = createCertificateWrapper("Test", "/home/bazi/Pictures/me/0.jpg");
//                createNewTab(certificateWrapper);
            }
        });
        toolButton4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CertificateWrapper certificateWrapper = openTemplate(getFileByDialog(PRIMARY_STAGE, DIALOG_TYPE.OPEN, "", null));
                createNewCertficateDialog(PRIMARY_STAGE, certificateWrapper);
            }
        });

        return toolBar;
    }
    
    private Node getStatusBar() {
        HBox box = new HBox();
        Label label = new Label("Status");
        box.getChildren().add(label);
        return box;
    }
    /*** END GUI INITIALIZER METHODS ***/
    
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
        String tabTitle = tab.getText();
        if(index != -1) {// do the rest
            if (tab.getFile() == null) {
                File file = getFileByDialog(stage, DIALOG_TYPE.SAVE, "Save template", tabTitle);
                saveFileAtTab(index, file);
                tab.setFile(file); // TODO change filepath to tabs
            } else {
                System.out.println("Saving file : " + tab.getFile().getAbsolutePath()); // DEBUG
                saveFileAtTab(index, tab.getFile());
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
        int index = tabPane.getSelectionModel().getSelectedIndex();
        if(index != -1){
            File file = getFileByDialog(PRIMARY_STAGE, DIALOG_TYPE.SAVE, "Save template", null);
            saveFileAtTab(index, file);
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
            file = correctXmlExtension(file);
//            System.out.println("current path : " + file.getAbsolutePath()); // debug
        }
        return file;
    }
    
    /**
     * Opens certificate template in a new tab and saves the path for future file chooser use.
     * @param file
     */
    private void openTemplateInGui(File file) {
//        System.out.println("certificate fields : ");
//        for (org.bluecipherz.certificatemaker.CertificateField certificateField : wrapper.getCertificateFields()) {
//            System.out.println(certificateField.getFieldName()); // debug
//        }
        // open in new tab
        createNewTab(file);
        // save the file path to the registry
        UserDataManager.setLastActivityPath(file);
        UserDataManager.setCertificateFilePath(file); // TODO broken, no usage yet
    }

    /********* END HIGH LEVEL METHODS *********/
    
    
    
    /*************************
     *   LOW LEVEL METHODS   *
     ************************/
    
    /**
     * saves certificate using the elements from the tablist and ceritificate list.
     * @param index
     * @param file 
     */
    private void saveFileAtTab(int index, File file) {
        CertificateTab tab = (CertificateTab) tabPane.getTabs().get(index);
        ScrollPane scrollPane = (ScrollPane) tab.getContent();
        Group group = (Group) scrollPane.getContent();
        ObservableMap<FieldType, CertificateField> certificateFieldMap = populateCertificateFields(group);
        CertificateWrapper wrapper = tab.getCertificateWrapper(); // TODO change certificatelist to tabs
        wrapper.setCertificateFields(certificateFieldMap);
        createCertificateFile(file, wrapper);
        tab.setChanged(false);
//        tab.setFile(file); // done at an upper level
        UserDataManager.setLastActivityPath(file);
    }

    /**
     * Used to retrieve certificatewrapper object from the xml template file.
     * @param file
     * @return
     */
    private CertificateWrapper openTemplate(File file) { // new implementation
        CertificateWrapper wrapper = null;
        boolean fileOk = testFileIntegrity(file);
        if(fileOk) {
            try {
                JAXBContext context = JAXBContext.newInstance(CertificateWrapper.class);
                Unmarshaller um = context.createUnmarshaller();
                if (file.exists()) {
                    wrapper = (CertificateWrapper) um.unmarshal(file);
                }
//                System.out.println("Unmarshalling...\n" + wrapper); // IMPORTANT debug
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        return wrapper;
    }

    /**
     * Converts a text object in a tab to a certificatefield object that encapsulates its data.
     * @param node
     * @return
     */
    private CertificateField convertToCertificateField(Node node) {
        CertificateText text = (CertificateText) node;
        int x = (int) text.getX();
        int y = (int) text.getY();
        Font font = text.getFont();
        int fontSize = (int) font.getSize(); // TODO do something
        String fontFamily = font.getFamily();
        String fontWeight = font.getStyle();
        int fontStyle = (FontWeight.BOLD.toString().equalsIgnoreCase(fontWeight)) ? java.awt.Font.BOLD : java.awt.Font.PLAIN;
        CertificateField field = new CertificateField(x, y, text.getText(), fontSize, fontFamily, fontStyle);
        field.setFieldType(text.getFieldType());
        return field;
    }
    
    private CertificateField convertToCertificateImage(Node node) {
        ImageView imageView = (ImageView) node;
        int x = (int) imageView.getX();
        int y = (int) imageView.getY();
        /*there are two image views in the graph, one is the certificate image
         * and the second is the avatar image, we only need to get the avatar image.
         */
        if(x != 0 && y != 0) {
            CertificateField field = new CertificateField(x, y); // this constructor makes this an image type
            field.setFieldType(FieldType.IMAGE);
            return field;
        } else {
            return null;
        }
    }

    /**
     * create a certificatefield array using all the text objects in a tab by specifying the group object
     * inside the tab and getting its fields manually.
     * @param group
     * @return
     */
    private ObservableMap<FieldType, CertificateField> populateCertificateFields(Group group) {
        ObservableMap<FieldType, CertificateField> certificateFieldMap = FXCollections.observableHashMap();
        for (Node node : group.getChildren()) {
            if (node instanceof CertificateText) {
                CertificateText text = (CertificateText) node;
                certificateFieldMap.put(text.getFieldType(), convertToCertificateField(node));
            } else if(node instanceof ImageView) {
                CertificateField field = convertToCertificateImage(node);
                if(field != null) {
                    certificateFieldMap.put(FieldType.IMAGE, field);
                }
            }
        }
        return certificateFieldMap;
    }

    /**
     * Low level method used to save a certificatewrapper object as a template xml file.
     * @param file
     * @param wrapper
     */
    private void createCertificateFile(File file, CertificateWrapper wrapper) {
        try {
            JAXBContext context = JAXBContext.newInstance(CertificateWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(wrapper, file);
            System.out.println("populated wrapper : " + wrapper);
            UserDataManager.setCertificateFilePath(file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
        
    /**
     * used to check the xml file for issues.
     * @param file 
     */
    private boolean testFileIntegrity(File file) {
        if(file != null){
            if(file.isFile()){
                // TODO not yet implemented
            }
        }
        return true;
    }
    
    /**
     * checks for the extension of the file and corrects.
     * @param file
     * @return 
     */
    private File correctXmlExtension(File file) {
        String path = file.getAbsolutePath();
        if(!path.endsWith(".xml")) {
//            System.out.print("correcting file extension... , previous path : "+ path ); // debug
            path = path.concat(".xml");
//            System.out.println(" , corrected path : " + path); // debug
        }
        return new File(path);
    }
    
    private Task createWorker() {
        return new Task() {

            @Override
            protected Object call() throws Exception {
                // 
                return true;
            }
            
        };
    }
    
    /********* END LOW LEVEL METHODS *********/


    /*****************
     * EVENT HANDLERS
     ****************/
    
    /**
     * get mouse handlers fort the image view
     * @param dialog
     * @return 
     */
    private EventHandler<MouseEvent> getImageMouseHandler(final LabelDialog dialog){
        return new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if(getMouseMode() == MouseMode.ADD) {
                    dialog.prepareAndShowNewTextDialog(t.getX(), t.getY());
                } else if(getMouseMode() == MouseMode.ADD_IMAGE) {
                    int x = (int) t.getX();
                    int y = (int) t.getY();
                    
                    CertificateTab tab = (CertificateTab) tabPane.getSelectionModel().getSelectedItem();
                    if(!tab.isAvatarFieldAdded()) {
                        // add to tab
                        ImageView imageView = createAvatarImage(x, y);
                        ((Group)((ScrollPane)tab.getContent()).getContent()).getChildren().add(imageView);
                        tab.setAvatarFieldAdded(true);
//                        imageView.setX(x); // redundant but necessary, 
//                        imageView.setY(y); // redundant but necessary, 
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
    
    /**
     * used by outsiders, so public
     * create a new blank certificate wrapper object using the specified file and image
     * @param fileName
     * @param imagePath
     * @return 
     */
    public CertificateWrapper createCertificateWrapper(String fileName, String imagePath) {
        CertificateWrapper certificateWrapper = new CertificateWrapper();
        certificateWrapper.setName(fileName); // used as tab name and save name
        certificateWrapper.setCertificateImage(new File(imagePath));
        certificateWrapper.setCertificateFields(new HashMap<FieldType, CertificateField>());
        return certificateWrapper;
    }
    
    
    public void createNewTab(final File file) {
        CertificateWrapper certificateWrapper = openTemplate(file);
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
        CertificateWrapper certificateWrapper = wrapper;
        //tab children heirarchy
        //tab -> scrollpane -> group -> imageview,text
        final CertificateTab tab = new CertificateTab();
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
        
        final ImageView imageView = new ImageView(certificateWrapper.getCertificateImage().toURI().toString()); // fixture
        if (LABEL_DIALOG == null) {
            LABEL_DIALOG = createLabelDialog(PRIMARY_STAGE, group);
        }
        // label dialog initialized
        imageView.setOnMouseClicked(getImageMouseHandler(LABEL_DIALOG));
        
        group.getChildren().add(imageView);
        // after adding the imageview, add the fields to the opened certificate if there are any
        if(!certificateWrapper.getCertificateFields().isEmpty()) {
            Set<Map.Entry<FieldType, CertificateField>> set = certificateWrapper.getCertificateFields().entrySet();
            for (Map.Entry<FieldType, CertificateField> certificateField : set) {
                if(certificateField.getKey() != FieldType.IMAGE) { // its a text object as long as its not an image
                    CertificateText certificateText = createText(certificateField.getValue());
                    group.getChildren().add(certificateText);
                } else {
                    // add avatar image
                    ImageView avatarImage = createAvatarImage(certificateField.getValue().getX(), certificateField.getValue().getY());
                    group.getChildren().add(avatarImage);
                }
            }
        }
//        imageView.setFitHeight(scrollPane.getHeight());
//        imageView.setFitWidth(scrollPane.getWidth());        
        scrollPane.setContent(group);
        tab.setContent(scrollPane);
//        System.out.println("adding tab to tabpane"); // debug
        tabPane.getTabs().add(tab);
        
        // TODO tab change action
        // TODO before tab close action
        refreshTabPaneEventHandlers();

        tab.setCertificateWrapper(certificateWrapper); // fixture
        return tab;
    }
    
    public void refreshTabPaneEventHandlers() {
        Set<Node> nodes = tabPane.lookupAll(".tab-close-button");
        for(final Node node : nodes) {
            node.setUserData(node.getOnMouseReleased());
            node.setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent t) {
//                    System.out.println("whoah!");
                    ((EventHandler<MouseEvent>)node.getUserData()).handle(t);
                }
            });
        }
    }

    /**
     * used by outsiders
     * converts and CertificateField into a Text object
     * @param certificateField
     * @return 
     */
    public CertificateText createText(final CertificateField certificateField) {
        CertificateText text = new CertificateText(certificateField);
//        text.setFont(Font.font(certificateField.getFontFamily(), certificateField.getFontSize())); // TODO custom font functionality
        EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {
            public double initialEventX;
            public double initialEventY;
            public double initialComponentX;
            public double initialComponentY;
            @Override
            public void handle(MouseEvent event) {
                EventType<? extends Event> eventType = event.getEventType();
                CertificateText text = (CertificateText) event.getSource();
                if (eventType.equals(MouseEvent.MOUSE_PRESSED)) {
                    if(MOUSEMODE == MouseMode.ADD || MOUSEMODE == MouseMode.ADD_IMAGE || MOUSEMODE == MouseMode.MOVE) {
                        initialComponentX = text.getX();
                        initialComponentY = text.getY();
                        initialEventX = event.getX();
                        initialEventY = event.getY();
                        System.out.println("Mode : add, move"); // debug
                    } else if (MOUSEMODE == MouseMode.DELETE) {
                        Group parent = (Group) text.getParent();
                        parent.getChildren().remove(text);
                        System.out.println("Mode : delete"); // debug
                    } else if (MOUSEMODE == MouseMode.EDIT) {
                        LABEL_DIALOG.prepareAndShowEditTextDialog(text);
                        System.out.println("Mode : edit"); // debug
                    }
                } else if (eventType.equals(MouseEvent.MOUSE_DRAGGED)) {
                    double currentX = event.getX();
                    double currentY = event.getY();
                    double x = currentX - initialEventX + initialComponentX;
                    double y = currentY - initialEventY + initialComponentY;
                    text.setX(x);
                    text.setY(y);
                } else if (eventType.equals(MouseEvent.MOUSE_RELEASED)) {
                    double currentX = event.getX();
                    double currentY = event.getY();
                    double x = currentX - initialEventX + initialComponentX;
                    double y = currentY - initialEventY + initialComponentY;
                    text.setX(x);
                    text.setY(y);
                }
            }
        };
        text.setOnMousePressed(mouseHandler);
        text.setOnMouseDragged(mouseHandler);
        text.setOnMouseReleased(mouseHandler);
        return text;
    }

    public ImageView createAvatarImage(int x, int y) {
        ImageView imageView = new ImageView(ResourceManger.getInstance().avatarx160);
        imageView.setX(x);
        imageView.setY(y);
        EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {
            public double initialEventX;
            public double initialEventY;
            public double initialComponentX;
            public double initialComponentY;
            @Override
            public void handle(MouseEvent event) {
                EventType<? extends Event> eventType = event.getEventType();
                ImageView imageView = (ImageView) event.getSource();
                if (eventType.equals(MouseEvent.MOUSE_PRESSED)) {
                    if(MOUSEMODE == MouseMode.ADD || MOUSEMODE == MouseMode.ADD_IMAGE || MOUSEMODE == MouseMode.MOVE) {
                        initialComponentX = imageView.getX();
                        initialComponentY = imageView.getY();
                        initialEventX = event.getX();
                        initialEventY = event.getY();
                        System.out.println("Mode : add, move"); // debug
                    } else if (MOUSEMODE == MouseMode.DELETE) {
                        Group parent = (Group) imageView.getParent();
                        parent.getChildren().remove(imageView);
                        System.out.println("Mode : delete"); // debug
                    } else if (MOUSEMODE == MouseMode.EDIT) {
                        System.out.println("Mode : edit"); // debug
                    }
                } else if (eventType.equals(MouseEvent.MOUSE_DRAGGED)) {
                    double currentX = event.getX();
                    double currentY = event.getY();
                    double x = currentX - initialEventX + initialComponentX;
                    double y = currentY - initialEventY + initialComponentY;
                    imageView.setX(x);
                    imageView.setY(y);
                } else if (eventType.equals(MouseEvent.MOUSE_RELEASED)) {
                    double currentX = event.getX();
                    double currentY = event.getY();
                    double x = currentX - initialEventX + initialComponentX;
                    double y = currentY - initialEventY + initialComponentY;
                    imageView.setX(x);
                    imageView.setY(y);
                }
            }
        };
        imageView.setOnMousePressed(mouseHandler);
        imageView.setOnMouseDragged(mouseHandler);
        imageView.setOnMouseReleased(mouseHandler);
        return imageView;
    }
    
    /**
     * method used to show the new certificate dialog
     * @param parent
     * @return 
     */
    private NewFileDialog createNewfileDialog(Stage parent) {
        if (NEWFILE_DIALOG != null) {
            NEWFILE_DIALOG.close();
        }
        return new NewFileDialog(parent, "Create new Certificate", this);
    }
    
    /**
     * initializes(if null) and returns a LabelDialog object
     * @param primaryStage
     * @param parent
     * @return 
     */
    private LabelDialog createLabelDialog(Stage primaryStage, Group parent) {
        if (LABEL_DIALOG != null) {
            LABEL_DIALOG.close();
        }
        return new LabelDialog(primaryStage, parent, this);
    }

    /**
     * initializes and returns a new NewCertificateDialog object
     * @param parent
     * @param wrapper
     * @return 
     */
    private NewCertificateDialog createNewCertficateDialog(Stage parent, CertificateWrapper wrapper) {
        return new NewCertificateDialog(parent, wrapper, this);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
