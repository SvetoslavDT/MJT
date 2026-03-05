package bg.sofia.uni.fmi.mjt.order;

import bg.sofia.uni.fmi.mjt.order.domain.Category;
import bg.sofia.uni.fmi.mjt.order.domain.Order;
import bg.sofia.uni.fmi.mjt.order.domain.PaymentMethod;
import bg.sofia.uni.fmi.mjt.order.domain.Status;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderTest {

    private static String LINE = "ORD0001,14-03-25,Running Shoes,Footwear,60,3,180,Emma Clark,New York," +
            "Debit Card,COMPLETED";

    @Test
    void testOfReturnsCorrectOrder() {
        Order order = Order.of(LINE);

        assertAll("order fields",
                () -> assertEquals("ORD0001", order.id()),
                () -> assertEquals(LocalDate.of(2025, 3, 14), order.date()),
                () -> assertEquals("Running Shoes", order.product()),
                () -> assertEquals(Category.FOOTWEAR, order.category()),
                () -> assertEquals(60.0, order.price(), 1e-6),
                () -> assertEquals(3, order.quantity()),
                () -> assertEquals(180.0, order.totalSales(), 1e-6),
                () -> assertEquals("Emma Clark", order.customerName()),
                () -> assertEquals("New York", order.customerLocation()),
                () -> assertEquals(PaymentMethod.DEBIT_CARD, order.paymentMethod()),
                () -> assertEquals(Status.COMPLETED, order.status()));
    }
}