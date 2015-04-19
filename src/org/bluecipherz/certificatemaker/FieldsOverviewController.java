/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

/**
 *
 * @author bazi
 */
public class FieldsOverviewController {

    private TableView<CertificateField> fieldTable;
    
    private TableColumn<CertificateField, Integer> xColumn;
    private TableColumn<CertificateField, Integer> yColumn;
    private TableColumn<CertificateField, FieldType> fieldTypeColumn;
//    private TableColumn<CertificateField, String> fieldNameColumn;
//    private TableColumn<CertificateField, String> fontFamilyColumn;
//    private TableColumn<CertificateField, String> fontSizeColumn;
//    private TableColumn<CertificateField, String> fontStyleColumn;
    
    public FieldsOverviewController() {
    }
    
    private void initialize() {
        
    }
    
}
