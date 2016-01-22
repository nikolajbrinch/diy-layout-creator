package org.diylc.app.platform;


public class PreferencesEvent extends AbstractAppEvent {

    public PreferencesEvent(Object source) {
        super(AppEventType.PREFERENCES, source);
    }

}
