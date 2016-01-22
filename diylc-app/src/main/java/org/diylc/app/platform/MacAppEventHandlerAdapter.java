package org.diylc.app.platform;


public class MacAppEventHandlerAdapter implements AppEventHandler {

    private AppEventHandler appEventHandler;

    public MacAppEventHandlerAdapter(AppEventHandler appEventHandler) {
        this.appEventHandler = appEventHandler;
    }

    public void handleEvent(AbstractAppEvent event) {
        switch (event.getType()) {
        case ABOUT:
            handleAbout((AboutEvent) event);
            break;
        case PREFERENCES:
            handlePreferences((PreferencesEvent) event);
            break;
        case QUIT:
            handleQuit((QuitEvent) event);
            break;
        default:
            break;
        }
    }

    private void handlePreferences(PreferencesEvent e) {
        ((PreferencesHandler) appEventHandler).handlePreferences(e);
    }

    private void handleQuit(QuitEvent e) {
        ((QuitHandler) appEventHandler).handleQuit(e);
    }

    private void handleAbout(AboutEvent e) {
        ((AboutHandler) appEventHandler).handleAbout(e);
    }
}
