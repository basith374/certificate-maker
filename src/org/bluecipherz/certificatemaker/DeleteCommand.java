/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

/**
 *
 * @author bazi
 */
public class DeleteCommand implements Command {

    private Node _target;
    private Group _parent;
    
    public DeleteCommand(Node node) {
        _target = node;
    }
    
    @Override
    public void execute() {
        if(_target instanceof CertificateText) {
            CertificateText text = (CertificateText) _target;
            _parent = (Group) text.getParent();
            _parent.getChildren().remove(text);
        } else {
            ImageView image = (ImageView) _target;
            Group parent = (Group) image.getParent();
            parent.getChildren().remove(image);
        }
    }

    @Override
    public void undo() {
        _parent.getChildren().add(_target);
    }
    
}
