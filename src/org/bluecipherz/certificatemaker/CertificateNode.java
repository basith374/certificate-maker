/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import javafx.scene.Node;

/**
 *
 * @author bazi
 */
public interface CertificateNode {
    
    public void setContainer(CertificateTab ct);
    public CertificateTab getContainer();
    public CertificateField getObserver();
    public void setObserver(CertificateField cf);
    public void setX(int x);
    public int getX();
    public void setY(int y);
    public int getY();
    public FieldType getFieldType();
    public Node get();
    public void setAttributes(CertificateField changes);
    public CertificateField getAttributes();
    
}
