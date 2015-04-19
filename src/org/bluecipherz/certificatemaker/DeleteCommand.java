/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

/**
 *
 * @author bazi
 */
public class DeleteCommand implements Command {

    private final CertificateNode _target;
    private final CertificateTab _parent;
    
    public DeleteCommand(CertificateNode node) {
        _target = node;
        _parent = node.getContainer();
    }
    
    @Override
    public void execute() {
        _parent.removeNode(_target);
    }

    @Override
    public void undo() {
        _parent.addNewNode(_target);
    }
    
}
