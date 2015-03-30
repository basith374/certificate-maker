/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import java.io.File;
import java.util.prefs.Preferences;

/**
 * 
 * @author bazi
 */
public class UserDataManager {

    private UserDataManager() { }
    
    private static Preferences prefs = Preferences.userNodeForPackage(UserDataManager.class);
    
    /************************
     * PERSISTENCE METHODS
     ***********************/
    
    /**
     * save the certificate file path in the current tab.
     * @param file 
     */
    public static void setCertificateFilePath(File file) {
        if (file != null) {
            prefs.put("filePath", file.getPath());
            System.out.println("Saving file path for current tab : " + file.getPath()); // debug
            // TODO update title
        } else {
            prefs.remove("filePath");
            // title
        }
    }

    /**
     * used the get the file path for the certificate at current tab
     * @return 
     */
    public static File getCertificateFilePath() {
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * save the last path where the user opened or saved a file.
     * @param file 
     */
    public static void setLastActivityPath(File file) {
        if (file != null) {
//            if (file.isDirectory()) {
//                prefs.put("lastActivityPath", file.getAbsolutePath());
//            }
//            System.out.println("seperator : " + File.separator + ", seperatorchar : " + File.separatorChar);
            String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separatorChar));
//            System.out.println("saving activity path :" +path);
            prefs.put("lastActivityPath", path);
        }
    }

    /**
     * get the last path where the user opened or saved a file.
     * @return 
     */
    public static File getLastActivityPath() {
        String path = prefs.get("lastActivityPath", null);
        if (path != null) {
            return new File(path);
        } else {
            return null;
        }
    }

    /**
     * saves the default font size
     * @param defaultFontSize 
     */
    public static void setDefaultFontSize(String defaultFontSize) {
        prefs.put("defaultFontSize", defaultFontSize);
    }

    /**
     * used by outsiders
     * retrieves the saved default font size
     * @return 
     */
    public static String getDefaultFontSize() {
        return prefs.get("defaultFontSize", null);
    }

    /**
     * sets the default font family
     * @param defaultFontFamily 
     */
    public static void setDefaultFontFamily(String defaultFontFamily) {
        prefs.put("defaultFontFamily", defaultFontFamily);
    }

    /**
     * used by outsiders
     * retrieves the default font family
     * @return 
     */
    public static String getDefaultFontFamily() {
        return prefs.get("defaultFontFamily", null);
    }

    /**
     * saves the default font style
     * @param defaultFontStyle 
     */
    public static void setDefaultFontStyle(String defaultFontStyle) {
        prefs.put("defaultFontStyle", defaultFontStyle);
    }

    /**
     * used by outsiders
     * retrieves the saved font style
     * @return 
     */
    public static String getDefaultFontStyle() {
        return prefs.get("defaultFontStyle", null);
    }
    
    
    /**
     * save the last path where the user opened or saved a file.
     * @param file 
     */
    public static void setLastSavePath(File file) {
        if (file != null) {
            String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separatorChar));
            prefs.put("lastSavePath", path);
        }
    }

    /**
     * get the last path where the user opened or saved a file.
     * @return 
     */
    public static File getLastSavePath() {
        String path = prefs.get("lastSavePath", null);
        if (path != null) {
            return new File(path);
        } else {
            return null;
        }
    }
    
    public static void setAvatarImagePath(File file) {
        if(file != null) {
            String path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(File.separatorChar));
            prefs.put("avatarImagePath", path);
        }
    }

    public static File getAvatarImagePath() {
        String path = prefs.get("avatarImagePath", null);
        if(path != null) {
            return new File(path);
        } else {
            return null;
        }
    }

    public static void setCertificateSavePath(File file) {
        if(file != null) {
            prefs.put("certificateSavePath", file.getAbsolutePath());
        }
    }

    public static File getCertificateSavePath(File file) {
        String path = prefs.get("certificateSavePath", null);
        if(path != null) {
            return new File(path);
        } else {
            return null;
        }
    }
    
    // END PERSISTENCE METHODS
    
}
