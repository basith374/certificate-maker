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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author bazi
 */
public final class CertificateText extends Text {
    
    // these values are binded to the observer
    private ReadOnlyObjectWrapper<FieldType> fieldType;
    private ReadOnlyBooleanWrapper repeatin;
    private ObservableList<String> array;
    // the actual field that gets saved in the wrapper
    private CertificateField observer;
    
    public void setObserver(CertificateField observer) {
        this.observer = observer;
    }

    public CertificateField getObserver() {
        return observer;
    }
    
    
    public CertificateText(CertificateField field) {
        this.setX(field.getX());
        this.setY(field.getY());
//        Debugger.log("adding text : " + field.getX() + "," + field.getY()); // debug
        FontWeight fw = FontWeight.findByName(field.getFontStyle());
        this.setFont(Font.font(field.getFontFamily(), fw, field.getFontSize()));
        
        if(field.getFieldType() == FieldType.TEXT || field.getFieldType() == FieldType.ARRAY) setText(field.getFieldName());
        else setText(field.getFieldType().toString());
//            Debugger.log("Text : " + certificateField.getFieldType().toString()); // debug
        
        fieldType = new ReadOnlyObjectWrapper(field.getFieldType());
        if(field.getFieldType() == FieldType.TEXT) repeatin = new ReadOnlyBooleanWrapper(field.isRepeating());
        if(field.getFieldType() == FieldType.ARRAY) array = FXCollections.observableArrayList(field.getArray());
    }
    
    public void setAttributes(CertificateField field) {
        FontWeight fw = FontWeight.findByName(field.getFontStyle());
        this.setFont(Font.font(field.getFontFamily(), fw, field.getFontSize()));
        if(field.getFieldType() == FieldType.TEXT) { setText(field.getFieldName()); repeatin.set(field.isRepeating()); }
        if(field.getFieldType() == FieldType.ARRAY) { setText(field.getFieldName()); array.setAll(field.getArray()); }
    }

    public ReadOnlyObjectProperty<FieldType> fieldTypeProperty() {
        return fieldType.getReadOnlyProperty();
    }
    
    public ReadOnlyBooleanProperty repeatingProperty() {
        if(repeatin != null) return repeatin.getReadOnlyProperty();
        else return null;
    }
    
    public ObservableList<String> arrayProperty() {
        if(array != null) return array;
        else return null;
    }
    
}
