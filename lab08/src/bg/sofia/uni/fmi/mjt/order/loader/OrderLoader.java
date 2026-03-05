package bg.sofia.uni.fmi.mjt.order.loader;

import bg.sofia.uni.fmi.mjt.order.domain.Order;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class OrderLoader {

    /**
     * Returns a list of orders read from the source Reader.
     *
     * @param reader the Reader with orders
     * @throws IllegalArgumentException if the reader is null
     */
    public static List<Order> load(Reader reader) {
        if (reader == null) {
            throw new IllegalArgumentException("Reader may not be null");
        }

        List<Order> orders = new ArrayList<>();
        BufferedReader br = new BufferedReader(reader);

        try {
            // Skipping first line
            String header = br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                orders.add(Order.of(line));
            }

            return orders;
        } catch (IOException ex) {
            throw new UncheckedIOException("Stream threw an exception", ex);
        }
    }
}