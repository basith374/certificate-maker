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
    
    public static void log(Object o, Class c) {
        if(ENABLED) {
            System.out.println("[" + c.getSimpleName() + "] " + o.toString());
        }
    }
    
}
