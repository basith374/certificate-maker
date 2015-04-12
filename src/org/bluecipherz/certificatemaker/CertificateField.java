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

import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
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
    private double fontSize;
    private String fontFamily;
    private String fontStyle; // depending on java.awt.Font (eg. Font.BOLD, Font.PLAIN)
    private List<String> array;
    private int width;
    private int height;
    private boolean repeating;

    public boolean isRepeating() {
        return repeating;
    }

    @XmlElement(name = "array")
    public List<String> getArray() {
        return array;
    }

    public void setArray(List<String> array) {
        this.array = array;
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
    
    @XmlAttribute
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
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

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }
    
    public void observe(final Node node) {
        if(node instanceof CertificateText) {
            CertificateText text = (CertificateText) node;
            text.boundsInLocalProperty().addListener(new ChangeListener<Bounds>() {
                @Override
                public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds newBounds) {
                    int x = (int) (newBounds.getMinX() + newBounds.getMaxX() / 2);
//                    int y = (int) (newBounds.getMinY() + newBounds.getMaxY() / 2);
                    int y = (int) newBounds.getMinY();
                    setX(x);
                    setY(y);
                }
            });
            text.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, String t, String newText) {
                    setFieldName(newText);
                }
            });
            if(fieldType == FieldType.ARRAY) {
                text.arrayProperty().addListener(new ListChangeListener<String>() {
                    @Override
                    public void onChanged(ListChangeListener.Change<? extends String> change) {
                        ObservableList<String> list = (ObservableList<String>) change.getList();
                        setArray(list);
                    }
                });
            }
            if(fieldType == FieldType.TEXT) {
                text.repeatingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean repeating) {
                        setRepeating(repeating);
                    }
                });
            }
            text.fontProperty().addListener(new ChangeListener<Font>() {
                @Override
                public void changed(ObservableValue<? extends Font> ov, Font t, Font newFont) {
                    setFontFamily(newFont.getFamily());
                    setFontSize(newFont.getSize());
                    setFontStyle(newFont.getStyle());
                }
            });
        } else if(node instanceof ImageView) {
            ImageView imageView = (ImageView) node;
            imageView.boundsInLocalProperty().addListener(new ChangeListener<Bounds>() {
                @Override
                public void changed(ObservableValue<? extends Bounds> ov, Bounds t, Bounds newBounds) {
                    setX((int) newBounds.getMinX());
                    setY((int) newBounds.getMinY());
                }
            });
            imageView.imageProperty().addListener(new ChangeListener<Image>() {
                @Override
                public void changed(ObservableValue<? extends Image> ov, Image t, Image newImage) {
                    setWidth((int) newImage.getWidth());
                    setHeight((int) newImage.getHeight());
                }
            });
        }
    }
    
    @Override
    public String toString() {
//        return super.toString();
        String output = "";
        output = output.concat("\t{CertificateField : x=" + x + ", y=" + y + ", fieldtype=" + fieldType);
        if(fieldType != FieldType.IMAGE) {
            if(fieldType == FieldType.TEXT) output = output.concat(", fieldname=" + fieldName);
            output = output.concat(", fontsize=" + fontSize + ", fontfamily=" + fontFamily + ", fontstyle=" + fontStyle);
//            if(fieldType == FieldType.COURSE) {
//                output = output.concat(", courses : ");
//                if(courses != null) {
//                    if(!courses.isEmpty()) {
//                        for(String course : courses) {
//                            output = output.concat(course+",");
//                        }
//                    } else {
//                        System.out.println("Courses is empty"); // debug
//                    }
//                } else {
//                    System.out.println("Courses has become null."); // debug
//                }
//            }
        }
        return output.concat("}\n");
    }
    
}
