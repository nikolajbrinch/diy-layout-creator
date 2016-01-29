package org.diylc.core.events;

import java.util.HashMap;
import java.util.Map;

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
public class MessageDispatcher<E extends Enum<E>> {

    private EventReciever<E> eventReciever = new EventReciever<E>();

    private EventDispatcher<E> eventDispatcher = new EventDispatcher<E>();

    private Map<IMessageListener<E>, MessageListenerAdapter<E>> listenerMap = new HashMap<>();

    private Object mutex = new Object();

    private final boolean synchronous;

    public MessageDispatcher(boolean synchronous) {
        this.synchronous = synchronous;
    }

    public void registerListener(IMessageListener<E> listener) {
        if (listener.getSubscribedEventTypes() != null) {
            MessageListenerAdapter<E> adapter = new MessageListenerAdapter<E>(listener);
            synchronized (mutex) {
                listenerMap.put(listener, adapter);
            }
            eventReciever.registerListener(listener.getSubscribedEventTypes(), adapter);
        }
    }

    public void unregisterListener(IMessageListener<E> listener) {
        synchronized (mutex) {
            MessageListenerAdapter<E> adapter = listenerMap.get(listener);
            eventReciever.unregisterListener(adapter);
            listenerMap.remove(adapter);
        }
    }

    /**
     * Notifies all interested listeners.
     * 
     * @param eventType
     * @param params
     */
    public void dispatchMessage(E eventType, Object... params) {
        if (synchronous) {
            eventDispatcher.sendEvent(eventType, params);
        } else {
            eventDispatcher.sendEventAsync(eventType, params);
        }
    }
}
