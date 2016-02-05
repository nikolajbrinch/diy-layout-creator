package org.diylc.core.events;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class EventReciever<E extends Enum<E>> {

    private EventSystem<E> eventSystem = EventSystem.getInstance();

    private long id;

    private Map<EventListener<E>, ProxyListener> listeners = new HashMap<>();

    public EventReciever() {
        id = Thread.currentThread().getId();
    }

    class ProxyListener implements EventListener<E> {

        private EventListener<E> targetListener;

        public ProxyListener(EventListener<E> targetListener) {
            this.targetListener = targetListener;
        }

        @Override
        public void processEvent(E eventType, Object... params) {
            long senderId = (long) params[0];

            if (senderId == id) {
                Object[] targetParams = new Object[params.length - 1];
                System.arraycopy(params, 1, targetParams, 0, targetParams.length);
                targetListener.processEvent(eventType, targetParams);
            }
        }
    }

    public void registerListener(EnumSet<E> eventTypes, EventListener<E> listener) {
        EventReciever<E>.ProxyListener proxyListener = new ProxyListener(listener);
        listeners.put(listener, proxyListener);
        eventSystem.registerListener(eventTypes, proxyListener);
    }

    public void unregisterListener(EventListener<E> listener) {
        ProxyListener proxyListener = listeners.remove(listener);
        eventSystem.unregisterListener(proxyListener);
    }

}
