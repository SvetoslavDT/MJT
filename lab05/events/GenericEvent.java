package bg.sofia.uni.fmi.mjt.eventbus.events;

import java.time.Instant;

public class GenericEvent<T extends Payload<?>> implements Event<T> {

    private final Instant timestamp;
    private final T payload;
    private final int priority;
    private final String source;

    public GenericEvent(T payload, int priority, String source) {
        if (payload == null) {
            throw new NullPointerException("Payload is null");
        }
        if (source == null || source.length() == 0) {
            throw new IllegalArgumentException("Source is null or empty");
        }

        this.timestamp = Instant.now();
        this.payload = payload;
        this.priority = priority;
        this.source = source;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public T getPayload() {
        return payload;
    }
}
