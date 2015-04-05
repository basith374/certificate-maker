/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
    private List<String> coursesDetails;
    private int width;
    private int height;
    private boolean repeating;

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public CertificateField() {
        
    }
    
    public CertificateField(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @XmlAttribute
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @XmlAttribute
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
    
    @XmlElement(name = "course")
    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
//        System.out.println("setCourses() called"); // debug
        this.courses = courses;
    }

    @XmlElement(name = "coursedetails")
    public List<String> getCoursesDetails() {
        return coursesDetails;
    }

    public void setCoursesDetails(List<String> coursesDetails) {
        this.coursesDetails = coursesDetails;
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
            output = output.concat(", fontsize=" + fontSize + ", fontfamily=" + fontFamily + ", fontstyle=" + fontStyle);
            if(fieldType == FieldType.COURSE) {
                output = output.concat(", courses : ");
                if(courses != null) {
                    if(!courses.isEmpty()) {
                        for(String course : courses) {
                            output = output.concat(course+",");
                        }
                    } else {
                        System.out.println("Courses is empty");
                    }
                } else {
                    System.out.println("Courses has become null.");
                }
            }
        }
        return output.concat("}\n");
    }
    
}
