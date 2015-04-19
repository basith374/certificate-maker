/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

/**
 *
 * @author bazi
 */
public class AddCommand implements Command {

    private final CertificateNode _target;
    private final CertificateTab _parent;
    
    public AddCommand(CertificateNode node, CertificateTab tab) {
        _target = node;
        _parent = tab;
    }
    
    @Override
    public void execute() {
        _parent.addNewNode(_target);
    }

    @Override
    public void undo() {
        _parent.removeNode(_target);
    }
    
}
