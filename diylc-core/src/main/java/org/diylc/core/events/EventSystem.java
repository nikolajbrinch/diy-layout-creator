package org.diylc.core.events;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EventSystem<E extends Enum<E>> {

    @SuppressWarnings("rawtypes")
    private static final EventSystem<?> INSTANCE = new EventSystem();

    static final Logger LOG = LoggerFactory.getLogger(EventDispatcher.class);

    private ListenerRegistry<E> listenerRegistry = new ListenerRegistry<E>();

    private ExecutorService threadFactory = Executors.newCachedThreadPool();

    private EventSystem() {
    }

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> EventSystem<E> getInstance() {
        return (EventSystem<E>) INSTANCE;
    }
    
    public void registerListener(EnumSet<E> eventTypes, EventListener<E> listener) {
        listenerRegistry.registerListener(eventTypes, listener);
    }

    public void unregisterListener(EventListener<E> listener) {
        listenerRegistry.unregisterListener(listener);
    }

    public void sendEvent(E eventType, Object... params) {
        Set<EventListener<E>> listeners = listenerRegistry.getListeners(eventType);

        for (EventListener<E> listener : listeners) {
            try {
                listener.processEvent(eventType, params);
            } catch (Exception e) {
                LOG.error("Listener threw an exception", e);
            }
        }
    }

    public void sendEventAsync(E eventType, Object... params) {
        threadFactory.execute(new EventRunnable<E>(listenerRegistry.getListeners(eventType), eventType, params));
    }
    
    public void removeAllListeners() {
        listenerRegistry.removeAllListeners();
    }
}
