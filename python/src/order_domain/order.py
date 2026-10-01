from decimal import Decimal


class OrderLine:
    def __init__(self, unit_price: Decimal, quantity: int):
        self.unit_price = unit_price
        self.quantity = quantity
        self.has_discount = False

    def apply_discount(self) -> None:
        self.has_discount = True

    @property
    def value(self) -> Decimal:
        if self.has_discount:
            return self.unit_price * self.quantity * Decimal("0.8")
        return self.unit_price * self.quantity


class Order:
    def __init__(self):
        self.lines: list[OrderLine] = []
        self.is_completed = False

    @property
    def total(self) -> Decimal:
        return sum(line.value for line in self.lines)

    def add_line(self, unit_price: Decimal, quantity: int) -> None:
        self.lines.append(OrderLine(unit_price, quantity))

    def apply_discount(self, line_index: int) -> None:
        self.lines[line_index].apply_discount()

    def complete(self) -> None:
        self.is_completed = True

    def is_fully_paid(self, expected_total: Decimal) -> bool:
        return self.is_completed and self.total == expected_total
