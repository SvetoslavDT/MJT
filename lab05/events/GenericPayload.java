package bg.sofia.uni.fmi.mjt.eventbus.events;

public record GenericPayload<T>(T payload) implements Payload<T> {

    public GenericPayload {
        if (payload == null) {
            throw new IllegalArgumentException("Payload is null");
        }
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public T getPayload() {
        return payload;
    }
}
