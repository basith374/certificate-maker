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

import java.io.File;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Tab;

/**
 *
 * @author bazi
 */
public class CertificateTab extends Tab {
    
//    private boolean changed;
    private File file;
    private CertificateWrapper certificateWrapper;
    
    private BooleanProperty dateFieldAdded;
    private BooleanProperty regnoFieldAdded;
    private BooleanProperty courseFieldAdded;
    private BooleanProperty courseDetailsFieldAdded;
    private BooleanProperty avatarFieldAdded;
    
    private BooleanProperty changed;
    
//    private boolean courseFieldAdded;
//    private boolean courseDetailsFieldAdded;
//    private boolean avatarFieldAdded;
//    private boolean dateFieldAdded;
//    private boolean regnoFieldAdded;

    public CertificateTab() {
        courseFieldAdded = new SimpleBooleanProperty(false);
        courseDetailsFieldAdded = new SimpleBooleanProperty(false);
        avatarFieldAdded = new SimpleBooleanProperty(false);
        dateFieldAdded = new SimpleBooleanProperty(false);
        regnoFieldAdded = new SimpleBooleanProperty(false);
        changed  = new SimpleBooleanProperty(false);
    }

    public boolean isCourseFieldAdded() {
        return courseFieldAdded.get();
    }

    public void setCourseFieldAdded(boolean courseFieldAdded) {
        this.courseFieldAdded.set(courseFieldAdded);
    }

    public boolean isAvatarFieldAdded() {
        return avatarFieldAdded.get();
    }

    public void setAvatarFieldAdded(boolean avatarFieldAdded) {
        this.avatarFieldAdded.set(avatarFieldAdded);
    }

    public boolean isDateFieldAdded() {
        return dateFieldAdded.get();
    }

    public void setDateFieldAdded(boolean dateFieldAdded) {
        this.dateFieldAdded.set(dateFieldAdded);
    }

    public boolean isRegnoFieldAdded() {
        return regnoFieldAdded.get();
    }

    public void setRegnoFieldAdded(boolean regnoFieldAdded) {
        this.regnoFieldAdded.set(regnoFieldAdded);
    }

    public boolean isCourseDetailsFieldAdded() {
        return courseDetailsFieldAdded.get();
    }

    public void setCourseDetailsFieldAdded(boolean courseDetailsFieldAdded) {
        this.courseDetailsFieldAdded.set(courseDetailsFieldAdded);
    }
    
    public boolean isChanged() {
        return changed.get();
    }

    public void setChanged(boolean changed) {
        this.changed.set(changed);
    }

    public BooleanProperty dateFieldProperty() {
        return dateFieldAdded;
    }
    public BooleanProperty regnoFieldProperty() {
        return dateFieldAdded;
    }
    public BooleanProperty courseFieldProperty() {
        return dateFieldAdded;
    }
    public BooleanProperty courseDetailsFieldProperty() {
        return dateFieldAdded;
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
