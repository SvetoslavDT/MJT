package bg.sofia.uni.fmi.mjt.eventbus.subscribers;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;

import java.util.Comparator;

public class ComparatorByPriorityTimestamp<E extends Event<?>> implements Comparator<E> {
    @Override
    public int compare(E e1, E e2) {
        int cmp = Integer.compare(e1.getPriority(), e2.getPriority());

        if (cmp != 0) {
            return cmp;
        }

        return e1.getTimestamp().compareTo(e2.getTimestamp());
    }
}