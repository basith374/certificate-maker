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

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author bazi
 */
public final class CertificateText implements CertificateNode {
    
    // these values are binded to the observer
    private ReadOnlyObjectWrapper<FieldType> fieldType;
    private ReadOnlyBooleanWrapper repeatin;
    private ObservableList<String> array; // this can be downgraded to ArrayList.
    // the actual field that gets saved in the wrapper
    private CertificateField observer;
    private CertificateTab container;
    private Text text;
    
    // latest implemetation
//    private IntegerProperty x;
//    private IntegerProperty y;
//    private StringProperty textString;

    public String getText() {
        return text.getText();
    }

    public void setText(String s) {
        text.setText(s);
    }
    
    @Override
    public void setContainer(CertificateTab container) {
        this.container = container;
    }
    
    @Override
    public CertificateTab getContainer() {
        return container;
    }
    
    @Override
    public void setObserver(CertificateField observer) {
        this.observer = observer;
    }

    @Override
    public CertificateField getObserver() {
        return observer;
    }
    
    public CertificateText(CertificateField field) {
        text = new Text();
        text.setX(field.getX().doubleValue());
        text.setY(field.getY().doubleValue());
        String ff = field.getFontFamily();
        FontWeight fw = FontWeight.findByName(field.getFontStyle());
        double fs = field.getFontSize().doubleValue();
        text.setFont(Font.font(ff, fw, fs));
        
        if(field.getFieldType() == FieldType.TEXT || field.getFieldType() == FieldType.ARRAY) {
            Debugger.log("[CertificateText] setting TEXT or ARRAY specific text...");
            text.setText(field.getFieldName());
        }
        else text.setText(field.getFieldType().toString());
//            Debugger.log("Text : " + certificateField.getFieldType().toString()); // debug
        
        fieldType = new ReadOnlyObjectWrapper(field.getFieldType());
        if(field.getFieldType() == FieldType.TEXT) {
            Debugger.log("[CertificateText] setting TEXT specific repeating boolean...");
            repeatin = new ReadOnlyBooleanWrapper(field.isRepeating());
        }
        if(field.getFieldType() == FieldType.ARRAY) {
            Debugger.log("[CertificateText] setting ARRAY specific array...");
//            array = field.getArray(); // shallow copy, i think this will create problems
            array = FXCollections.observableArrayList(field.getArray()); // deep copy, not needed
//            array.setAll(field.getArray());
        }
        Debugger.log("Creating node from field " + field);
    }
    
    @Override
    public void setAttributes(CertificateField changes) {
        FontWeight fw = FontWeight.findByName(changes.getFontStyle());
        text.setFont(Font.font(changes.getFontFamily(), fw, changes.getFontSize()));
        if(changes.getFieldType() == FieldType.TEXT) { text.setText(changes.getFieldName()); repeatin.set(changes.isRepeating()); }
        if(changes.getFieldType() == FieldType.ARRAY) { text.setText(changes.getFieldName()); array.setAll(changes.getArray()); }
    }
    
    @Override
    public CertificateField getAttributes() {
        if(getFieldType() == FieldType.TEXT) {
            return new CertificateField(getX(), getY(), FieldType.TEXT, getFontFamily(), getFontSize(), getFontStyle(), getText(), getRepeating());
        } else { // ARRAY
            return new CertificateField(getX(), getY(), FieldType.TEXT, getFontFamily(), getFontSize(), getFontStyle(), getText(), getArray());
        }
    }

    public ObservableList<String> getArray() {
        return array;
    }
    
    public boolean getRepeating() {
        return repeatin.get();
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
    
    public ReadOnlyObjectProperty<Parent> parentProperty() {
        return text.parentProperty();
    }
    
    public StringProperty textProperty() {
        return text.textProperty();
    }
    
    public ReadOnlyObjectProperty<Bounds> layoutBoundsProperty() {
        return text.layoutBoundsProperty();
    }
    
    public ReadOnlyObjectProperty<Font> fontProperty() {
        return text.fontProperty();
    }
    
    @Override
    public void setX(int x) {
        text.setX(x);
    }
    
    @Override
    public int getX() {
        return (int) text.getX();
    }
    
    @Override
    public void setY(int y) {
        text.setY(y);
    }
    
    @Override
    public int getY() {
        return (int) text.getY();
    }
    
    @Override
    public FieldType getFieldType() {
        return fieldType.get();
    }
    
    public int getWidth() {
        return (int) text.getLayoutBounds().getWidth();
    }
    
    public int getHeight() {
        return (int) text.getLayoutBounds().getHeight();
    }

    public void setOnMousePressed(EventHandler<MouseEvent> mouseHandler) {
        text.setOnMousePressed(mouseHandler);
    }

    public void setOnMouseDragged(EventHandler<MouseEvent> mouseHandler) {
        text.setOnMouseDragged(mouseHandler);
    }

    public void setOnMouseReleased(EventHandler<MouseEvent> mouseHandler) {
        text.setOnMouseReleased(mouseHandler);
    }
    
    @Override
    public Text get() {
        return text;
    }

    public String getFontFamily() {
        return text.getFont().getFamily();
    }

    public Integer getFontSize() {
        return new Integer((int) text.getFont().getSize());
    }
    
    public String getFontStyle() {
        return text.getFont().getStyle();
    }
    
}
