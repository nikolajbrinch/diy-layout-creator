package org.diylc.app.platform;


public class WindowsPlatform extends DefaultPlatform {

    private static WindowsPlatform windowsPlatform = new WindowsPlatform();

    public static Platform getInstance() {
        return windowsPlatform;
    }

}
