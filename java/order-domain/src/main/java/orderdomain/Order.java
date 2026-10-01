package orderdomain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Order {

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

    public void addLine(BigDecimal unitPrice, int quantity) {
        lines.add(new OrderLine(unitPrice, quantity));
    }

    public void applyDiscount(int lineIndex) {
        lines.get(lineIndex).applyDiscount();
    }

    public void complete() {
        completed = true;
    }

    // C# decimal == is numeric (scale-insensitive); BigDecimal.equals is
    // scale-sensitive, so compare numerically with compareTo.
    public boolean isFullyPaid(BigDecimal expectedTotal) {
        return completed && getTotal().compareTo(expectedTotal) == 0;
    }
}
