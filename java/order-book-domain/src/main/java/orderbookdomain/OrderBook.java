package orderbookdomain;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderBook {

    private final Duration paymentWindow;
    private final Map<Integer, Order> ordersById = new HashMap<>();
    private int nextId = 1;

    public OrderBook() {
        this(Duration.ofDays(14));
    }

    public OrderBook(Duration paymentWindow) {
        this.paymentWindow = paymentWindow;
    }

    public List<Order> getOrders() {
        return List.copyOf(ordersById.values());
    }

    public Order openOrder(String customer) {
        Order order = new Order(nextId++, customer);
        ordersById.put(order.getId(), order);
        return order;
    }

    public Order getOrder(int orderId) {
        Order order = ordersById.get(orderId);
        if (order == null) {
            throw new IllegalStateException("Unknown order id " + orderId + ".");
        }
        return order;
    }

    public void finalizeOrder(int orderId, Instant finalizedAt) {
        Order order = getOrder(orderId);
        requireStatus(order, OrderStatus.PENDING, "finalize");
        order.setStatus(OrderStatus.WAITING_FOR_PAYMENT);
        order.setFinalizedAt(finalizedAt);
    }

    public void payOrder(int orderId, Instant paidAt) {
        Order order = getOrder(orderId);
        requireStatus(order, OrderStatus.WAITING_FOR_PAYMENT, "pay");
        if (Duration.between(order.getFinalizedAt(), paidAt).compareTo(paymentWindow) > 0) {
            order.setStatus(OrderStatus.CANCELLED);
            throw new IllegalStateException(
                "Payment for order " + orderId + " arrived after the payment window; the order was cancelled.");
        }
        order.setStatus(OrderStatus.FULFILLING);
    }

    public void shipOrder(int orderId) {
        Order order = getOrder(orderId);
        requireStatus(order, OrderStatus.FULFILLING, "ship");
        order.setStatus(OrderStatus.DELIVERED);
    }

    public void cancelOrder(int orderId) {
        Order order = getOrder(orderId);
        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.WAITING_FOR_PAYMENT) {
            throw new IllegalStateException(
                "Cannot cancel order " + orderId + " in status " + order.getStatus() + ".");
        }
        order.setStatus(OrderStatus.CANCELLED);
    }

    private static void requireStatus(Order order, OrderStatus expected, String action) {
        if (order.getStatus() != expected) {
            throw new IllegalStateException(
                "Cannot " + action + " order " + order.getId() + " in status " + order.getStatus()
                    + " (expected " + expected + ").");
        }
    }
}
