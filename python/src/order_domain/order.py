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
        self._payments: list[Decimal] = []

    @property
    def total(self) -> Decimal:
        return sum(line.value for line in self.lines)

    @property
    def payments(self) -> list[Decimal]:
        return list(self._payments)

    @property
    def amount_paid(self) -> Decimal:
        return sum(self._payments, Decimal("0"))

    @property
    def outstanding(self) -> Decimal:
        return self.total - self.amount_paid

    @property
    def is_fully_paid(self) -> bool:
        return self.is_completed and self.outstanding == 0

    def add_line(self, unit_price: Decimal, quantity: int) -> None:
        self.lines.append(OrderLine(unit_price, quantity))

    def apply_discount(self, line_index: int) -> None:
        self.lines[line_index].apply_discount()

    def complete(self) -> None:
        self.is_completed = True

    def pay(self, amount: Decimal) -> None:
        if not self.is_completed:
            raise ValueError("Cannot pay an open order.")
        if amount <= 0:
            raise ValueError("Payment must be positive.")
        if amount > self.outstanding:
            raise ValueError("Payment exceeds outstanding amount.")
        self._payments.append(amount)
