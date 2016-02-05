package org.diylc.core.platform;

import org.diylc.core.utils.SystemUtils;


public class WindowsPlatform extends DefaultPlatform {

    private static WindowsPlatform windowsPlatform = new WindowsPlatform();

    public static Platform getInstance() {
        return windowsPlatform;
    }
    
    public String getDefaultDisplayFontName() {
        return SystemUtils.getOsMajorVersion() >= 7 ? "Segoe UI" : "Tahoma";
    }

    public  String getDefaultMonospacedFontName() {
        return SystemUtils.getOsMajorVersion() >= 7 ? "Consolas" : "Courier New";
    }

    public  String getDefaultTextFontName() {
        return "Arial";
    }


}
