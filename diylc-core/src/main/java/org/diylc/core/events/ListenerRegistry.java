package org.diylc.core.events;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class ListenerRegistry<E extends Enum<E>> {

    Object mutex = new Object();

    ListenerRegistry() {
    }

    private Map<EventListener<E>, EnumSet<E>> listenerMap = new HashMap<EventListener<E>, EnumSet<E>>();

    public void registerListener(EnumSet<E> eventTypes, EventListener<E> listener) {
        if (eventTypes != null) {
            synchronized (mutex) {
                listenerMap.put(listener, eventTypes);
            }
        }
    }

    public void unregisterListener(EventListener<E> listener) {
        synchronized (mutex) {
            listenerMap.remove(listener);
        }
    }

    public Set<EventListener<E>> getListeners(E eventType) {
        Set<EventListener<E>> listeners = new HashSet<>();

        synchronized (mutex) {
            for (Map.Entry<EventListener<E>, EnumSet<E>> entry : listenerMap.entrySet()) {
                if (entry.getValue().contains(eventType)) {
                    listeners.add(entry.getKey());
                }
            }
        }

        return listeners;
    }

    public void removeAllListeners() {
        synchronized (mutex) {
            listenerMap.clear();
        }
    }
}