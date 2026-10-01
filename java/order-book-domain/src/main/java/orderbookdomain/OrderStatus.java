package orderbookdomain;

public enum OrderStatus {
    PENDING,
    WAITING_FOR_PAYMENT,
    FULFILLING,
    DELIVERED,
    CANCELLED
}
