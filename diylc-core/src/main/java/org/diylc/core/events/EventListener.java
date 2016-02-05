package org.diylc.core.events;

@FunctionalInterface
public interface EventListener<E extends Enum<E>> {

    /**
     * Called from the background thread when event is received. Use
     * {@link SwingUtilities#invokeLater} if event processing needs to take
     * place in the EDT.
     * 
     * @param eventType
     * @param params
     */
    void processEvent(E eventType, Object... params);

}
