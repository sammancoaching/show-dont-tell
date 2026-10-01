package ordertests;

import static ordertests.OrderStateAssertion.assertThat;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import orderdomain.Order;

class OrderTotalTests {

    // This test was refactored — notice the custom matcher.
    // The other tests still need the same treatment.
    @Test
    void discountOnFirstLine_totalAndLineValues() {
        Order order = new Order();
        order.addLine(new BigDecimal("100"), 2);  // line 0: 200 → 160 with discount
        order.addLine(new BigDecimal("50"), 1);   // line 1: 50

        order.applyDiscount(0);

        assertThat(order).hasState(
            new BigDecimal("210"),
            List.of(new BigDecimal("160"), new BigDecimal("50")));
    }

    @Test
    void discountOnSecondLine_totalAndLineValues() {
        Order order = new Order();
        order.addLine(new BigDecimal("100"), 2);  // line 0: 200
        order.addLine(new BigDecimal("50"), 1);   // line 1: 50 → 40 with discount

        order.applyDiscount(1);

        assertThat(order.getTotal()).isEqualByComparingTo("240");
        assertThat(order.getLines().get(0).getValue()).isEqualByComparingTo("200");
        assertThat(order.getLines().get(1).getValue()).isEqualByComparingTo("40");
    }

    @Test
    void discountOnBothLines_totalAndLineValues() {
        Order order = new Order();
        order.addLine(new BigDecimal("100"), 2);  // line 0: 200 → 160
        order.addLine(new BigDecimal("50"), 1);   // line 1: 50 → 40

        order.applyDiscount(0);
        order.applyDiscount(1);

        assertThat(order.getTotal()).isEqualByComparingTo("200");
        assertThat(order.getLines().get(0).getValue()).isEqualByComparingTo("160");
        assertThat(order.getLines().get(1).getValue()).isEqualByComparingTo("40");
    }

    @Test
    void noDiscount_totalAndLineValues() {
        Order order = new Order();
        order.addLine(new BigDecimal("100"), 2);  // line 0: 200
        order.addLine(new BigDecimal("50"), 1);   // line 1: 50

        assertThat(order.getTotal()).isEqualByComparingTo("250");
        assertThat(order.getLines().get(0).getValue()).isEqualByComparingTo("200");
        assertThat(order.getLines().get(1).getValue()).isEqualByComparingTo("50");
    }

    @Test
    void singleLineWithDiscount_totalAndValue() {
        Order order = new Order();
        order.addLine(new BigDecimal("75"), 4);  // line 0: 300 → 240

        order.applyDiscount(0);

        assertThat(order.getTotal()).isEqualByComparingTo("240");
        assertThat(order.getLines().get(0).getValue()).isEqualByComparingTo("240");
    }

    @Test
    void threeLinesOneDiscount_totalAndLineValues() {
        Order order = new Order();
        order.addLine(new BigDecimal("20"), 1);   // line 0: 20
        order.addLine(new BigDecimal("30"), 2);   // line 1: 60 → 48
        order.addLine(new BigDecimal("10"), 3);   // line 2: 30

        order.applyDiscount(1);

        assertThat(order.getTotal()).isEqualByComparingTo("98");
        assertThat(order.getLines().get(0).getValue()).isEqualByComparingTo("20");
        assertThat(order.getLines().get(1).getValue()).isEqualByComparingTo("48");
        assertThat(order.getLines().get(2).getValue()).isEqualByComparingTo("30");
    }
}
