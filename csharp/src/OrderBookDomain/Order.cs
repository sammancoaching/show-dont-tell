namespace OrderBookDomain;

public enum OrderStatus
{
    Pending,
    WaitingForPayment,
    Fulfilling,
    Delivered,
    Cancelled
}

public class Order
{
    public int Id { get; }
    public string Customer { get; }
    public OrderStatus Status { get; internal set; }
    public DateTimeOffset? FinalizedAt { get; internal set; }

    internal Order(int id, string customer)
    {
        Id = id;
        Customer = customer;
        Status = OrderStatus.Pending;
    }
}
