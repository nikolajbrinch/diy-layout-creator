package org.diylc.app.platform;

public interface Platform {

    public static Platform getPlatform() {
        return PlatformStrategy.getPlatform();
    }

    public void setup();

    public void setAbouthandler(AboutHandler aboutHandler);

    public void setPreferencesHandler(PreferencesHandler preferencesHandler);
    
    public void setQuitHandler(QuitHandler quitHandler);
    
}
