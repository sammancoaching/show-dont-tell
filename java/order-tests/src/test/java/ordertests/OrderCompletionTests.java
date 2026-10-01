package ordertests;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import orderdomain.Order;

class OrderCompletionTests {

    @Test
    void completedOrder_totalIsCorrect() {
        Order order = new Order();
        order.addLine(new BigDecimal("100"), 1);
        order.complete();

        assertThat(order.isFullyPaid(new BigDecimal("100"))).isTrue();
    }
}
