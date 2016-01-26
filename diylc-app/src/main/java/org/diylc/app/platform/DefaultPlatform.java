package org.diylc.app.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPlatform implements Platform {

    private static final Logger LOG = LoggerFactory.getLogger(Platform.class);
    
    private static DefaultPlatform defaultPlatform = new DefaultPlatform();

    public static Platform getInstance() {
        return defaultPlatform;
    }

    @Override
    public void setup() {
        LOG.debug("Java version: " + System.getProperty("java.runtime.version") + " by " + System.getProperty("java.vm.vendor"));
        LOG.debug("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
    }

    @Override
    public void setAbouthandler(AboutHandler aboutHandler) {
    }

    @Override
    public void setPreferencesHandler(PreferencesHandler preferencesHandler) {
    }

    @Override
    public void setQuitHandler(QuitHandler quitHandler) {
    }

}
