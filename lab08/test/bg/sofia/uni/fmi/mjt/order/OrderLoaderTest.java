package bg.sofia.uni.fmi.mjt.order;

import bg.sofia.uni.fmi.mjt.order.loader.OrderLoader;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderLoaderTest {

    private static final String FILE_PATH = "amazon_sales_2025.csv";
    private static final int FILE_LINES_TO_READ = 250;

    OrderLoader orderLoader;

    @Test
    void testLoadReaderIsNullThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> orderLoader.load(null),
                "Expected to be thrown IllegalArgumentException when argument is null.");
    }

    @Test
    void testLoadCorrectlyReadNLines() throws IOException {
        Reader reader = new FileReader(FILE_PATH);
        var orders = OrderLoader.load(reader);

        assertEquals(FILE_LINES_TO_READ, orders.size(), "Expected to be read " + FILE_LINES_TO_READ + " lines.");
    }

    @Test
    void testLoadDoesntReadNulls() throws IOException {
        Reader reader = new FileReader(FILE_PATH);
        var orders = OrderLoader.load(reader);

        assertFalse(orders.contains(null), "Expected none of the elements to be null.");
    }
}