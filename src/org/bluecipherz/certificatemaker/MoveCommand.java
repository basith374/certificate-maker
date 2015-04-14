/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

/**
 *
 * @author bazi
 */
public class MoveCommand implements Command {

    private Node _target;
    private Point2D end;
    private Point2D start;
    
    public MoveCommand(Node node, Point2D start, Point2D end) {
        _target = node;
        this.end = end;
        this.start = start;
    }
    
    @Override
    public void execute() {
        if(_target instanceof CertificateText) {
            CertificateText text = (CertificateText) _target;
            text.setX(end.getX());
            text.setY(end.getY());
        } else {
            ImageView image = (ImageView) _target;
            image.setX(end.getX());
            image.setY(end.getY());
        }
    }

    @Override
    public void undo() {
        if(_target instanceof CertificateText) {
            CertificateText text = (CertificateText) _target;
            text.setX(start.getX());
            text.setY(start.getY());
        } else {
            ImageView image = (ImageView) _target;
            image.setX(start.getX());
            image.setY(start.getY());
        }
    }
    
}
