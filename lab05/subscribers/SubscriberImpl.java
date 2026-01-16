package bg.sofia.uni.fmi.mjt.eventbus.subscribers;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;

public class SubscriberImpl implements Subscriber<Event<?>> {

    @Override
    public void onEvent(Event<?> event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        // Do something
    }
}