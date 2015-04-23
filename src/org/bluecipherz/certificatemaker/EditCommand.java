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

/**
 *
 * @author bazi
 */
public class EditCommand implements Command {

    private final CertificateNode _target;
    private final CertificateField _oldstate;
    private final CertificateField _newstate;
    
    public EditCommand(CertificateNode node, CertificateField changes) {
        _target = node;
        _oldstate = node.getAttributes();
        _newstate = changes;
//        Debugger.log("editing : current state " + _oldstate.getFieldName()); // debug
    }
    
    @Override
    public void execute() {
        _target.setAttributes(_newstate);
//        Debugger.log("editing : new state " + _newstate.getFieldName()); // debug
    }

    @Override
    public void undo() {
        _target.setAttributes(_oldstate);
    }
    
}
