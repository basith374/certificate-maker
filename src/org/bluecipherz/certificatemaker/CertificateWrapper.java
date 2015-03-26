package org.bluecipherz.certificatemaker;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.File;
import java.util.List;

/**
 * Created by bazi on 23/3/15.
 */
@XmlRootElement(name="certificatefields")
public class CertificateWrapper {
    private List<CertificateField> certificateFields;
    private File image;
    private String name;

    public String filePath;
    public boolean changed = false;

    private int imageX;

    @XmlElement(name = "imagex")
    public int getImageX() {
        return imageX;
    }

    public void setImageX(int imageX) {
        this.imageX = imageX;
    }

    @XmlElement(name = "imagex")
    public int getImageY() {
        return imageY;
    }

    public void setImageY(int imageY) {
        this.imageY = imageY;
    }

    private int imageY;

    @XmlElement(name = "certificatename")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "imagepath")
    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }

//    @XmlElement(name = "certificatefield")
    @XmlJavaTypeAdapter(CertificateFieldAdapter.class)
    public List<CertificateField> getCertificateFields() {
        return certificateFields;
    }

    public void setCertificateFields(List<CertificateField> certificateFields) {
        this.certificateFields = certificateFields;
    }

}
