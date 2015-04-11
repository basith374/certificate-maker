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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * This class has been declared stable. further changes will affect the overall integrity of the application.
 * DANGER ZONE! dont edit anything here!
 * @author bazi
 */
public class CertificateFieldAdapter extends XmlAdapter<CertificateField, CertificateField> {

    @Override
    public CertificateField unmarshal(CertificateField v) throws Exception {
        CertificateField certificateField = new CertificateField(v.getX(), v.getY());
        if(v.getFieldType() == FieldType.IMAGE) {
            certificateField.setFieldType(FieldType.IMAGE);
            certificateField.setWidth(v.getWidth());
            certificateField.setHeight(v.getHeight());
        } else {
            certificateField.setFieldName(v.getFieldName());
            certificateField.setFieldType(v.getFieldType());
            certificateField.setFontFamily(v.getFontFamily());
            certificateField.setFontSize(v.getFontSize());
            certificateField.setFontStyle(v.getFontStyle());
            if(v.getFieldType() == FieldType.TEXT) certificateField.setRepeating(v.isRepeating());
            if(v.getFieldType() == FieldType.COURSE) certificateField.setCourses(v.getCourses()); // important!
            if(v.getFieldType() == FieldType.COURSEDETAILS) certificateField.setCoursesDetails(v.getCoursesDetails());
        }
        return certificateField;
    }

    @Override
    public CertificateField marshal(CertificateField v) throws Exception {
        CertificateField certificateField = new CertificateField(v.getX(), v.getY());
        if(v.getFieldType() == FieldType.IMAGE) {
            certificateField.setFieldType(FieldType.IMAGE);
            certificateField.setWidth(v.getWidth());
            certificateField.setHeight(v.getHeight());
        } else {
            certificateField.setFieldName(v.getFieldName());
            certificateField.setFieldType(v.getFieldType());
            certificateField.setFontFamily(v.getFontFamily());
            certificateField.setFontSize(v.getFontSize());
            certificateField.setFontStyle(v.getFontStyle());
            if(v.getFieldType() == FieldType.TEXT) certificateField.setRepeating(v.isRepeating());
            if(v.getFieldType() == FieldType.COURSE) certificateField.setCourses(v.getCourses()); // important!
            if(v.getFieldType() == FieldType.COURSEDETAILS) certificateField.setCoursesDetails(v.getCoursesDetails());
        }
        return certificateField;
    }
    
}
