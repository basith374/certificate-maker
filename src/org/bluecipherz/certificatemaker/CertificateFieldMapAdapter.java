/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author bazi
 */
public class CertificateFieldMapAdapter extends XmlAdapter<CertificateFieldMapType, Map<FieldType, CertificateField>>{

    @Override
    public Map<FieldType, CertificateField> unmarshal(CertificateFieldMapType v) throws Exception {
        System.out.println("Using certificatefieldmapadapter");
        HashMap<FieldType, CertificateField> map = new HashMap<>();
        for(CertificateFieldMapEntry entry : v.list) {
//            CertificateField field = new CertificateField(entry.value.getX(), entry.value.getY());
//            if(entry.key != FieldType.IMAGE) {
//                if(entry.key == FieldType.TEXT) field.setFieldName(entry.value.getFieldName()); // add the field name if its text type
//                if(entry.key == FieldType.COURSE) field.setCourses(entry.value.getCourses()); // only add course fields if its a course type
//                field.setFontFamily(entry.value.getFontFamily()); // this applies for all(text, course, date, regno)
//                field.setFontSize(entry.value.getFontSize()); // this applies for all(text, course, date, regno)
//                field.setFontStyle(entry.value.getFontStyle()); // this applies for all(text, course, date, regno)
//            }
//            map.put(entry.key, field);
            map.put(entry.key, entry.value);
        }
        return map;
    }

    @Override
    public CertificateFieldMapType marshal(Map<FieldType, CertificateField> v) throws Exception {
        CertificateFieldMapType mapType = new CertificateFieldMapType();
        for(Entry<FieldType, CertificateField> entry : v.entrySet()) {
            CertificateFieldMapEntry mapEntry = new CertificateFieldMapEntry();
            mapEntry.key = entry.getKey();
            mapEntry.value = entry.getValue();
            
//            mapEntry.value.setX(entry.getValue().getX());
//            mapEntry.value.setY(entry.getValue().getY());
//            
//            if(entry.getKey() != FieldType.IMAGE) {
//                if(entry.getKey() == FieldType.TEXT) mapEntry.value.setFieldName(entry.getValue().getFieldName());
//                if(entry.getKey() == FieldType.COURSE) mapEntry.value.setCourses(entry.getValue().getCourses());
//                mapEntry.value.setFontFamily(entry.getValue().getFontFamily());
//                mapEntry.value.setFontSize(entry.getValue().getFontSize());
//                mapEntry.value.setFontStyle(entry.getValue().getFontStyle());
//            }
            
            mapType.list.add(mapEntry);
        }
        return mapType;
    }
    
    
}
