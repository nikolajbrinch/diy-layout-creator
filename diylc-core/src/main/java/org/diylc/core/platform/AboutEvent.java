package org.diylc.core.platform;


public class AboutEvent extends AbstractAppEvent {

    public AboutEvent(Object source) {
        super(AppEventType.ABOUT, source);
    }

}
