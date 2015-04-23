/*
 * Copyright (c) 2012-2015 BCZ Inc.
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
