package org.diylc.app.platform;


public class AboutEvent extends AbstractAppEvent {

    public AboutEvent(Object source) {
        super(AppEventType.ABOUT, source);
    }

}
