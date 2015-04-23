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

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This class has been declared stable. further changes will affect the overall integrity of the application.
 * DANGER ZONE! dont edit anything here!
 * @author bazi
 */
public class CertificateFieldAdapter extends XmlAdapter<CertificateField, CertificateField> {

    @Override
    public CertificateField unmarshal(CertificateField v) throws Exception {
        if(v.getFieldType() == FieldType.IMAGE) {
            Debugger.log("[CertificateFieldAdapter] unmarshalling IMAGE at " + v.getX() + "," + v.getY() + ", " + v.getFieldType()); // debug
            return new CertificateField(v.getX(), v.getY(), FieldType.IMAGE, v.getWidth(), v.getHeight());
        } else if(v.getFieldType() == FieldType.TEXT) {
            Debugger.log("[CertificateFieldAdapter] unmarshalling TEXT at " + v.getX() + "," + v.getY() + ", " + v.getFieldType()); // debug
            return new CertificateField(v.getX(), v.getY(), v.getFieldType(), v.getFontFamily(), v.getFontSize(), v.getFontStyle(), v.getFieldName(), v.isRepeating());
        } else if(v.getFieldType() == FieldType.ARRAY) {
//            Debugger.log("[CertificateFieldAdapter] unmarshalling ARRAY at " + v.getX() + "," + v.getY() + ", " + v.getFieldType() + ", array :" + v.getArray()); // debug
            return new CertificateField(v.getX(), v.getY(), v.getFieldType(), v.getFontFamily(), v.getFontSize(), v.getFontStyle(), v.getFieldName(), v.getArray());
        } else { // REGNO, DATE
            if(v.getX() == null) Debugger.log("[CertificateFieldAdapter] x is null"); // debug
            if(v.getY() == null) Debugger.log("[CertificateFieldAdapter] y is null"); // debug
            if(v.getFieldType() == null) Debugger.log("[CertificateFieldAdapter] field type is null"); // debug
            Debugger.log("[CertificateFieldAdapter] unmarshalling (DATE or REGNO) at " + v.getX() + "," + v.getY() + ", " + v.getFieldType()); // debug
            return new CertificateField(v.getX(), v.getY(), v.getFieldType(), v.getFontFamily(), v.getFontSize(), v.getFontStyle());
        }
    }

    @Override
    public CertificateField marshal(CertificateField v) throws Exception {
        if(v.getFieldType() == null) Debugger.log("[CeritificateFieldAdapter] MARSHALL ERROR : no field type specified");
        if(v.getFieldType() == FieldType.IMAGE) {
            Debugger.log("[CertificateFieldAdapter] avatar dimensions : " + v.getWidth() + "," + v.getHeight()); // debug
            return new CertificateField(v.getX(), v.getY(), FieldType.IMAGE, v.getWidth(), v.getHeight());
        } else if(v.getFieldType() == FieldType.TEXT) {
            Debugger.log("[CertificateFieldAdapter] font size :" + v.getFontSize() + ", repeating :" + v.isRepeating()); // debug
            return new CertificateField(v.getX(), v.getY(), v.getFieldType(), v.getFontFamily(), v.getFontSize(), v.getFontStyle(), v.getFieldName(), v.isRepeating());
        } else if(v.getFieldType() == FieldType.ARRAY) {
            return new CertificateField(v.getX(), v.getY(), v.getFieldType(), v.getFontFamily(), v.getFontSize(), v.getFontStyle(), v.getFieldName(), v.getArray());
        } else { // REGNO, DATE
            return new CertificateField(v.getX(), v.getY(), v.getFieldType(), v.getFontFamily(), v.getFontSize(), v.getFontStyle());
        }
    }
    
}
