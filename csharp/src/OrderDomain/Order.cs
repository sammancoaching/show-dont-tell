namespace OrderDomain;

public class Order
{
    public List<OrderLine> Lines { get; } = new();

    public decimal Total => Lines.Sum(l => l.Value);

    public bool IsCompleted { get; private set; }

    public void AddLine(decimal unitPrice, int quantity)
        => Lines.Add(new OrderLine(unitPrice, quantity));

    public void ApplyDiscount(int lineIndex)
        => Lines[lineIndex].ApplyDiscount();

    public void Complete() => IsCompleted = true;
}
