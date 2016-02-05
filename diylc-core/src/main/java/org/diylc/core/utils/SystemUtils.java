package org.diylc.core.utils;

import java.io.File;

public class SystemUtils {

    private static final int[] osVersion;
    
    static {
        int major = -1;
        int minor = -1;
        int patch = -1;
    
        String[] osVersionString = System.getProperty("os.version").split("\\.");
        if (osVersionString.length > 0) {
            major = Integer.valueOf(osVersionString[0]);
        }
        if (osVersionString.length > 1) {
            minor = Integer.valueOf(osVersionString[1]);
        }
        if (osVersionString.length > 2) {
            patch = Integer.valueOf(osVersionString[2]);
        }
        osVersion = new int[] {major, minor, patch};
    }
    
    private static String getOsName() {
        return System.getProperty("os.name").toLowerCase();
    }
    
    public static boolean isWindows() {
        return (getOsName().indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (getOsName().indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (getOsName().indexOf("nix") >= 0 || getOsName().indexOf("nux") >= 0);
    }

    public static boolean isSolaris() {
        return (getOsName().indexOf("sunos") >= 0);
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

    public static int getOsMajorVersion() {
        return osVersion[0];
    }

    public static int getOsMinorVersion() {
        return osVersion[1];
    }

    public static int getOsPatchVersion() {
        return osVersion[2];
    }
    
}
