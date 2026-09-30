namespace OrderBookDomain;

public class OrderBook
{
    private readonly TimeSpan _paymentWindow;
    private readonly Dictionary<int, Order> _ordersById = new();
    private int _nextId = 1;

    public OrderBook(TimeSpan? paymentWindow = null)
        => _paymentWindow = paymentWindow ?? TimeSpan.FromDays(14);

    public IReadOnlyList<Order> Orders => _ordersById.Values.ToList();

    public Order OpenOrder(string customer)
    {
        var order = new Order(_nextId++, customer);
        _ordersById[order.Id] = order;
        return order;
    }

    public Order GetOrder(int orderId)
        => _ordersById.TryGetValue(orderId, out var order)
            ? order
            : throw new InvalidOperationException($"Unknown order id {orderId}.");

    public void FinalizeOrder(int orderId, DateTimeOffset finalizedAt)
    {
        var order = GetOrder(orderId);
        RequireStatus(order, OrderStatus.Pending, "finalize");
        order.Status = OrderStatus.WaitingForPayment;
        order.FinalizedAt = finalizedAt;
    }

    public void PayOrder(int orderId, DateTimeOffset paidAt)
    {
        var order = GetOrder(orderId);
        RequireStatus(order, OrderStatus.WaitingForPayment, "pay");
        if (paidAt - order.FinalizedAt > _paymentWindow)
        {
            order.Status = OrderStatus.Cancelled;
            throw new InvalidOperationException(
                $"Payment for order {orderId} arrived after the payment window; the order was cancelled.");
        }
        order.Status = OrderStatus.Fulfilling;
    }

    public void ShipOrder(int orderId)
    {
        var order = GetOrder(orderId);
        RequireStatus(order, OrderStatus.Fulfilling, "ship");
        order.Status = OrderStatus.Delivered;
    }

    public void CancelOrder(int orderId)
    {
        var order = GetOrder(orderId);
        if (order.Status is not (OrderStatus.Pending or OrderStatus.WaitingForPayment))
            throw new InvalidOperationException(
                $"Cannot cancel order {orderId} in status {order.Status}.");
        order.Status = OrderStatus.Cancelled;
    }

    private static void RequireStatus(Order order, OrderStatus expected, string action)
    {
        if (order.Status != expected)
            throw new InvalidOperationException(
                $"Cannot {action} order {order.Id} in status {order.Status} (expected {expected}).");
    }
}
