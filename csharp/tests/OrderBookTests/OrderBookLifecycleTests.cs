using NUnit.Framework;
using OrderBookDomain;

namespace OrderBookTests;

public class OrderBookLifecycleTests
{
    private static readonly DateTimeOffset Day0 = new(2025, 6, 1, 0, 0, 0, TimeSpan.Zero);

    private static DateTimeOffset At(int day) => Day0.AddDays(day);

    [Test]
    public void FinalizeThenPay_MovesOrderThroughTwoStates()
    {
        var book = new OrderBook();
        var order = book.OpenOrder("anna");

        book.FinalizeOrder(order.Id, At(1));
        Assert.That(book.GetOrder(order.Id).Status, Is.EqualTo(OrderStatus.WaitingForPayment));

        book.PayOrder(order.Id, At(2));
        Assert.That(book.GetOrder(order.Id).Status, Is.EqualTo(OrderStatus.Fulfilling));
    }

    [Test]
    public void PayThenShip_DeliversTheOrder()
    {
        var book = new OrderBook();
        var order = book.OpenOrder("anna");

        book.FinalizeOrder(order.Id, At(1));
        Assert.That(book.GetOrder(order.Id).Status, Is.EqualTo(OrderStatus.WaitingForPayment));

        book.PayOrder(order.Id, At(2));
        book.ShipOrder(order.Id);
        Assert.That(book.GetOrder(order.Id).Status, Is.EqualTo(OrderStatus.Delivered));
    }

    [Test]
    public void PayWithinWindow_ThenLatePayOnSecondOrder_CancelsOnlyThatOrder()
    {
        var book = new OrderBook();
        var first = book.OpenOrder("anna");
        var second = book.OpenOrder("bram");

        book.FinalizeOrder(first.Id, At(1));
        book.FinalizeOrder(second.Id, At(1));
        Assert.That(book.Orders, Has.Count.EqualTo(2));

        book.PayOrder(first.Id, At(5));
        Assert.That(book.GetOrder(first.Id).Status, Is.EqualTo(OrderStatus.Fulfilling));

        Assert.Throws<InvalidOperationException>(() => book.PayOrder(second.Id, At(30)));
        Assert.That(book.GetOrder(second.Id).Status, Is.EqualTo(OrderStatus.Cancelled));
    }

    [Test]
    public void PayBeforeFinalize_Throws_ThenFinalizeStillWorks()
    {
        var book = new OrderBook();
        var order = book.OpenOrder("anna");

        Assert.Throws<InvalidOperationException>(() => book.PayOrder(order.Id, At(1)));
        Assert.That(book.GetOrder(order.Id).Status, Is.EqualTo(OrderStatus.Pending));

        book.FinalizeOrder(order.Id, At(2));
        Assert.That(book.GetOrder(order.Id).Status, Is.EqualTo(OrderStatus.WaitingForPayment));
    }

    [Test]
    public void CancelPendingOrder_ThenPayAfterCancel_ThrowsAgain()
    {
        var book = new OrderBook();
        var order = book.OpenOrder("anna");

        book.CancelOrder(order.Id);
        Assert.That(book.GetOrder(order.Id).Status, Is.EqualTo(OrderStatus.Cancelled));

        Assert.Throws<InvalidOperationException>(() => book.PayOrder(order.Id, At(1)));
        Assert.That(book.GetOrder(order.Id).Status, Is.EqualTo(OrderStatus.Cancelled));
    }

    [Test]
    public void ShippedOrder_CannotBeCancelled()
    {
        var book = new OrderBook();
        var order = book.OpenOrder("anna");

        book.FinalizeOrder(order.Id, At(1));
        book.PayOrder(order.Id, At(2));
        book.ShipOrder(order.Id);
        Assert.That(book.GetOrder(order.Id).Status, Is.EqualTo(OrderStatus.Delivered));

        Assert.Throws<InvalidOperationException>(() => book.CancelOrder(order.Id));
        Assert.That(book.GetOrder(order.Id).Status, Is.EqualTo(OrderStatus.Delivered));
    }

    [Test]
    public void TwoCustomers_InterleaveTransitions_Independently()
    {
        var book = new OrderBook();
        var annas = book.OpenOrder("anna");
        var brams = book.OpenOrder("bram");

        book.FinalizeOrder(brams.Id, At(1));
        Assert.That(book.GetOrder(annas.Id).Status, Is.EqualTo(OrderStatus.Pending));
        Assert.That(book.GetOrder(brams.Id).Status, Is.EqualTo(OrderStatus.WaitingForPayment));

        book.PayOrder(brams.Id, At(2));
        book.CancelOrder(annas.Id);
        Assert.That(book.GetOrder(annas.Id).Status, Is.EqualTo(OrderStatus.Cancelled));
        Assert.That(book.GetOrder(brams.Id).Status, Is.EqualTo(OrderStatus.Fulfilling));
    }
}
