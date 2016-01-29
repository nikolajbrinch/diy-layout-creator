package org.diylc.app.platform;


public class MacAppEventHandlerAdapter implements AppEventHandler {

    private AppEventHandler appEventHandler;

    public MacAppEventHandlerAdapter(AppEventHandler appEventHandler) {
        this.appEventHandler = appEventHandler;
    }

    public void handleEvent(AbstractAppEvent event, AbstractAppResponse response) {
        switch (event.getType()) {
        case ABOUT:
            handleAbout((AboutEvent) event);
            break;
        case PREFERENCES:
            handlePreferences((PreferencesEvent) event);
            break;
        case QUIT:
            handleQuit((QuitEvent) event, (MacQuitResponse) response);
            break;
        default:
            break;
        }
    }

    private void handlePreferences(PreferencesEvent event) {
        ((PreferencesHandler) appEventHandler).handlePreferences(event);
    }

    private void handleQuit(QuitEvent event, MacQuitResponse response) {
        ((QuitHandler) appEventHandler).handleQuit(event, response);
    }

    private void handleAbout(AboutEvent event) {
        ((AboutHandler) appEventHandler).handleAbout(event);
    }
}
