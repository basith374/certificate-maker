package org.bluecipherz.certificatemaker;

import javafx.beans.property.*;

/**
 * Created by bazi on 23/3/15.
 */
public class CertificateField {
//    private final StringProperty fieldName;
//    private final IntegerProperty x;
//    private final IntegerProperty y;
//    private final DoubleProperty fontSize;
//    private final StringProperty fontFamily;
//    private final BooleanProperty boldText;
    private final String fieldName;
    private final int x;
    private final int y;
    private final int fontSize;
    private final String fontFamily;
    private final boolean boldText;

    public String text;

    public CertificateField(String fieldName, int x, int y, String fontFamily, int fontSize, boolean boldText) {
//        this.fieldName = new SimpleStringProperty(fieldName);
//        this.x = new SimpleIntegerProperty(x);
//        this.y = new SimpleIntegerProperty(y);
//        this.fontSize = new SimpleDoubleProperty(fontSize);
//        this.fontFamily = new SimpleStringProperty(fontFamily);
//        this.boldText = new SimpleBooleanProperty(boldText);
        this.fieldName = fieldName;
        this.x = x;
        this.y = y;
        this.fontSize = fontSize;
        this.fontFamily = fontFamily;
        this.boldText = boldText;

    }

    public String getFieldName() {
//        return fieldName.get();
        return fieldName;
    }

    public int getX() {
//        return x.get();
        return x;
    }

    public int getY() {
//        return y.get();
        return y;
    }

    public String getFontFamily() {
//        return fontFamily.get();
        return fontFamily;
    }

    public int getFontSize() {
//        return fontSize.get();
        return fontSize;
    }

    public boolean isBoldText() {
//        return boldText.get();
        return boldText;
    }
}
