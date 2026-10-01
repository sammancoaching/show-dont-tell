from decimal import Decimal

from order_domain import Order
from order_state import assert_order_state


# This test was refactored - notice the custom matcher.
# The other tests still need the same treatment.
def test_discount_on_first_line_total_and_line_values():
    order = Order()
    order.add_line(Decimal("100"), 2)  # line 0: 200 -> 160 with discount
    order.add_line(Decimal("50"), 1)  # line 1: 50

    order.apply_discount(0)

    assert_order_state(
        order, total=Decimal("210"), line_values=[Decimal("160"), Decimal("50")]
    )


def test_discount_on_second_line_total_and_line_values():
    order = Order()
    order.add_line(Decimal("100"), 2)  # line 0: 200
    order.add_line(Decimal("50"), 1)  # line 1: 50 -> 40 with discount

    order.apply_discount(1)

    assert order.total == Decimal("240")
    assert order.lines[0].value == Decimal("200")
    assert order.lines[1].value == Decimal("40")


def test_discount_on_both_lines_total_and_line_values():
    order = Order()
    order.add_line(Decimal("100"), 2)  # line 0: 200 -> 160
    order.add_line(Decimal("50"), 1)  # line 1: 50 -> 40

    order.apply_discount(0)
    order.apply_discount(1)

    assert order.total == Decimal("200")
    assert order.lines[0].value == Decimal("160")
    assert order.lines[1].value == Decimal("40")


def test_no_discount_total_and_line_values():
    order = Order()
    order.add_line(Decimal("100"), 2)  # line 0: 200
    order.add_line(Decimal("50"), 1)  # line 1: 50

    assert order.total == Decimal("250")
    assert order.lines[0].value == Decimal("200")
    assert order.lines[1].value == Decimal("50")


def test_single_line_with_discount_total_and_value():
    order = Order()
    order.add_line(Decimal("75"), 4)  # line 0: 300 -> 240

    order.apply_discount(0)

    assert order.total == Decimal("240")
    assert order.lines[0].value == Decimal("240")


def test_three_lines_one_discount_total_and_line_values():
    order = Order()
    order.add_line(Decimal("20"), 1)  # line 0: 20
    order.add_line(Decimal("30"), 2)  # line 1: 60 -> 48
    order.add_line(Decimal("10"), 3)  # line 2: 30

    order.apply_discount(1)

    assert order.total == Decimal("98")
    assert order.lines[0].value == Decimal("20")
    assert order.lines[1].value == Decimal("48")
    assert order.lines[2].value == Decimal("30")
