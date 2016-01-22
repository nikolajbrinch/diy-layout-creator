package org.diylc.app.platform;


public abstract class AbstractAppEvent {

    private final AppEventType type;

    private final transient Object source;

    AbstractAppEvent(AppEventType type, Object source) {
        this.type = type;
        this.source = source;
    }

    public Object getSource() {
        return source;
    }

    public AppEventType getType() {
        return type;
    }

}
