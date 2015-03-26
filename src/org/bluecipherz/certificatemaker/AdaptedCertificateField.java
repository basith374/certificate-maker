package org.bluecipherz.certificatemaker;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by bazi on 23/3/15.
 */
public class AdaptedCertificateField {
    private String fieldName;
    private int x;
    private int y;
    private double fontSize;
    private String fontFamily;
    private boolean boldText;

    @XmlAttribute
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getFontSize() {
        return fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public boolean isBoldText() {
        return boldText;
    }

    public void setBoldText(boolean boldText) {
        this.boldText = boldText;
    }
}
