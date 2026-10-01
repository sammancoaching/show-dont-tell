from decimal import Decimal

from order_domain import Order


def test_completed_order_total_is_correct():
    order = Order()
    order.add_line(Decimal("100"), 1)
    order.complete()

    assert order.is_fully_paid(Decimal("100"))
