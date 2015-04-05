/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bluecipherz.certificatemaker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author bazi
 */
public class RegexUtils {

    public String[] splitDigitAndLetters(String s) {
        String regex = "(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)";
        return s.split(regex);
    }
    
    public String incrementRegno(String regno) {
        Pattern digitPattern = Pattern.compile("(\\d+)");
        Matcher matcher = digitPattern.matcher(regno);
        StringBuffer result = new StringBuffer();
        if(matcher.find()) {
             // find if regno has leading zeros in it and append it to the result
            String s = matcher.group(1);
            Integer no = Integer.parseInt(s);
            no++;
//            System.out.println("incremented :" + no); // debug
            if(s.length() > no.toString().length()) {
//                System.out.println("fixing"); // debug
                int zeros = s.length() - no.toString().length();
                String a = "";
                for(int i = 0; i < zeros; i++)
                    a = a.concat("0");
                s = a.concat(no.toString());
            } else {
                s = no.toString();
            }
            //
//            System.out.println("fixed : " + s); // debug
            matcher.appendReplacement(result, s);
//            matcher.appendReplacement(result, String.valueOf(Integer.parseInt(matcher.group(1)) + 1));
        }
        matcher.appendTail(result);
        return result.toString();
    }
    
    public boolean isTrue(String s) {
        return s.matches("true");
    }
    
    public boolean isTrue2(String s) {
        return s.matches("[tT]rue");
    }
    
    public boolean isTrueOrYes(String s) {
        return s.matches("[tT]rue|[yY]es");
    }
    
    public boolean containsTrue(String s) {
        return s.matches(".*true.*");
    }
    
    public boolean isThreeLetters(String s) {
        return s.matches("[a-zA-Z]{3}");
    }
    
    public boolean isNoNumberAtBeginning(String s) {
        return s.matches("^[^\\d].*");
    }
    
    public boolean isIntersection(String s) {
        return s.matches("([\\w&&[^b]])*");
    }
    
    public boolean isLessThanThreeHundred(String s) {
        return s.matches("[^0-9]*[12]?[0-9]{1,2}[^0-9]*");
    }
}
