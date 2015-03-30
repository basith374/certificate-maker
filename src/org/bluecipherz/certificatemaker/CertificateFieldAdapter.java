/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author bazi
 */
public class CertificateFieldAdapter extends XmlAdapter<CertificateField, CertificateField> {

    @Override
    public CertificateField unmarshal(CertificateField v) throws Exception {
        System.out.println("unmarshalling using certificatefieldadapter"); // debug
        CertificateField certificateField = new CertificateField(v.getX(), v.getY());
        if(v.getFieldType() == FieldType.IMAGE) {
            certificateField.setFieldType(FieldType.IMAGE);
        } else {
            certificateField.setFieldName(v.getFieldName());
            certificateField.setFieldType(v.getFieldType());
            certificateField.setFontFamily(v.getFontFamily());
            certificateField.setFontSize(v.getFontSize());
            certificateField.setFontStyle(v.getFontStyle());
        }
        return certificateField;
    }

    @Override
    public CertificateField marshal(CertificateField v) throws Exception {
        CertificateField certificateField = new CertificateField(v.getX(), v.getY());
        if(v.getFieldType() == FieldType.IMAGE) {
            certificateField.setFieldType(FieldType.IMAGE);
        } else {
            certificateField.setFieldName(v.getFieldName());
            certificateField.setFieldType(v.getFieldType());
            certificateField.setFontFamily(v.getFontFamily());
            certificateField.setFontSize(v.getFontSize());
            certificateField.setFontStyle(v.getFontStyle());
        }
        return certificateField;
    }
    
}
