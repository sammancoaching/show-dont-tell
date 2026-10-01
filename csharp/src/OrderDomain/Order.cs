namespace OrderDomain;

public class Order
{
    private readonly List<decimal> payments = new();

    public List<OrderLine> Lines { get; } = new();

    public decimal Total => Lines.Sum(l => l.Value);

    public bool IsCompleted { get; private set; }

    public IReadOnlyList<decimal> Payments => payments;

    public decimal AmountPaid => payments.Sum();

    public decimal Outstanding => Total - AmountPaid;

    public bool IsFullyPaid => IsCompleted && Outstanding == 0m;

    public void AddLine(decimal unitPrice, int quantity)
        => Lines.Add(new OrderLine(unitPrice, quantity));

    public void ApplyDiscount(int lineIndex)
        => Lines[lineIndex].ApplyDiscount();

    public void Complete() => IsCompleted = true;

    public void Pay(decimal amount)
    {
        if (!IsCompleted)
            throw new InvalidOperationException("Cannot pay an open order.");
        if (amount <= 0m)
            throw new ArgumentOutOfRangeException(nameof(amount), amount, "Payment must be positive.");
        if (amount > Outstanding)
            throw new InvalidOperationException("Payment exceeds outstanding amount.");
        payments.Add(amount);
    }
}
