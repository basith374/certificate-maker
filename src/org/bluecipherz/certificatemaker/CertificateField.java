/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author bazi
 */
@XmlJavaTypeAdapter(CertificateFieldAdapter.class)
public class CertificateField {

    private int x;
    private int y;
    private FieldType fieldType;
    private String fieldName;
    private int fontSize;
    private String fontFamily;
    private int fontStyle; // depending on java.awt.Font (eg. Font.BOLD, Font.PLAIN)
    private List<String> courses;


    public CertificateField() {
        
    }
    
    public CertificateField(List<String> courses) {
        this.courses = courses;
    }
    
    public CertificateField(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public CertificateField(int x, int y, String fieldName, int fontSize, String fontFamily, int fontStyle) {
        this.x = x;
        this.y = y;
        this.fieldName = fieldName;
        this.fontSize = fontSize;
        this.fontFamily = fontFamily;
        this.fontStyle = fontStyle;
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

    @XmlAttribute
    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }
    
    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }
    
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public int getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(int fontStyle) {
        this.fontStyle = fontStyle;
    }

    @Override
    public String toString() {
//        return super.toString();
        String output = "";
        output = output.concat("{CertificateField : x=" + x + ", y=" + y + ", fieldtype=" + fieldType);
        if(fieldType != FieldType.IMAGE) {
            if(fieldType == FieldType.TEXT) output = output.concat(", fieldname=" + fieldName);
            output = output.concat(", fontsize=" + fontSize + ", fontfamily=" + fontFamily);
        }
        return output.concat("}\n");
    }
    
}
