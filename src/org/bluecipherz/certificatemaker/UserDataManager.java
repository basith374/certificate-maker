/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * 
 * @author bazi
 */
public class UserDataManager {

    private UserDataManager() { }
    
    private static Preferences prefs = Preferences.userNodeForPackage(UserDataManager.class);
    
    private static Gson gson = new Gson();
    
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
    
    public static List<String> getRecentTemplates() {
        List<String> recentList = null;
        try {
            File file = new File("recent.json");
            if(!file.exists()) file.createNewFile(); // fix 
            BufferedReader br = new BufferedReader(new FileReader("recent.json"));
            recentList = gson.fromJson(br, ArrayList.class);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UserDataManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserDataManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return recentList;
    }
    
    public static void setRecentTemplates(List<String> recentList) {
        String json = gson.toJson(recentList);
        try {
            FileWriter writer = new FileWriter("recent.json");
            writer.write(json);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(UserDataManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static boolean isA3Output() {
        return prefs.getBoolean("a3output", false);
    }
    
    public static void setA3Output(boolean val) {
        prefs.putBoolean("a3output", val);
    }
    
    public static boolean isAvatarImageProportionate() {
        return prefs.getBoolean("avatarproportionate", false);
    }
    
    public static void setAvatarImageProportionate(boolean val) {
        prefs.getBoolean("avatarproportionate", val);
    }
    
    public static String getDefaultImageFormat() {
        return prefs.get("imageFormat", "jpg");
    }
    
    public static void setDefaultImageFormat(String format) {
        if("jpg".equalsIgnoreCase(format) || "png".equalsIgnoreCase(format)) {
            System.out.println("DEFAULT IMAGE EXTENSION : " + format); // debug
            prefs.put("imageFormat", format);
        }
    }
    // END PERSISTENCE METHODS
    
}
