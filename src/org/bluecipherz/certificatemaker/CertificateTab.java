/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import java.io.File;
import javafx.scene.control.Tab;

/**
 *
 * @author bazi
 */
public class CertificateTab extends Tab {
    
    private boolean changed;
    private File file;
    private CertificateWrapper certificateWrapper;
    
    private boolean courseFieldAdded;
    private boolean courseDetailsFieldAdded;
    private boolean avatarFieldAdded;
    private boolean dateFieldAdded;
    private boolean regnoFieldAdded;

    public boolean isCourseDetailsFieldAdded() {
        return courseDetailsFieldAdded;
    }

    public void setCourseDetailsFieldAdded(boolean courseDetailsFieldAdded) {
        this.courseDetailsFieldAdded = courseDetailsFieldAdded;
    }

    public CertificateTab() {
        courseFieldAdded = false;
        courseDetailsFieldAdded = false;
        avatarFieldAdded = false;
        dateFieldAdded = false;
        regnoFieldAdded = false;
    }

    public boolean isCourseFieldAdded() {
        return courseFieldAdded;
    }

    public void setCourseFieldAdded(boolean courseFieldAdded) {
        this.courseFieldAdded = courseFieldAdded;
    }

    public boolean isAvatarFieldAdded() {
        return avatarFieldAdded;
    }

    public void setAvatarFieldAdded(boolean avatarFieldAdded) {
        this.avatarFieldAdded = avatarFieldAdded;
    }

    public boolean isDateFieldAdded() {
        return dateFieldAdded;
    }

    public void setDateFieldAdded(boolean dateFieldAdded) {
        this.dateFieldAdded = dateFieldAdded;
    }

    public boolean isRegnoFieldAdded() {
        return regnoFieldAdded;
    }

    public void setRegnoFieldAdded(boolean regnoFieldAdded) {
        this.regnoFieldAdded = regnoFieldAdded;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public CertificateWrapper getCertificateWrapper() {
        return certificateWrapper;
    }

    public void setCertificateWrapper(CertificateWrapper certificateWrapper) {
        this.certificateWrapper = certificateWrapper;
    }
    
}
