package org.bluecipherz.certificatemaker;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by bazi on 23/3/15.
 */
@XmlRootElement(name="certificatewrapper")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class CertificateWrapper {
    private Map<FieldType,CertificateField> certificateFields;
    private String name;
    private File certificateImage;

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
    @XmlElement(name = "certificatefield")
    @XmlJavaTypeAdapter(CertificateFieldMapAdapter.class)
    public Map<FieldType, CertificateField> getCertificateFields() {
        return certificateFields;
    }

    public void setCertificateFields(Map<FieldType, CertificateField> certificateFields) {
        this.certificateFields = certificateFields;
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
        output = output.concat("\nfields={" + certificateFields.toString() + "}");
        // courses
//        output = output.concat("\ncourses={" + courses.toString() + "}");
        // end
        return output.concat("\n}");
    }
}
