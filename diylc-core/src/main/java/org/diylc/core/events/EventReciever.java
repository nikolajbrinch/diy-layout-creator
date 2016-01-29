package org.diylc.core.events;

import java.util.EnumSet;

public class EventReciever<E extends Enum<E>> {

    EventSystem<E> eventSystem = EventSystem.getInstance();

    public EventReciever() {
    }

    public void registerListener(EnumSet<E> eventTypes, EventListener<E> listener) {
        eventSystem.registerListener(eventTypes, listener);
    }

    public void unregisterListener(EventListener<E> listener) {
        eventSystem.unregisterListener(listener);
    }

}
