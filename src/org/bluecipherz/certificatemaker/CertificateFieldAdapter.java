/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
