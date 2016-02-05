package org.diylc.core.platform;


public class UnixPlatform extends DefaultPlatform {

    private static UnixPlatform unixPlatform = new UnixPlatform();

    public static Platform getInstance() {
        return unixPlatform;
    }

}
