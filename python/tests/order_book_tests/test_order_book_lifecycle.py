from datetime import datetime, timedelta, timezone

import pytest

from order_book_domain import OrderBook, OrderStatus

DAY0 = datetime(2025, 6, 1, tzinfo=timezone.utc)


def at(day: int) -> datetime:
    return DAY0 + timedelta(days=day)


def test_finalize_then_pay_moves_order_through_two_states():
    book = OrderBook()
    order = book.open_order("anna")

    book.finalize_order(order.id, at(1))
    assert book.get_order(order.id).status is OrderStatus.WAITING_FOR_PAYMENT

    book.pay_order(order.id, at(2))
    assert book.get_order(order.id).status is OrderStatus.FULFILLING


def test_pay_then_ship_delivers_the_order():
    book = OrderBook()
    order = book.open_order("anna")

    book.finalize_order(order.id, at(1))
    assert book.get_order(order.id).status is OrderStatus.WAITING_FOR_PAYMENT

    book.pay_order(order.id, at(2))
    book.ship_order(order.id)
    assert book.get_order(order.id).status is OrderStatus.DELIVERED


def test_pay_within_window_then_late_pay_on_second_order_cancels_only_that_order():
    book = OrderBook()
    first = book.open_order("anna")
    second = book.open_order("bram")

    book.finalize_order(first.id, at(1))
    book.finalize_order(second.id, at(1))
    assert len(book.orders) == 2

    book.pay_order(first.id, at(5))
    assert book.get_order(first.id).status is OrderStatus.FULFILLING

    with pytest.raises(ValueError):
        book.pay_order(second.id, at(30))
    assert book.get_order(second.id).status is OrderStatus.CANCELLED


def test_pay_before_finalize_throws_then_finalize_still_works():
    book = OrderBook()
    order = book.open_order("anna")

    with pytest.raises(ValueError):
        book.pay_order(order.id, at(1))
    assert book.get_order(order.id).status is OrderStatus.PENDING

    book.finalize_order(order.id, at(2))
    assert book.get_order(order.id).status is OrderStatus.WAITING_FOR_PAYMENT


def test_cancel_pending_order_then_pay_after_cancel_throws_again():
    book = OrderBook()
    order = book.open_order("anna")

    book.cancel_order(order.id)
    assert book.get_order(order.id).status is OrderStatus.CANCELLED

    with pytest.raises(ValueError):
        book.pay_order(order.id, at(1))
    assert book.get_order(order.id).status is OrderStatus.CANCELLED


def test_shipped_order_cannot_be_cancelled():
    book = OrderBook()
    order = book.open_order("anna")

    book.finalize_order(order.id, at(1))
    book.pay_order(order.id, at(2))
    book.ship_order(order.id)
    assert book.get_order(order.id).status is OrderStatus.DELIVERED

    with pytest.raises(ValueError):
        book.cancel_order(order.id)
    assert book.get_order(order.id).status is OrderStatus.DELIVERED


def test_two_customers_interleave_transitions_independently():
    book = OrderBook()
    annas = book.open_order("anna")
    brams = book.open_order("bram")

    book.finalize_order(brams.id, at(1))
    assert book.get_order(annas.id).status is OrderStatus.PENDING
    assert book.get_order(brams.id).status is OrderStatus.WAITING_FOR_PAYMENT

    book.pay_order(brams.id, at(2))
    book.cancel_order(annas.id)
    assert book.get_order(annas.id).status is OrderStatus.CANCELLED
    assert book.get_order(brams.id).status is OrderStatus.FULFILLING
