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

    private EventSystem<E> eventSystem = EventSystem.getInstance();

    private long id;
    
    public EventDispatcher() {
        id = Thread.currentThread().getId();
    }

    /**
     * Notifies all interested listeners.
     * 
     * @param eventType
     * @param params
     */
    public void sendEvent(E eventType, Object... params) {
        Object[] idParams = new Object[params.length + 1];
        idParams[0] = id;
        System.arraycopy(params, 0, idParams, 1, params.length);
        eventSystem.sendEvent(eventType, idParams);
    }

    public void sendEventAsync(E eventType, Object... params) {
        Object[] idParams = new Object[params.length + 1];
        idParams[0] = id;
        System.arraycopy(params, 0, idParams, 1, params.length);
        eventSystem.sendEventAsync(eventType, idParams);
    }

}
