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
    
    private CertificateField certificateField;

    public CertificateField getCertificateField() {
        return certificateField;
    }

    public void setCertificateField(CertificateField certificateField) {
        this.certificateField = certificateField;
    }

    CertificateText(CertificateField certField) {
        this.certificateField = certField; // save this for now, use it later for COURSE
        if(certificateField.getFieldType() == FieldType.TEXT) {
            this.setText(certificateField.getFieldName());
        } else {
            this.setText(certificateField.getFieldType().toString());
//            System.out.println("Text : " + certificateField.getFieldType().toString()); // debug
        }
        this.setX(certificateField.getX());
        this.setY(certificateField.getY());
        FontWeight fw = certificateField.getFontStyle() == java.awt.Font.BOLD ? FontWeight.BOLD : FontWeight.NORMAL; // a lil messy
        this.setFont(Font.font(certificateField.getFontFamily(), fw, certificateField.getFontSize()));
    }
    
}
