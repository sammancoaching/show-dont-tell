from decimal import Decimal

import pytest

from order_domain import Order


def test_completed_order_total_is_correct():
    order = Order()
    order.add_line(Decimal("100"), 1)
    order.complete()

    assert order.is_completed
    assert order.total == Decimal("100")
    assert sum(order.payments) == Decimal("0")


def test_payment_on_open_order_throws_order_still_unpaid():
    order = Order()
    order.add_line(Decimal("100"), 1)

    with pytest.raises(ValueError):
        order.pay(Decimal("100"))

    assert not order.is_completed
    assert sum(order.payments) == Decimal("0")
    assert order.total - sum(order.payments) == Decimal("100")


def test_completed_order_partial_payment_is_not_fully_paid():
    order = Order()
    order.add_line(Decimal("100"), 1)
    order.complete()

    order.pay(Decimal("40"))

    assert order.is_completed
    assert sum(order.payments) == Decimal("40")
    assert order.total - sum(order.payments) == Decimal("60")


def test_completed_order_payments_summing_to_total_is_fully_paid():
    order = Order()
    order.add_line(Decimal("100"), 1)
    order.complete()

    order.pay(Decimal("40"))
    order.pay(Decimal("60"))

    assert order.is_completed
    assert order.payments == [Decimal("40"), Decimal("60")]
    assert order.total - sum(order.payments) == Decimal("0")


def test_overpayment_throws_amount_paid_unchanged():
    order = Order()
    order.add_line(Decimal("100"), 1)
    order.complete()
    order.pay(Decimal("100"))

    with pytest.raises(ValueError):
        order.pay(Decimal("10"))

    assert order.is_completed
    assert sum(order.payments) == Decimal("100")
    assert order.total - sum(order.payments) == Decimal("0")
