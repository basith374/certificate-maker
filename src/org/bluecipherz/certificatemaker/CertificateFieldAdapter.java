package org.bluecipherz.certificatemaker;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by bazi on 23/3/15.
 */
public class CertificateFieldAdapter extends XmlAdapter<AdaptedCertificateField, CertificateField> {

    @Override
    public CertificateField unmarshal(AdaptedCertificateField v) throws Exception {
        return new CertificateField(v.getFieldName(), v.getX(), v.getY(), v.getFontFamily(), (int) v.getFontSize(), v.isBoldText());
    }

    @Override
    public AdaptedCertificateField marshal(CertificateField v) throws Exception {
        AdaptedCertificateField adaptedCertificateField = new AdaptedCertificateField();
        adaptedCertificateField.setFieldName(v.getFieldName());
        adaptedCertificateField.setX(v.getX());
        adaptedCertificateField.setY(v.getY());
        adaptedCertificateField.setFontFamily(v.getFontFamily());
        adaptedCertificateField.setFontSize(v.getFontSize());
        adaptedCertificateField.setBoldText(v.isBoldText());
        return adaptedCertificateField;
    }
}
