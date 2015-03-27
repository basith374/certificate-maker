/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author bazi
 */
class NewCertificateDialog extends Stage {
    private final CertificateWrapper wrapper; // no use yet
    private final Stage primaryStage;
    private final Image certificateImage;
    private final Window window; // no use yet

    public NewCertificateDialog(Stage parent, CertificateWrapper wrapper, final Window window) {
        super();
        primaryStage = parent;
        this.window = window;
        initOwner(parent);
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Create new certificate");
        this.wrapper = wrapper;
        this.certificateImage = new Image(wrapper.getImage().toURI().toString());
        //            org.bluecipherz.certificatemaker.Window.this.createNewTab(wrapper);
        GridPane gridPane = createEntryFieldsandLabels(wrapper);
        Scene scene = new Scene(gridPane, Color.WHITE);
        setScene(scene);
        sizeToScene();
        show();
    }

    private GridPane createEntryFieldsandLabels(final CertificateWrapper wrapper) {
        final GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        int row = 0;
        for (CertificateField certificateField : wrapper.getCertificateFields()) {
            Label label = new Label(certificateField.getFieldName());
            gridPane.add(label, 0, row);
            TextField textField = new TextField();
            gridPane.add(textField, 1, row);
            row++;
        }
        Button button = new Button("OK");
        GridPane.setHalignment(button, HPos.RIGHT);
        gridPane.add(button, 1, row);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // System.out.println("Populating certificate fields :"); // debug
                ObservableList<TextField> textFields = FXCollections.observableArrayList();
                for (Node node : gridPane.getChildren()) {
                    if (node instanceof TextField) {
                        textFields.add((TextField) node);
                    }
                }
                int index = 0;
                for (CertificateField field : wrapper.getCertificateFields()) {
                    field.text = textFields.get(index).getText();
                    // System.out.println("row " + index + " : " + field.text); // debug
                    index++;
                }
                createCertificateImage(wrapper, certificateImage);
                close();
            }
        });
        return gridPane;
    }

    private void createCertificateImage(CertificateWrapper wrapper, Image certificateImage) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JPEG & PNG Files", "*.jpg", "*.jpeg", "*.png");
        File file = fileChooser.showSaveDialog(primaryStage); // dependency
        ImageUtils.createCertificateImage(wrapper, certificateImage, file);
    }
    
}
