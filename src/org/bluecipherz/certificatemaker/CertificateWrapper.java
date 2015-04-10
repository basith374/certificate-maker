package org.bluecipherz.certificatemaker;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by bazi on 23/3/15.
 */
@XmlRootElement(name="certificatewrapper")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CertificateWrapper implements Cloneable {
    private ArrayList<CertificateField> certificateFields;
    private String name;
    private File certificateImage;

    @Override
    protected Object clone() {
        Object clone = null;
        try {
            clone = super.clone();
        } catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return clone;
    }

    @XmlAttribute(name = "certificateimage")
    public File getCertificateImage() {
        return certificateImage;
    }

    public void setCertificateImage(File certificateImage) {
        this.certificateImage = certificateImage;
    }

    @XmlAttribute(name = "certificatename")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    @XmlJavaTypeAdapter(CertificateFieldAdapter.class)
//    @XmlJavaTypeAdapter(CertificateFieldMapAdapter.class)
    @XmlElement(name = "certificatefield")
    public ArrayList<CertificateField> getCertificateFields() {
        return certificateFields;
    }

    public void setCertificateFields(ArrayList<CertificateField> certificateFields) {
        this.certificateFields = certificateFields;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CertificateWrapper) {
            CertificateWrapper wrapper = (CertificateWrapper) obj;
//            System.out.println("wrapper fields : " + certificateFields.size() + ", targetfields : " + wrapper.getCertificateFields().size()); // debug
            if(wrapper.name == null ? this.name != null : !wrapper.name.equals(this.name)) return false;
            if(!wrapper.certificateImage.equals(this.certificateImage)) return false;
            if(wrapper.getCertificateFields().size() != certificateFields.size()) return false;        
            if(!wrapper.certificateFields.equals(certificateFields)) return false;
            return true;
        } else return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.certificateFields);
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.certificateImage);
        return hash;
    }
    
    @Override
    public String toString() {
//        return super.toString();
        String output = "{";
        
        // name
        output = output.concat("Certificate Wrapper : name=" + name);
        // image
        output = output.concat(" image=" + certificateImage.toString());
        // fields
        output = output.concat(" fields={\n " + certificateFields.toString() + "}");
        // courses
//        output = output.concat("\ncourses={" + courses.toString() + "}");
        // end
        return output.concat("\n}");
    }
}
