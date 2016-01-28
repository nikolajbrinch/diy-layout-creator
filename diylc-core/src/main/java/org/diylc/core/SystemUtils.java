package org.diylc.core;

import java.io.File;

public class SystemUtils {

    private static String getOsString() {
        return System.getProperty("os.name").toLowerCase();
    }

    public static boolean isWindows() {
        return (getOsString().indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (getOsString().indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (getOsString().indexOf("nix") >= 0 || getOsString().indexOf("nux") >= 0);
    }

    public static boolean isSolaris() {
        return (getOsString().indexOf("sunos") >= 0);
    }

    public static File getUserHome() {
        return new File(System.getProperty("user.home"));
    }

    public static File getConfigDirectory() {
        return new File(getUserHome(), ".diylc");
    }

    public static File getConfigFile(String filename) {
        return new File(getConfigDirectory(), filename);
    }

    public static String getDefaultDisplayFontName() {
        return isMac() ? "Helvetica Neue" : "Tahoma";
    }

    public static String getDefaultMonospacedFontName() {
        return isMac() ? "Monaco" : "Courier New";
    }

    public static String getDefaultTextFontName() {
        return isMac() ? "Arial" : "Calibri";
    }

}
