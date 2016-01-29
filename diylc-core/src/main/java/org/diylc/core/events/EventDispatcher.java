package org.diylc.core.events;

/**
 * Utility for synchronous or asynchronous message distribution.
 * 
 * @author Branislav Stojkovic
 * 
 * @param <E>
 *            enum that contains all available event types
 * 
 * @see IMessageListener
 */
public class EventDispatcher<E extends Enum<E>> {

    EventSystem<E> eventSystem = EventSystem.getInstance();

    public EventDispatcher() {
    }

    /**
     * Notifies all interested listeners.
     * 
     * @param eventType
     * @param params
     */
    public void sendEvent(E eventType, Object... params) {
        eventSystem.sendEvent(eventType, params);
    }

    public void sendEventAsync(E eventType, Object... params) {
        eventSystem.sendEventAsync(eventType, params);
    }

}
