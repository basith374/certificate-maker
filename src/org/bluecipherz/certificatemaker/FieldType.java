/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

/**
 *
 * @author bazi
 */
public enum FieldType {    
    // if you change this, add anything remove anything or order anything
    // also change references in Window.class LabelDialog.class and also field wrapper classes
    TEXT("Text"), DATE("Date"), REGNO("Regno"), COURSE("Course"), IMAGE("Image");

    private String name;
    
    FieldType(String s) {
        name = s;
    }
    
    public String getName() {
        return name;
    }
    
}
