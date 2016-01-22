package org.diylc.app.platform;


public class QuitEvent extends AbstractAppEvent {

    public QuitEvent(Object source) {
        super(AppEventType.QUIT, source);
    }

}
