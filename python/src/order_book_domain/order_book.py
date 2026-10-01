from datetime import datetime, timedelta, timezone
from enum import Enum, auto


class OrderStatus(Enum):
    PENDING = auto()
    WAITING_FOR_PAYMENT = auto()
    FULFILLING = auto()
    DELIVERED = auto()
    CANCELLED = auto()


class Order:
    # Constructed by OrderBook only.
    def __init__(self, id: int, customer: str) -> None:
        self.id = id
        self.customer = customer
        self.status = OrderStatus.PENDING
        self.finalized_at: datetime | None = None


class OrderBook:
    def __init__(self, payment_window: timedelta | None = None) -> None:
        self.payment_window = payment_window or timedelta(days=14)
        self._orders_by_id: dict[int, Order] = {}
        self._next_id = 1

    @property
    def orders(self) -> list[Order]:
        return list(self._orders_by_id.values())

    def open_order(self, customer: str) -> Order:
        order = Order(self._next_id, customer)
        self._next_id += 1
        self._orders_by_id[order.id] = order
        return order

    def get_order(self, order_id: int) -> Order:
        try:
            return self._orders_by_id[order_id]
        except KeyError:
            raise ValueError(f"Unknown order id {order_id}.") from None

    def finalize_order(self, order_id: int, finalized_at: datetime) -> None:
        order = self.get_order(order_id)
        _require_status(order, OrderStatus.PENDING, "finalize")
        order.status = OrderStatus.WAITING_FOR_PAYMENT
        order.finalized_at = finalized_at

    def pay_order(self, order_id: int, paid_at: datetime) -> None:
        order = self.get_order(order_id)
        _require_status(order, OrderStatus.WAITING_FOR_PAYMENT, "pay")
        if paid_at - order.finalized_at > self.payment_window:
            order.status = OrderStatus.CANCELLED
            raise ValueError(
                f"Payment for order {order_id} arrived after the payment window; the order was cancelled."
            )
        order.status = OrderStatus.FULFILLING

    def ship_order(self, order_id: int) -> None:
        order = self.get_order(order_id)
        _require_status(order, OrderStatus.FULFILLING, "ship")
        order.status = OrderStatus.DELIVERED

    def cancel_order(self, order_id: int) -> None:
        order = self.get_order(order_id)
        if order.status not in (OrderStatus.PENDING, OrderStatus.WAITING_FOR_PAYMENT):
            raise ValueError(f"Cannot cancel order {order_id} in status {order.status}.")
        order.status = OrderStatus.CANCELLED


def _require_status(order: Order, expected: OrderStatus, action: str) -> None:
    if order.status is not expected:
        raise ValueError(
            f"Cannot {action} order {order.id} in status {order.status} (expected {expected})."
        )
