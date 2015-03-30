/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author bazi
 */
public class CertificateText extends Text {
    
    private FieldType fieldType;

    CertificateText(CertificateField certificateField) {
        fieldType = certificateField.getFieldType();
        if(certificateField.getFieldType() == FieldType.TEXT) {
            this.setText(certificateField.getFieldName());
        } else {
            this.setText(certificateField.getFieldType().toString());
            System.out.println("Text : " + certificateField.getFieldType().toString()); // debug
        }
        this.setX(certificateField.getX());
        this.setY(certificateField.getY());
        FontWeight fw = certificateField.getFontStyle() == java.awt.Font.BOLD ? FontWeight.BOLD : FontWeight.NORMAL; // a lil messy
        this.setFont(Font.font(certificateField.getFontFamily(), fw, certificateField.getFontSize()));
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }
    
}
