/*
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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author bazi
 */
@XmlJavaTypeAdapter(CertificateFieldAdapter.class)
public class CertificateField implements Comparable {

    /*
     * FOR ORDERING
     * Used for sorting by Collections.sort() method.
     */
    public static final int DATE = 1;
    public static final int REGNO = 2;
    public static final int TEXTREPEATING = 3;
    public static final int TEXT = 4;
    public static final int ARRAY = 5;
    public static final int IMAGE = 6;
    
    /*
     * NEW PROPERTIES WAY, SO YOU CAN BIND THESE VALUES TO LOW LEVEL WIDGETS (for MVC)
     */
    private final IntegerProperty x; // 1st field
    private final IntegerProperty y; // 2nd field
    private final ObjectProperty<FieldType> fieldType; // 3rd field
    private final StringProperty fieldName; // 4th field
    private final StringProperty fontFamily; // 5th field
    private final ObjectProperty<Integer> fontSize; // 6th field
    private final StringProperty fontStyle; // 7th field
//    private final ObservableList<String> array; // 8th field
    private List<String> array; // 8th field
    private final ObjectProperty<Integer> width; // 9th field
    private final ObjectProperty<Integer> height; // 10th field
    private final ObjectProperty<Boolean> repeating; // 11th field
    
    /*
     * THE OLD WAY
     */
//    private int x;
//    private int y;
//    private FieldType fieldType;
//    private String fieldName;
//    private String fontFamily;
//    private Integer fontSize; // not primitive since some fields dont need this. so it becomes null. *
//    private String fontStyle;
//    private List<String> array;
//    private Integer width; // not primitive *
//    private Integer height; // not primitive *
//    private Boolean repeating;
    
    /* (*)these values are not primitive because JAXB does not write null values in XML files during
     * serialization which is Exactyl what we want. primitives cannot contain null values
     */
    
    // TODO yet to implement
//    private ReadOnlyBooleanWrapper changed = new ReadOnlyBooleanWrapper(false);
//
//    public ReadOnlyBooleanProperty changedProperty() {
//        return changed.getReadOnlyProperty();
//    }

    /**
     * No parameter constructor, required for serialization by JAXB(SHOULD NOT BE USED)
     */
    @Deprecated
    public CertificateField() {
        this(0, 0, null, null, null, null, null, null, null, null, null);
    }

    /**
     * FULL PARAMETER CONSTRUCTOR (CENTrAL CONSTRUCTOR, every other constructor points to this)
     * @param x
     * @param y
     * @param type
     * @param name
     * @param family
     * @param size
     * @param style
     * @param array
     * @param width
     * @param height
     * @param repeating 
     */
    @Deprecated
    public CertificateField(Integer x, Integer y, FieldType type, String name, String family, Integer size, String style, List<String> array, Integer width, Integer height, Boolean repeating) {
//        Debugger.log("[CeritificateField] Constructing CertificateField" + x + "," + y);
        this.x = new SimpleIntegerProperty(x);
        this.y = new SimpleIntegerProperty(y);
        this.fieldType = new SimpleObjectProperty<>(type);
        this.fieldName = new SimpleStringProperty(name);
        this.fontFamily = new SimpleStringProperty(family);
        this.fontSize = new SimpleObjectProperty(size);
        this.fontStyle = new SimpleStringProperty(style);
//        if(array != null) {
//            this.array = FXCollections.observableArrayList(array);
//        } else {
//            this.array = FXCollections.observableArrayList();
//        }
        if(array != null) {
            this.array = array;
        } else {
            this.array = new ArrayList<>();
        }
        this.width = new SimpleObjectProperty(width);
        this.height = new SimpleObjectProperty(height);
        this.repeating = new SimpleObjectProperty(repeating);
        
//        this.x = x;
//        this.y = y;
//        this.fieldType = type;
//        this.fieldName = name;
//        this.fontFamily = family;
//        this.fontSize = size;
//        this.fontStyle = style;
//        if(array != null) {
//            this.array = FXCollections.observableArrayList(array);
//        } else {
//            this.array = FXCollections.observableArrayList();
//        }
//        this.width = width;
//        this.height = height;
//        this.repeating = repeating;
    }

    /**
     * FOR TEXT
     * @param x
     * @param y
     * @param type
     * @param fontFamily
     * @param fontSize
     * @param fontStyle
     * @param fieldName
     * @param isRepeating 
     */
    public CertificateField(int x, int y, FieldType type, String fontFamily, Integer fontSize, String fontStyle, String fieldName, Boolean repeating) {
        this(x, y, type, fieldName, fontFamily, fontSize, fontStyle, null, null, null, repeating);
    }
    
    /**
     * FOR ARRAY
     * @param x
     * @param y
     * @param type
     * @param fontFamily
     * @param fontSize
     * @param fontStyle
     * @param fieldName
     * @param array 
     */
    public CertificateField(int x, int y, FieldType type, String fontFamily, Integer fontSize, String fontStyle, String fieldName, List<String> array) {
        this(x, y, type, fieldName, fontFamily, fontSize, fontStyle, array, null, null, null);
    }
    
