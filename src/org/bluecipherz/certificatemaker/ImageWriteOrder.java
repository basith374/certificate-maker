/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import java.io.File;
import java.util.HashMap;

/**
 *
 * @author bazi
 */
class ImageWriteOrder {
    public final HashMap<CertificateField, String> fields;
//    public final File saveFile;
    public String savePath;
    public String saveName;

    public ImageWriteOrder(HashMap<CertificateField, String> fields, String savePath, String saveName) {
        this.fields = fields;
//        this.saveFile = saveFile;
        this.savePath = savePath;
        this.saveName = saveName;
    }
}
