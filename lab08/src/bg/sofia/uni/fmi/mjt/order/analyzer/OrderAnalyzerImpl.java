package bg.sofia.uni.fmi.mjt.order.analyzer;

import bg.sofia.uni.fmi.mjt.order.domain.Category;
import bg.sofia.uni.fmi.mjt.order.domain.Order;
import bg.sofia.uni.fmi.mjt.order.domain.PaymentMethod;
import bg.sofia.uni.fmi.mjt.order.domain.Status;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderAnalyzerImpl implements OrderAnalyzer {

    private static final double EPS = 1e-6;
    private static final double MAX_SUSPICIOUS_SUM = 100.0;
    private static final int MIN_SUSPICIOUS_PURCHASES = 3;

    private final List<Order> orders;

    public OrderAnalyzerImpl(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public List<Order> allOrders() {
        return Collections.unmodifiableList(orders);
    }

    @Override
    public List<Order> ordersByCustomer(String customer) {
        if (customer == null || customer.isBlank()) {
            throw new IllegalArgumentException("Customer cannot be null or blank");
        }

        return orders.stream()
                .filter(order -> order.customerName().equals(customer))
                .toList();
    }

    @Override
    public Map.Entry<LocalDate, Long> dateWithMostOrders() {
        return orders.stream()
                .collect(Collectors.groupingBy(Order::date, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.<LocalDate, Long>comparingByValue()
                        .thenComparing(Map.Entry::getKey, Comparator.reverseOrder()))
                .orElse(null);
    }

    @Override
    public List<String> topNMostOrderedProducts(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Number of products cannot be negative");
        }
        if (n == 0) {
            return List.of();
        }

        return orders.stream()
                .collect(Collectors.groupingBy(Order::product, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry::getKey))
                .limit(n)
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public Map<Category, Double> revenueByCategory() {
        return orders.stream()
                .collect(Collectors.groupingBy(Order::category, Collectors.summingDouble(Order::totalSales)));
    }

    @Override
    public Set<String> suspiciousCustomers() {
        return orders.stream()
                .filter(entry -> entry.status().equals(Status.CANCELLED) && lessThan(entry.totalSales(),
                        MAX_SUSPICIOUS_SUM))
                .collect(Collectors.groupingBy(Order::customerName, Collectors.counting()))
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > MIN_SUSPICIOUS_PURCHASES)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<Category, PaymentMethod> mostUsedPaymentMethodForCategory() {

        Map<Category, Map<PaymentMethod, Long>> countsByCategory = orders.stream()
                .collect(Collectors.groupingBy(Order::category, Collectors.groupingBy(
                        Order::paymentMethod, Collectors.counting()
                )));

        Map<Category, PaymentMethod> result = new HashMap<>();
        for (var categoryEntry : countsByCategory.entrySet()) {

            Category category = categoryEntry.getKey();
            Map<PaymentMethod, Long> paymentCounts = categoryEntry.getValue();

            Map.Entry<PaymentMethod, Long> top = paymentCounts.entrySet().stream()
                    .max(Comparator.<Map.Entry<PaymentMethod, Long>>comparingLong(Map.Entry::getValue)
                            .thenComparing(entry -> entry.getKey().name(), Comparator.reverseOrder()))
                    .orElse(null);

            if (top != null) {
                result.put(category, top.getKey());
            }
        }

        return Collections.unmodifiableMap(result);
    }

    @Override
    public String locationWithMostOrders() {
        return orders.stream()
                .collect(Collectors.groupingBy(Order::customerLocation, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Comparator.<Map.Entry<String, Long>>comparingLong(Map.Entry::getValue).reversed()
                        .thenComparing(Map.Entry::getKey))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Map<Category, Map<Status, Long>> groupByCategoryAndStatus() {

        if (orders.isEmpty()) {
            return Collections.emptyMap();
        }

        return orders.stream()
                .collect(Collectors.groupingBy(Order::category,
                        Collectors.groupingBy(Order::status, Collectors.counting())));

    }

    private static boolean lessThan(double a, double b) {
        return a < b - EPS;
    }
}