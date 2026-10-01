package orderbookdomain;

import java.time.Instant;

public class Order {

    private final int id;
    private final String customer;

    private OrderStatus status;
    private Instant finalizedAt;

    Order(int id, String customer) {
        this.id = id;
        this.customer = customer;
        this.status = OrderStatus.PENDING;
    }

    public int getId() {
        return id;
    }

    public String getCustomer() {
        return customer;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getFinalizedAt() {
        return finalizedAt;
    }

    void setStatus(OrderStatus status) {
        this.status = status;
    }

    void setFinalizedAt(Instant finalizedAt) {
        this.finalizedAt = finalizedAt;
    }
}