    /**
     * For REGNO and DATE
     * @param x
     * @param y
     * @param type
     * @param fontFamily
     * @param fontSize
     * @param fontStyle 
     */
    public CertificateField(int x, int y, FieldType type, String fontFamily, Integer fontSize, String fontStyle) {
        this(x, y, type, null, fontFamily, fontSize, fontStyle, null, null, null, null);
    }
    
    /**
     * For IMAGE
     * @param x
     * @param y
     * @param type
     * @param width
     * @param height 
     */
    public CertificateField(int x, int y, FieldType type, Integer width, Integer height) {
        this(x, y, type, null, null, null, null, null, width, height, null);
    }
    
    /*
     * CERTIFICATE FIELD FIXED METHODS
     */

    @XmlAttribute
    public Integer getX() {
        return x.get();
//        return x;
    }

    public void setX(Integer x) {
        this.x.set(x);
//        this.x = x;
    }

    @XmlAttribute
    public Integer getY() {
        return y.get();
//        return y;
    }

    public void setY(Integer y) {
        this.y.set(y);
//        this.y = y;
    }

    @XmlAttribute
    public FieldType getFieldType() {
        return fieldType.get();
//        return fieldType;
    }
    
    public void setFieldType(FieldType type) {
        this.fieldType.set(type);
//        this.fieldType = type;
    }
     
    /*
     * FOR TEXT AND ARRAY
     */
    
    @XmlAttribute
    public String getFieldName() {
        Debugger.log("[CertificateField] getFieldName() : fieldType :" + getFieldType()); // debug
        return fieldName.get();
//        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName.set(fieldName);
//        this.fieldName = fieldName;
    }
    
    /*
     * FONT
     */
    
    @XmlAttribute
    public String getFontFamily() {
        return fontFamily.get();
//        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily.set(fontFamily);
//        this.fontFamily = fontFamily;
    }
    
    @XmlAttribute
    public Integer getFontSize() {
        return fontSize.get();
//        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize.set(fontSize);
//        this.fontSize = fontSize;
    }

    @XmlAttribute
    public String getFontStyle() {
        return fontStyle.get();
//        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle.set(fontStyle);
//        this.fontStyle = fontStyle;
    }

    /*
     * FOR ARRAY
     */
    
    @XmlElement(name = "value")
//    @XmlJavaTypeAdapter(ObservableListAdapter.class)
    public List<String> getArray() {
        return array;
    }

    public void setArray(List<String> array) {
//        this.array.setAll(array);
        this.array = array;
    }

    /*
     * FOR TEXT
     */
    
    @XmlAttribute
    public Boolean isRepeating() {
        return repeating.get();
//        return repeating;
    }

    public void setRepeating(Boolean repeating) {
        this.repeating.set(repeating);
//        this.repeating = repeating;
    }

    /*
     * FOR IMAGE
     */
    
    @XmlAttribute
    public Integer getWidth() {
        if(getFieldType() == FieldType.IMAGE) {
            if(width == null) Debugger.log("[CertificateField] ERROR : width field null"); else Debugger.log("[CertificateField] width field OKAY"); // debug
            if(width.get() == null) Debugger.log("[CertificateField] ERROR : width value null"); // debug
        }
        return width.get();
//        return width;
    }

    public void setWidth(Integer width) {
        this.width.set(width);
//        this.width = width;
    }

    @XmlAttribute
    public Integer getHeight() {
        if(getFieldType() == FieldType.IMAGE) {
            if(height == null) Debugger.log("[CertificateField] ERROR : height field null"); else Debugger.log("[CertificateField] height field OKAY"); // debug
            if(height.get() == null) Debugger.log("[CertificateField] ERROR : height value null"); // debug
        }
        return height.get();
//        return height;
    }

    public void setHeight(Integer height) {
        this.height.set(height);
//        this.height = height;
    }

    
    
    /*
     * RETURN PROPERTIES
     */
    
    public IntegerProperty xProperty() {
        return x;
    }

    public IntegerProperty yProperty() {
        return y;
    }

    public ObjectProperty<FieldType> fieldTypeProperty() {
        return fieldType;
    }
//    
//    public StringProperty fieldNameProperty() {
//        return fieldName;
//    }
//    
//    public StringProperty fontFamilyProperty() {
//        return fontFamily;
//    }
//    
//    public ObjectProperty<Integer> fontSizeProperty() {
//        return fontSize;
//    }
//    
//    public StringProperty fontStyleProperty() {
//        return fontStyle;
//    }
//    
//    public ObservableList<String> arrayProperty() {
//        return array;
//    }
//    
//    public ObjectProperty<Integer> widthProperty() {
//        return width;
//    }
//    
//    public ObjectProperty<Integer> heightProperty() {
//        return height;
//    }
//    
//    public ObjectProperty<Boolean> repeatingProperty() {
//        return repeating;
//    }
    
