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

import javafx.scene.image.Image;

/**
 * ResourceManager singleton
 * @author bazi
 */
public class ResourceManger {
    
    private static final ResourceManger INSTANCE = new ResourceManger();
    
    public enum SIZE {
        SMALL,
        MEDIUM,
        LARGE
    }
    
    public Image crossx1;
    
    public Image addimgx32;
    public Image addx32;
    public Image delx32;
    public Image editx32;
    public Image movex32;
    public Image exitx16;
    public Image savex16;
    public Image saveasx16;
    public Image newtempx16;
    public Image newx16;
    public Image opentempx16;
    public Image openex16;
    public Image avatarx160;
    public Image avatarx1500;
    public Image avatarx15002;
    public Image iconx16;
    public Image iconx32;
    public Image iconx48;
    public Image iconx100;
    public Image splash;
    public Image avatarx32;
    public Image avatarx320;
    public Image errorx16;
    public Image errorx32;
    public Image errorx48;
    public Image questionx16;
    public Image questionx32;
    public Image questionx48;
    public Image infox16;
    public Image infox32;
    public Image infox48;
    public Image warnx16;
    public Image warnx32;
    public Image warnx48;

    private ResourceManger() { }
    
    public static ResourceManger getInstance() {
        return INSTANCE;
    }
    
    public void loadAppResources() {
        Debugger.log("[ResourceManager] Loading app resources...");
        this.addimgx32 = new Image(getClass().getResourceAsStream("icons/addimgx32.png"));
        this.addx32 = new Image(getClass().getResourceAsStream("icons/addx32.png"));
        this.delx32 = new Image(getClass().getResourceAsStream("icons/delx32.png"));
        this.editx32 = new Image(getClass().getResourceAsStream("icons/editx32.png"));
        this.movex32 = new Image(getClass().getResourceAsStream("icons/movex32.png"));
        this.exitx16 = new Image(getClass().getResourceAsStream("icons/exitx16.png"));
        this.savex16 = new Image(getClass().getResourceAsStream("icons/savex16.png"));
        this.saveasx16 = new Image(getClass().getResourceAsStream("icons/saveasx16.png"));
        this.newtempx16 = new Image(getClass().getResourceAsStream("icons/newtempx16.png"));
        this.newx16 = new Image(getClass().getResourceAsStream("icons/newx16.png"));
        this.opentempx16 = new Image(getClass().getResourceAsStream("icons/opentempx16.png"));
        this.openex16 = new Image(getClass().getResourceAsStream("icons/openx16.png"));
        this.avatarx32 = new Image(getClass().getResourceAsStream("icons/avatarx32.png"));
        this.avatarx160 = new Image(getClass().getResourceAsStream("icons/avatarx160.png"));
        this.avatarx320 = new Image(getClass().getResourceAsStream("icons/avatarx320.png"));
        this.avatarx1500 = new Image(getClass().getResourceAsStream("icons/avatarx1500.png"));
        this.avatarx15002 = new Image(getClass().getResourceAsStream("icons/avatarx15002.png"));
        this.iconx16 = new Image(getClass().getResourceAsStream("icons/iconx16.png"));
        this.iconx32 = new Image(getClass().getResourceAsStream("icons/iconx32.png"));
        this.iconx48 = new Image(getClass().getResourceAsStream("icons/iconx48.png"));
        this.iconx100 = new Image(getClass().getResourceAsStream("icons/iconx100.png"));
        this.errorx16 = new Image(getClass().getResourceAsStream("icons/errorx16.png"));
        this.errorx32 = new Image(getClass().getResourceAsStream("icons/errorx32.png"));
        this.errorx48 = new Image(getClass().getResourceAsStream("icons/errorx48.png"));
        this.questionx16 = new Image(getClass().getResourceAsStream("icons/questionx16.png"));
        this.questionx32 = new Image(getClass().getResourceAsStream("icons/questionx32.png"));
        this.questionx48 = new Image(getClass().getResourceAsStream("icons/questionx48.png"));
        this.infox16 = new Image(getClass().getResourceAsStream("icons/infox16.png"));
        this.infox32 = new Image(getClass().getResourceAsStream("icons/infox32.png"));
        this.infox48 = new Image(getClass().getResourceAsStream("icons/infox48.png"));
        this.warnx16 = new Image(getClass().getResourceAsStream("icons/warnx16.png"));
        this.warnx32 = new Image(getClass().getResourceAsStream("icons/warnx32.png"));
        this.warnx48 = new Image(getClass().getResourceAsStream("icons/warnx48.png"));
        
        this.crossx1 = new Image(getClass().getResourceAsStream("icons/crossx1.png"));
    }
    
    public void loadSplashResource() {
//        Debugger.log("[ResourceManager] Loading splash resources...");
        this.splash = new Image(getClass().getResourceAsStream("icons/premiumsplash.png"));
//        this.splash = new Image(getClass().getResourceAsStream("icons/demosplash.png"));
    }
    
}
