package org.diylc.core.platform;

import org.diylc.core.utils.SystemUtils;

public class PlatformStrategy {

    private static Platform platform;
    
    @SuppressWarnings("unused")
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
