package ordertests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import orderdomain.Order;

class OrderCompletionTests {

    @Test
    void completedOrder_totalIsCorrect() {
        Order order = new Order();
        order.addLine(new BigDecimal("100"), 1);
        order.complete();

        assertThat(order.isCompleted()).isTrue();
        assertThat(order.getTotal()).isEqualByComparingTo("100");
        assertThat(order.getPayments().stream().reduce(BigDecimal.ZERO, BigDecimal::add))
            .isEqualByComparingTo("0");
    }

    @Test
    void paymentOnOpenOrder_throws_orderStillUnpaid() {
        Order order = new Order();
        order.addLine(new BigDecimal("100"), 1);

        assertThatThrownBy(() -> order.pay(new BigDecimal("100")))
            .isInstanceOf(IllegalStateException.class);

        assertThat(order.isCompleted()).isFalse();
        assertThat(order.getPayments().stream().reduce(BigDecimal.ZERO, BigDecimal::add))
            .isEqualByComparingTo("0");
        assertThat(order.getTotal().subtract(
            order.getPayments().stream().reduce(BigDecimal.ZERO, BigDecimal::add)))
            .isEqualByComparingTo("100");
    }

    @Test
    void completedOrder_partialPayment_isNotFullyPaid() {
        Order order = new Order();
        order.addLine(new BigDecimal("100"), 1);
        order.complete();

        order.pay(new BigDecimal("40"));

        assertThat(order.isCompleted()).isTrue();
        assertThat(order.getPayments().stream().reduce(BigDecimal.ZERO, BigDecimal::add))
            .isEqualByComparingTo("40");
        assertThat(order.getTotal().subtract(
            order.getPayments().stream().reduce(BigDecimal.ZERO, BigDecimal::add)))
            .isEqualByComparingTo("60");
    }

    @Test
    void completedOrder_paymentsSummingToTotal_isFullyPaid() {
        Order order = new Order();
        order.addLine(new BigDecimal("100"), 1);
        order.complete();

        order.pay(new BigDecimal("40"));
        order.pay(new BigDecimal("60"));

        assertThat(order.isCompleted()).isTrue();
        assertThat(order.getPayments())
            .usingElementComparator(BigDecimal::compareTo)
            .containsExactly(new BigDecimal("40"), new BigDecimal("60"));
        assertThat(order.getTotal().subtract(
            order.getPayments().stream().reduce(BigDecimal.ZERO, BigDecimal::add)))
            .isEqualByComparingTo("0");
    }

    @Test
    void overpayment_throws_amountPaidUnchanged() {
        Order order = new Order();
        order.addLine(new BigDecimal("100"), 1);
        order.complete();
        order.pay(new BigDecimal("100"));

        assertThatThrownBy(() -> order.pay(new BigDecimal("10")))
            .isInstanceOf(IllegalStateException.class);

        assertThat(order.isCompleted()).isTrue();
        assertThat(order.getPayments().stream().reduce(BigDecimal.ZERO, BigDecimal::add))
            .isEqualByComparingTo("100");
        assertThat(order.getTotal().subtract(
            order.getPayments().stream().reduce(BigDecimal.ZERO, BigDecimal::add)))
            .isEqualByComparingTo("0");
    }
}
