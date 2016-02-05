package org.diylc.core.platform;


public class QuitEvent extends AbstractAppEvent {

    public QuitEvent(Object source) {
        super(AppEventType.QUIT, source);
    }

}
