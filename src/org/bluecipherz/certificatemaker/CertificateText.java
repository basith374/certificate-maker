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

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author bazi
 */
public final class CertificateText extends Text {
    
    private CertificateField certificateField;

    public CertificateField getCertificateField() {
        return certificateField;
    }

    public void setCertificateField(CertificateField certificateField) {
        this.certificateField = certificateField;
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

    public CertificateText(CertificateField certificateField) {
        setCertificateField(certificateField);
    }
    
}
