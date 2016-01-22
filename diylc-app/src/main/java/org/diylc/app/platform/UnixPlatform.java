package org.diylc.app.platform;


public class UnixPlatform extends DefaultPlatform {

    private static UnixPlatform unixPlatform = new UnixPlatform();

    public static Platform getInstance() {
        return unixPlatform;
    }

}
