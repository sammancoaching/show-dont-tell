package orderbooktests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import orderbookdomain.OrderBook;
import orderbookdomain.OrderStatus;

class OrderBookLifecycleTests {

    private static final Instant DAY_0 = Instant.parse("2025-06-01T00:00:00Z");

    private static Instant at(int day) {
        return DAY_0.plus(day, ChronoUnit.DAYS);
    }

    @Test
    void finalizeThenPay_movesOrderThroughTwoStates() {
        OrderBook book = new OrderBook();
        var order = book.openOrder("anna");

        book.finalizeOrder(order.getId(), at(1));
        assertThat(book.getOrder(order.getId()).getStatus()).isEqualTo(OrderStatus.WAITING_FOR_PAYMENT);

        book.payOrder(order.getId(), at(2));
        assertThat(book.getOrder(order.getId()).getStatus()).isEqualTo(OrderStatus.FULFILLING);
    }

    @Test
    void payThenShip_deliversTheOrder() {
        OrderBook book = new OrderBook();
        var order = book.openOrder("anna");

        book.finalizeOrder(order.getId(), at(1));
        assertThat(book.getOrder(order.getId()).getStatus()).isEqualTo(OrderStatus.WAITING_FOR_PAYMENT);

        book.payOrder(order.getId(), at(2));
        book.shipOrder(order.getId());
        assertThat(book.getOrder(order.getId()).getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void payWithinWindow_thenLatePayOnSecondOrder_cancelsOnlyThatOrder() {
        OrderBook book = new OrderBook();
        var first = book.openOrder("anna");
        var second = book.openOrder("bram");

        book.finalizeOrder(first.getId(), at(1));
        book.finalizeOrder(second.getId(), at(1));
        assertThat(book.getOrders()).hasSize(2);

        book.payOrder(first.getId(), at(5));
        assertThat(book.getOrder(first.getId()).getStatus()).isEqualTo(OrderStatus.FULFILLING);

        assertThatThrownBy(() -> book.payOrder(second.getId(), at(30)))
            .isInstanceOf(IllegalStateException.class);
        assertThat(book.getOrder(second.getId()).getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void payBeforeFinalize_throws_thenFinalizeStillWorks() {
        OrderBook book = new OrderBook();
        var order = book.openOrder("anna");

        assertThatThrownBy(() -> book.payOrder(order.getId(), at(1)))
            .isInstanceOf(IllegalStateException.class);
        assertThat(book.getOrder(order.getId()).getStatus()).isEqualTo(OrderStatus.PENDING);

        book.finalizeOrder(order.getId(), at(2));
        assertThat(book.getOrder(order.getId()).getStatus()).isEqualTo(OrderStatus.WAITING_FOR_PAYMENT);
    }

    @Test
    void cancelPendingOrder_thenPayAfterCancel_throwsAgain() {
        OrderBook book = new OrderBook();
        var order = book.openOrder("anna");

        book.cancelOrder(order.getId());
        assertThat(book.getOrder(order.getId()).getStatus()).isEqualTo(OrderStatus.CANCELLED);

        assertThatThrownBy(() -> book.payOrder(order.getId(), at(1)))
            .isInstanceOf(IllegalStateException.class);
        assertThat(book.getOrder(order.getId()).getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shippedOrder_cannotBeCancelled() {
        OrderBook book = new OrderBook();
        var order = book.openOrder("anna");

        book.finalizeOrder(order.getId(), at(1));
        book.payOrder(order.getId(), at(2));
        book.shipOrder(order.getId());
        assertThat(book.getOrder(order.getId()).getStatus()).isEqualTo(OrderStatus.DELIVERED);

        assertThatThrownBy(() -> book.cancelOrder(order.getId()))
            .isInstanceOf(IllegalStateException.class);
        assertThat(book.getOrder(order.getId()).getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void twoCustomers_interleaveTransitions_independently() {
        OrderBook book = new OrderBook();
        var annas = book.openOrder("anna");
        var brams = book.openOrder("bram");

        book.finalizeOrder(brams.getId(), at(1));
        assertThat(book.getOrder(annas.getId()).getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(book.getOrder(brams.getId()).getStatus()).isEqualTo(OrderStatus.WAITING_FOR_PAYMENT);

        book.payOrder(brams.getId(), at(2));
        book.cancelOrder(annas.getId());
        assertThat(book.getOrder(annas.getId()).getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(book.getOrder(brams.getId()).getStatus()).isEqualTo(OrderStatus.FULFILLING);
    }
}
