namespace OrderDomain;

public class OrderLine
{
    public decimal UnitPrice { get; }
    public int Quantity { get; }
    public bool HasDiscount { get; private set; }

    public decimal Value =>
        HasDiscount ? UnitPrice * Quantity * 0.8m : UnitPrice * Quantity;

    public OrderLine(decimal unitPrice, int quantity)
    {
        UnitPrice = unitPrice;
        Quantity = quantity;
    }

    public void ApplyDiscount() => HasDiscount = true;
}
