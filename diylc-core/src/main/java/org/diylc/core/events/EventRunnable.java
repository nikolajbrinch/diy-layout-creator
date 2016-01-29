package org.diylc.core.events;

import java.util.Set;

/**
 * {@link Runnable} implementation that loops over <code>listenerMap</code>
 * and dispatches the event to listeners that are listening for that
 * particular event type.
 * 
 */
class EventRunnable<E extends Enum<E>> implements Runnable {

    private E eventType;

    private Object[] params;

    private Set<EventListener<E>> listeners;

    public EventRunnable(Set<EventListener<E>> listeners, E eventType, Object[] params) {
        this.listeners = listeners;
        this.eventType = eventType;
        this.params = params;
    }

    @Override
    public void run() {
        for (EventListener<E> listener : listeners) {
            try {
                listener.processEvent(eventType, params);
            } catch (Exception e) {
                EventSystem.LOG.error("Listener threw an exception", e);
            }
        }
    }
}