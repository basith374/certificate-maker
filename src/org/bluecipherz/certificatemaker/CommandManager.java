package org.bluecipherz.certificatemaker;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.bluecipherz.certificatemaker.Command;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author bazi
 */
public class CommandManager {

    private LinkedList<Command> prevCommands = new LinkedList<>();
    private LinkedList<Command> futureCommands = new LinkedList<>();
    
    public CommandManager() {
        
    }
    
    public void addAndExecute(Command c) {
        c.execute();
        prevCommands.add(c);
    }
    
    public void undoLast() {
        if(undoable()) {
            Command command = prevCommands.removeLast();
            command.undo();
            futureCommands.add(command);
        } else {
            Debugger.log("no undo commands left");
        }
    }
    
    public void redoLast() {
        if(redoable()) {
            Command command = futureCommands.removeLast();
            command.execute();
            prevCommands.add(command);
        } else {
            Debugger.log("no redo commands left");
        }
    }
    
    public boolean undoable() {
        if(prevCommands.size() > 0) return true;
        else return false;
    }
    
    public boolean redoable() {
        if(futureCommands.size() > 0) return true;
        else return false;
    }
    
}
