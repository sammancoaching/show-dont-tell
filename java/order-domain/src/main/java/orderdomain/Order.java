package orderdomain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {

    private final List<BigDecimal> payments = new ArrayList<>();

    private final List<OrderLine> lines = new ArrayList<>();

    private boolean completed;

    public List<OrderLine> getLines() {
        return lines;
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderLine line : lines) {
            total = total.add(line.getValue());
        }
        return total;
    }

    public boolean isCompleted() {
        return completed;
    }

    public List<BigDecimal> getPayments() {
        return Collections.unmodifiableList(payments);
    }

    public BigDecimal getAmountPaid() {
        BigDecimal amountPaid = BigDecimal.ZERO;
        for (BigDecimal payment : payments) {
            amountPaid = amountPaid.add(payment);
        }
        return amountPaid;
    }

    public BigDecimal getOutstanding() {
        return getTotal().subtract(getAmountPaid());
    }

    public boolean isFullyPaid() {
        return completed && getOutstanding().compareTo(BigDecimal.ZERO) == 0;
    }

    public void addLine(BigDecimal unitPrice, int quantity) {
        lines.add(new OrderLine(unitPrice, quantity));
    }

    public void applyDiscount(int lineIndex) {
        lines.get(lineIndex).applyDiscount();
    }

    public void complete() {
        completed = true;
    }

    public void pay(BigDecimal amount) {
        if (!completed) {
            throw new IllegalStateException("Cannot pay an open order.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment must be positive.");
        }
        if (amount.compareTo(getOutstanding()) > 0) {
            throw new IllegalStateException("Payment exceeds outstanding amount.");
        }
        payments.add(amount);
    }
}
