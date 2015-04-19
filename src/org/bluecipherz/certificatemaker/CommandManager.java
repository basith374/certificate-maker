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

    private ArrayList<Command> commands = new ArrayList<>();
    private int currentLocation = -1;
    private int saveLocation = currentLocation;
    
    public CommandManager() {
        
    }
    
    public void add(Command c) {
        clearInFrontOfCurrent();
        c.execute();
        commands.add(c);
        currentLocation++;
    }
    
    public void undo() {
        if(undoable()) { // check not necessary, can be done at upper level
            commands.get(currentLocation).undo();
            currentLocation--;
            Debugger.log("undoing"); // debug
        } else {
            Debugger.log("no undo commands left"); // debug
        }
    }
    
    public void redo() {
        if(redoable()) { // check not necessary, can be done at upper level
            currentLocation++;
            commands.get(currentLocation).execute();
            Debugger.log("redoing");
        } else {
            Debugger.log("no redo commands left"); // debug
        }
    }
    
    public boolean undoable() {
        return currentLocation >= 0;
    }
    
    public boolean redoable() {
        return currentLocation < commands.size() - 1;
    }
    
    private void clearInFrontOfCurrent() {
        while(currentLocation < commands.size() - 1) {
            commands.remove(currentLocation + 1);
        }
    }
    
    public boolean dirty() {
        return currentLocation != saveLocation;
    }
    
    public void markSaveLocation() {
        saveLocation = currentLocation;
    }
    
}
