/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import javafx.geometry.Point2D;

/**
 *
 * @author bazi
 */
public class MoveCommand implements Command {

    private final CertificateNode _target;
    private final Point2D end;
    private final Point2D start;
    
    public MoveCommand(CertificateNode node, Point2D start, Point2D end) {
        _target = node;
        this.start = start;
        this.end = end;
//        Debugger.log("move command received , destination: " + end.getX() + "," + end.getY()); // debug
    }
    
    @Override
    public void execute() {
        int x = (int) end.getX();
        int y = (int) end.getY();
        _target.setX(x);
        _target.setY(y);
//        Debugger.log("final destination : " + x + "," + y); // debug
    }

    @Override
    public void undo() {
        _target.setX((int) start.getX());
        _target.setY((int) start.getY());
    }
    
}
