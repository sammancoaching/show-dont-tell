"""Custom matcher that bundles multiple order assertions into one.

Participants: you do not need to understand how this works —
just recognize the pattern and use it as an example for the agent.
"""

from decimal import Decimal

from order_domain import Order


def assert_order_state(
    order: Order, *, total: Decimal, line_values: list[Decimal]
) -> None:
    actual_total = order.total
    actual_line_values = [line.value for line in order.lines]

    details: list[str] = []
    if actual_total != total:
        details.append(f"Total: expected {total}, but was {actual_total}")
    if actual_line_values != line_values:
        details.append(
            f"Line values: expected [{', '.join(str(v) for v in line_values)}], "
            f"but was [{', '.join(str(v) for v in actual_line_values)}]"
        )

    if details:
        header = (
            f"Expected order with total {total} and line values "
            f"[{', '.join(str(v) for v in line_values)}] but was:"
        )
        raise AssertionError("\n".join([header, *details]))
