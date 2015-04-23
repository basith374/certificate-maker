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