    /*
     * ADDITIONAL UTILITY METHODS
     */
    
    /**
     * Forms And Controls theory of Martin Fowler
     * @param node 
     */
    public void observe(final CertificateNode node) {
        node.setObserver(this); // backreference
        if(node instanceof CertificateText) {
            final CertificateText text = (CertificateText) node;
            text.parentProperty().addListener(new ChangeListener<Parent>() {
                @Override
                public void changed(ObservableValue<? extends Parent> ov, Parent t, Parent t1) {
//                    if(t1 == null) parent.remove(this); // done at certificate tab
//                    Debugger.log("field removed, wrapper contents:" + parent.size());
//                    changed.set(true); // TODO
                }
            });
            text.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
                @Override
                public void changed(ObservableValue<? extends Bounds> ov, Bounds oldBounds, Bounds newBounds) {
                    int x = (int) (text.getX() + text.getWidth() / 2);
                    int y = (int) text.getY();
                    setX(x);
                    setY(y);
//                    Debugger.log("component coords changed : " + x + "," + y); // debug
//                    changed.set(true); // TODO
                }
            });
            text.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, String t, String newText) {
                    setFieldName(newText);
//                    changed.set(true); // TODO
                }
            });
            if(getFieldType() == FieldType.ARRAY) {
                text.arrayProperty().addListener(new ListChangeListener<String>() {
                    @Override
                    public void onChanged(ListChangeListener.Change<? extends String> change) {
                        ObservableList<String> list = (ObservableList<String>) change.getList();
                        Debugger.log("[CeritificateField]array changed :" + change.getList().toString()); // debug
                        setArray(list);
//                        changed.set(true); // TODO
                    }
                });
            }
            if(getFieldType() == FieldType.TEXT) {
                text.repeatingProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean repeating) {
                        setRepeating(repeating);
//                        changed.set(true); // TODO
                    }
                });
            }
            text.fontProperty().addListener(new ChangeListener<Font>() {
                @Override
                public void changed(ObservableValue<? extends Font> ov, Font t, Font newFont) {
                    setFontFamily(newFont.getFamily());
                    setFontSize((int) newFont.getSize()); // CONVERSION!
                    setFontStyle(newFont.getStyle());
//                    changed.set(true); // TODO
                }
            });
        } else if(node instanceof CertificateAvatar) {
            final CertificateAvatar avatar = (CertificateAvatar) node;
            avatar.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
                @Override
                public void changed(ObservableValue<? extends Bounds> ov, Bounds t, Bounds newBounds) {
                    setX((int) avatar.getX());
                    setY((int) avatar.getY());
//                    changed.set(true); // TODO
                }
            });
            avatar.imageProperty().addListener(new ChangeListener<Image>() {
                @Override
                public void changed(ObservableValue<? extends Image> ov, Image t, Image newImage) {
                    setWidth((int) newImage.getWidth());
                    setHeight((int) newImage.getHeight());
//                    changed.set(true); // TODO
                }
            });
        }
    }
    
    @Override
    public String toString() {
//        return super.toString();
        String output = "";
        output = output.concat("\t{CertificateField : x=" + x + ", y=" + y + ", fieldtype=" + fieldType);
        if(getFieldType() != FieldType.IMAGE) {
            if(getFieldType() == FieldType.TEXT || getFieldType() == FieldType.ARRAY) output = output.concat(", fieldname=" + fieldName);
            output = output.concat(", fontsize=" + fontSize + ", fontfamily=" + fontFamily + ", fontstyle=" + fontStyle);
            if(getFieldType() == FieldType.ARRAY) {
                output = output.concat(", values : ");
                if(array != null) {
                    if(!array.isEmpty()) {
                        for(String element : array) {
                            output = output.concat(element+",");
                        }
                    } else {
                        Debugger.log("[CertificateField] array is empty"); // debug
                    }
                } else {
                    Debugger.log("array has become null."); // debug
                }
            }
        }
        return output.concat("}\n");
    }

    /**
     * Used for comparing
     * @return 
     */
    public int getOrderIndex() {
        if(getFieldType() == FieldType.DATE) return DATE;
        if(getFieldType() == FieldType.REGNO) return REGNO;
        if(getFieldType() == FieldType.TEXT)
            if(isRepeating()) return TEXTREPEATING; else return TEXT;
        if(getFieldType() == FieldType.ARRAY) return ARRAY;
        if(getFieldType() == FieldType.IMAGE) return IMAGE;
        return 0; // if nothing matches(WHICH IS QUITE IMPOSSIBLE, unless something new is added to FieldType enum)
    }
    
    @Override
    public int compareTo(Object o) {
        if(o instanceof CertificateField) {
            CertificateField field = (CertificateField) o;
            return this.getOrderIndex() - field.getOrderIndex(); // ascending order
//            return field.getOrderIndex() - this.getOrderIndex(); // descending order
        } else throw new IllegalArgumentException("Parameter must be CertificateField...");
    }
    
}
