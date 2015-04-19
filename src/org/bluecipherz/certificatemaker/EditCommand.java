/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
