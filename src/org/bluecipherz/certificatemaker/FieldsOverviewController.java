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

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 *
 * @author bazi
 */
public class FieldsOverviewController {

    private TableView<CertificateField> fieldTable;
    
    private TableColumn<CertificateField, Integer> xColumn;
    private TableColumn<CertificateField, Integer> yColumn;
    private TableColumn<CertificateField, FieldType> fieldTypeColumn;
//    private TableColumn<CertificateField, String> fieldNameColumn;
//    private TableColumn<CertificateField, String> fontFamilyColumn;
//    private TableColumn<CertificateField, String> fontSizeColumn;
//    private TableColumn<CertificateField, String> fontStyleColumn;
    
    public FieldsOverviewController() {
    }
    
    private void initialize() {
        
    }
    
}
