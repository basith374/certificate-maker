/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author bazi
 */
public class FieldsDialog extends Stage {
    private VBox box;
    private TableView<CertificateNode> table;
    private final ObservableList<CertificateNode> fields;
    
    public FieldsDialog(Stage primary) {
        initModality(Modality.APPLICATION_MODAL);
        initOwner(primary);
        setTitle("Total fields");
        
        box = new VBox();
        box.setPadding(new Insets(10));
        table = new TableView<>();
//        table.setPrefWidth(500);
        table.setMaxWidth(500);
        fields = FXCollections.observableArrayList();
        table.setItems(fields);
        
        
        TableColumn<CertificateNode, String> column1 = new TableColumn<>("X");
        column1.setCellValueFactory(new PropertyValueFactory("x"));
        TableColumn<CertificateNode, String> column2 = new TableColumn<>("Y");
        column2.setCellValueFactory(new PropertyValueFactory("y"));
        TableColumn<CertificateNode, String> column3 = new TableColumn<>("Field Type");
        column3.setCellValueFactory(new PropertyValueFactory("fieldType"));
        TableColumn<CertificateNode, String> column4 = new TableColumn<>("Field Name");
        column4.setCellValueFactory(new PropertyValueFactory("fieldName"));
        TableColumn<CertificateNode, String> column5 = new TableColumn<>("Font Family");
        column5.setCellValueFactory(new PropertyValueFactory("fontFamily"));
        TableColumn<CertificateNode, String> column6 = new TableColumn<>("Font Size");
        column6 .setCellValueFactory(new PropertyValueFactory("fontSize"));
        TableColumn<CertificateNode, String> column7 = new TableColumn<>("Font Style");
        column7.setCellValueFactory(new PropertyValueFactory("fontStyle"));
        
        table.getColumns().setAll(column1, column2, column3, column4, column5, column6, column7);
        
        Button okButton = new Button("OK");
        VBox.setMargin(okButton, new Insets(5));
        box.setAlignment(Pos.CENTER_RIGHT);
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                close();
            }
        });
        
        box.getChildren().add(table);
        box.getChildren().add(okButton);
        Scene scene = new Scene(box);
        setScene(scene);
    }
    
    public void showDialog(ObservableList<CertificateNode> list) {
        fields.setAll(list);
        sizeToScene();
        show();
    }
    
}
