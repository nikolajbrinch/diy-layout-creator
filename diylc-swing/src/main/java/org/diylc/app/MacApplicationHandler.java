package org.diylc.app;

import com.apple.eawt.AppEvent.PreferencesEvent;
import com.apple.eawt.Application;
import com.apple.eawt.PreferencesHandler;

public class MacApplicationHandler {

    @SuppressWarnings("deprecation")
    public static void setupMacApplication() {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.eawt.quitStrategy", "CLOSE_ALL_WINDOWS");

        Application application = Application.getApplication();
        application.setPreferencesHandler(new PreferencesHandler() {

            @Override
            public void handlePreferences(PreferencesEvent arg0) {
                // TODO Auto-generated method stub

            }
        });
        application.setEnabledPreferencesMenu(true);
    }
}
