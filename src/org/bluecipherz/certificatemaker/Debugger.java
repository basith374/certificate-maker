/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

/**
 *
 * @author bazi
 */
public class Debugger {
    
    private static final boolean ENABLED = true;
    
//    public static void log(Object a, String text) {
//        if(isEnabled()) {
//            System.out.println("[DEBUG][CM][" + a.getClass().getSimpleName() + "]" + " " + text);
//        }
//    }
    
    public static void log(Object o) {
        if(ENABLED) {
            System.out.println(o.toString());
        }
    }
    
}
