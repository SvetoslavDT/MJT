package bg.sofia.uni.fmi.mjt.order;

import bg.sofia.uni.fmi.mjt.order.analyzer.OrderAnalyzerImpl;
import bg.sofia.uni.fmi.mjt.order.domain.Category;
import bg.sofia.uni.fmi.mjt.order.domain.Order;
import bg.sofia.uni.fmi.mjt.order.domain.PaymentMethod;
import bg.sofia.uni.fmi.mjt.order.domain.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderAnalyzerImplTest {

    private static final double EPS = 1e-6;
    private List<Order> orders;
    private OrderAnalyzerImpl analyzer;

    @BeforeEach
    void setUp() {
        orders = new ArrayList<>();

        orders.add(new Order("ORD1", LocalDate.of(2025, 3, 1), "ProdA", Category.ELECTRONICS,
                10.0, 1, 10.0, "Alice", "Zagreb", PaymentMethod.PAYPAL, Status.COMPLETED));
        orders.add(new Order("ORD2", LocalDate.of(2025, 3, 1), "ProdA", Category.ELECTRONICS,
                10.0, 2, 20.0, "Bob", "Sofia", PaymentMethod.CREDIT_CARD, Status.COMPLETED));
        orders.add(new Order("ORD3", LocalDate.of(2025, 2, 28), "ProdB", Category.BOOKS,
                5.0, 1, 5.0, "Alice", "Sofia", PaymentMethod.CREDIT_CARD, Status.COMPLETED));
        orders.add(new Order("ORD4", LocalDate.of(2025, 2, 28), "ProdB", Category.BOOKS,
                5.0, 1, 5.0, "Charlie", "Plovdiv", PaymentMethod.PAYPAL, Status.COMPLETED));
        // making ProdC once
        orders.add(new Order("ORD5", LocalDate.of(2025, 3, 2), "ProdC", Category.CLOTHING,
                30.0, 1, 30.0, "Dan", "Sofia", PaymentMethod.AMAZON_PAY, Status.COMPLETED));

        // For suspiciousCustomers: create 4 cancelled orders for "Suspect" under 100.0
        orders.add(new Order("ORD6", LocalDate.of(2025, 3, 3), "X", Category.CLOTHING,
                10.0, 1, 10.0, "Suspect", "CityA", PaymentMethod.GIFT_CARD, Status.CANCELLED));
        orders.add(new Order("ORD7", LocalDate.of(2025, 3, 4), "Y", Category.CLOTHING,
                20.0, 1, 20.0, "Suspect", "CityA", PaymentMethod.GIFT_CARD, Status.CANCELLED));
        orders.add(new Order("ORD8", LocalDate.of(2025, 3, 5), "Z", Category.CLOTHING,
                30.0, 1, 30.0, "Suspect", "CityA", PaymentMethod.GIFT_CARD, Status.CANCELLED));
        orders.add(new Order("ORD9", LocalDate.of(2025, 3, 6), "W", Category.CLOTHING,
                40.0, 1, 40.0, "Suspect", "CityA", PaymentMethod.GIFT_CARD, Status.CANCELLED));

        // For mostUsedPaymentMethodForCategory: create tie in ELECTRONICS between CREDIT_CARD and PAYPAL (both 2),
        // alphabetical should prefer CREDIT_CARD
        orders.add(new Order("ORD10", LocalDate.of(2025, 3, 7), "G", Category.ELECTRONICS,
                50.0, 1, 50.0, "E", "Sofia", PaymentMethod.CREDIT_CARD, Status.COMPLETED));
        orders.add(new Order("ORD11", LocalDate.of(2025, 3, 8), "H", Category.ELECTRONICS,
                60.0, 1, 60.0, "F", "Sofia", PaymentMethod.PAYPAL, Status.COMPLETED));
        orders.add(new Order("ORD12", LocalDate.of(2025, 3, 9), "I", Category.ELECTRONICS,
                70.0, 1, 70.0, "G", "Sofia", PaymentMethod.CREDIT_CARD, Status.COMPLETED));
        orders.add(new Order("ORD13", LocalDate.of(2025, 3, 10), "J", Category.ELECTRONICS,
                80.0, 1, 80.0, "H", "Sofia", PaymentMethod.PAYPAL, Status.COMPLETED));

        analyzer = new OrderAnalyzerImpl(orders);
    }

    @Test
    void testAllOrdersWorksCorrectly() {
        // Original method uses IDE logic so it can not be incorrect
        assertNotNull(analyzer.allOrders(), "Expecte initialised unmodifialbe view to be returned");
    }

    @Test
    void testOrdersByCustomerNullParameterThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.ordersByCustomer(null),
                "Expected to be thrown IllegalArgumentException");
    }

    @Test
    void testOrdersByCustomerBlankParameterThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,() -> analyzer.ordersByCustomer(""),
                "Expected to be thrown IllegalArgumentException");
    }

    @Test
    void testOrdersByCustomerReturnsOnlyCustomerOrdersCaseSensitive() {
        List<Order> aliceOrders = analyzer.ordersByCustomer("Alice");

        assertEquals(2, aliceOrders.size(), "Expected two alice orders");
        assertTrue(aliceOrders.stream().allMatch(o -> "Alice".equals(o.customerName())),
                "Expected alice orders only");
    }

    @Test
    void testDateWithMostOrdersEarliestOnTie() {
        Map.Entry<LocalDate, Long> entry = analyzer.dateWithMostOrders();

        assertNotNull(entry, "Expected entry to be not null");
        // dates: 2025-03-01 -> 2, 2025-02-28 -> 2, earliest is 2025-02-28
        assertEquals(LocalDate.of(2025, 2, 28), entry.getKey(), "Expected different date");
        assertEquals(2L, entry.getValue().longValue(), "Expected different order date");
    }

    @Test
    void testTopNMostOrderedProductsNIsNegativeThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.topNMostOrderedProducts(-1),
                "Expected to be thrown IllegalArgumentException");
    }

    @Test
    void testTopNMostOrderedProductsNIsZeroReturnsEmptyList() {
        assertEquals(0, analyzer.topNMostOrderedProducts(0).size(), "Expected zero elements");
    }

    @Test
    void testTopNMostOrderedProductsBasicAndTieOrdering() {
        // ProdA = 2, ProdB = 2, ProdC = 1
        List<String> top2 = analyzer.topNMostOrderedProducts(2);
        // ProdA and ProdB both have 2; alphabetical -> ProdA, ProdB

        assertEquals(2, top2.size(), "Expected two top 2 products");
        assertEquals(List.of("ProdA", "ProdB"), top2, "Expected asserted products");
    }

    @Test
    void testRevenueByCategorySumsCorrectly() {
        Map<Category, Double> revenues = analyzer.revenueByCategory();
        // BOOKS: ORD3 + ORD4 = 5 + 5 = 10
        assertEquals(10.0, revenues.get(Category.BOOKS), EPS, "Expected 10.0 revenue");
        // ELECTRONICS: ORD1 + ORD2 + ORD10 + ORD12 + ORD11 + ORD13 = 10 + 20 + 50 + 70 + 60 + 80 = 290
        assertEquals(290.0, revenues.get(Category.ELECTRONICS), EPS, "Expected 290.0 revenue");
        // CLOTHING: ORD5 + ORD6..9 totals: ORD5=30, ORD6..9 are 10+20+30+40 = 100 => CLOTHING total = 130
        assertEquals(130.0, revenues.get(Category.CLOTHING), EPS, "Expected 130.0 revenue");
    }

    @Test
    void testSuspiciousCustomersDetectsCorrectly() {
        Set<String> suspects = analyzer.suspiciousCustomers();

        assertTrue(suspects.contains("Suspect"), "Expected Suspect in result");
        assertFalse(suspects.contains("Alice"), "Alice was not expected in result");
    }

    @Test
    void testMostUsedPaymentMethodForCategory() {
        Map<Category, PaymentMethod> map = analyzer.mostUsedPaymentMethodForCategory();
        // For ELECTRONICS I arranged a tie between CREDIT_CARD and PAYPAL (2 each) -> CREDIT_CARD should win alphabetically

        assertEquals(PaymentMethod.CREDIT_CARD, map.get(Category.ELECTRONICS), "Expected different payment");
    }

    @Test
    void testLocationWithMostOrdersWorksCorrectly() {
        // Given our setup Sofia has many orders; CityA has 4. Sofia should be larger -> expect "Sofia".
        String topLocation = analyzer.locationWithMostOrders();

        assertNotNull(topLocation, "Expected to be not null");
        assertEquals("Sofia", topLocation, "Expected Sofia to be location with most orders");
    }

    @Test
    void testGroupByCategoryAndStatusOrdersIsEmptyReturnsEmptyMap() {
        List<Order> newOrders = new ArrayList<>();
        OrderAnalyzerImpl newAnalyzer = new OrderAnalyzerImpl(newOrders);

        assertEquals(0, newAnalyzer.groupByCategoryAndStatus().size(), "Expected empty map");
    }

    @Test
    void testGroupByCategoryAndStatusReturnsCounts() {
        Map<Category, Map<Status, Long>> grouped = analyzer.groupByCategoryAndStatus();

        // BOOKS: 2 completed
        assertEquals(2L, grouped.get(Category.BOOKS).get(Status.COMPLETED).longValue(),
                "Expected different status counts");

        // CLOTHING: ORD5 completed + 4 cancelled = completed 1, cancelled 4
        assertEquals(1L, grouped.get(Category.CLOTHING).get(Status.COMPLETED).longValue(),
                "Expected different status counts");
        assertEquals(4L, grouped.get(Category.CLOTHING).get(Status.CANCELLED).longValue(),
                "Expected different status counts");
    }
}