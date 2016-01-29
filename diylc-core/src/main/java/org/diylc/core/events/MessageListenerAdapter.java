package org.diylc.core.events;

public class MessageListenerAdapter<E extends Enum<E>> implements EventListener<E> {

    private IMessageListener<E> listener;

    public MessageListenerAdapter(IMessageListener<E> listener) {
        this.listener = listener;
    }

    @Override
    public void processEvent(E eventType, Object... params) {
        listener.processMessage(eventType, params);
    }

}
