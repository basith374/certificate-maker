/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

/**
 *
 * @author bazi
 */
public interface Command {
    
    public void execute();
    
    public void undo();
    
}
