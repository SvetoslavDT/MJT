package bg.sofia.uni.fmi.mjt.eventbus;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;
import bg.sofia.uni.fmi.mjt.eventbus.exception.MissingSubscriptionException;
import bg.sofia.uni.fmi.mjt.eventbus.subscribers.Subscriber;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EventBusImpl implements EventBus {

    private final Map<Class<? extends Event<?>>, Set<Subscriber<?>>> subscribers;
    private final Map<Class<? extends Event<?>>, List<Event<?>>> eventLog;

    public EventBusImpl() {
        subscribers = new HashMap<>();
        eventLog = new HashMap<>();
    }

    @Override
    public <T extends Event<?>> void subscribe(Class<T> eventType, Subscriber<? super T> subscriber) {
        if (eventType == null) {
            throw new IllegalArgumentException("EventType cannot be null");
        }
        if (subscriber == null) {
            throw new IllegalArgumentException("Subscriber cannot be null");
        }

        subscribers.putIfAbsent(eventType, new HashSet<>());
        subscribers.get(eventType).add(subscriber);
    }

    @Override
    public <T extends Event<?>> void unsubscribe(Class<T> eventType, Subscriber<? super T> subscriber)
            throws MissingSubscriptionException {

        if (eventType == null) {
            throw new IllegalArgumentException("EventType cannot be null");
        }
        if (subscriber == null) {
            throw new IllegalArgumentException("Subscriber cannot be null");
        }

        Set<Subscriber<?>> set = subscribers.get(eventType);
        if (set == null || !set.contains(subscriber)) {
            throw new MissingSubscriptionException("Subscriber is not subscribed to this event");
        }

        set.remove(subscriber);
    }

    @Override
    public <T extends Event<?>> void publish(T event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        // This cast is needed and is safe from exceptions
        Class<? extends Event<?>> key = (Class<? extends Event<?>>) event.getClass();
        eventLog.putIfAbsent(key, new ArrayList<>());
        eventLog.get(key).add(event);

        if (subscribers.containsKey(key)) {
            for (Subscriber<?> subscriber : subscribers.get(key)) {
                // Needed casting which is safe
                Subscriber<? super T> sub = (Subscriber<? super T>) subscriber;
                sub.onEvent(event);
            }
        }
    }

    @Override
    public void clear() {
        subscribers.clear();
        eventLog.clear();
    }

    @Override
    public Collection<? extends Event<?>> getEventLogs(Class<? extends Event<?>> eventType,
                                                       Instant from, Instant to) {
        if (eventType == null) {
            throw new IllegalArgumentException("EventType cannot be null");
        }
        if (from == null) {
            throw new IllegalArgumentException("From cannot be null");
        }
        if (to == null) {
            throw new IllegalArgumentException("To cannot be null");
        }
        if (!from.isBefore(to)) {
            return List.of();
        }

        List<Event<?>> events = eventLog.get(eventType);
        if (events == null || events.isEmpty()) {
            return List.of();
        }

        List<Event<?>> result = new ArrayList<>();
        for (Event<?> e : events) {
            Instant ts = e.getTimestamp();
            if (!ts.isBefore(from) && ts.isBefore(to)) {
                result.add(e);
            }
        }

        return Collections.unmodifiableList(result);
    }

    @Override
    public <T extends Event<?>> Collection<Subscriber<?>> getSubscribersForEvent(Class<T> eventType) {
        if (eventType == null) {
            throw new IllegalArgumentException("EventType cannot be null");
        }
        Set<Subscriber<?>> set = subscribers.get(eventType);
        if (set == null || set.isEmpty()) {
            return List.of();
        }

        List<Subscriber<?>> copy = new ArrayList<>(set);
        return Collections.unmodifiableList(copy);
    }
}