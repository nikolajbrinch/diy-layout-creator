package org.diylc.app.platform;

import org.diylc.core.SystemUtils;

public class PlatformStrategy {

    private static Platform platform;
    
    private static PlatformStrategy platformStrategy = new PlatformStrategy();
    
    private PlatformStrategy() {
        if (SystemUtils.isMac()) {
            platform = MacPlatform.getInstance();
        } else if (SystemUtils.isWindows()) {
            platform = WindowsPlatform.getInstance();
        } else if (SystemUtils.isUnix()) {
            platform = UnixPlatform.getInstance();
        } else {
            platform = DefaultPlatform.getInstance(); 
        }
    }
    
    public static Platform getPlatform() {
        return platform;
    }

}
