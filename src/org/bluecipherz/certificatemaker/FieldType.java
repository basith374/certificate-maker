/*
 * Copyright BCZ Inc. 2015.
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
public enum FieldType {    
    // if you change this, add anything remove anything or order anything
    // also change references in Window.class LabelDialog.class and also field wrapper classes
    TEXT("Text"), DATE("Date"), REGNO("Regno"), ARRAY("Array"), IMAGE("Image");
    /*
     * also make sure you visit these places after you edit this datastructure
     * clearOrIncrementFields
     *  certificate field adapter - marshall, unmarshall
     *  prepareandshowedittextdialog
     *  setdefaultvalues
     *  generatecertificatefield
     *  dynamic add remove component methods in labeldialog
     */

    private String name;
    
    FieldType(String s) {
        name = s;
    }
    
    public String getName() {
        return name;
    }
    
}
