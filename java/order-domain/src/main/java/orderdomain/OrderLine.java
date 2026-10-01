package orderdomain;

import java.math.BigDecimal;

public class OrderLine {

    private final BigDecimal unitPrice;
    private final int quantity;
    private boolean hasDiscount;

    public OrderLine(BigDecimal unitPrice, int quantity) {
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean hasDiscount() {
        return hasDiscount;
    }

    public BigDecimal getValue() {
        BigDecimal value = unitPrice.multiply(BigDecimal.valueOf(quantity));
        return hasDiscount ? value.multiply(new BigDecimal("0.8")) : value;
    }

    public void applyDiscount() {
        hasDiscount = true;
    }
}
