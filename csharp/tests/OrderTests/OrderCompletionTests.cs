using NUnit.Framework;
using OrderDomain;

namespace OrderTests;

public class OrderCompletionTests
{
    [Test]
    public void CompletedOrder_TotalIsCorrect()
    {
        var order = new Order();
        order.AddLine(100m, 1);
        order.Complete();

        Assert.That(order.Total, Is.EqualTo(100m));
    }
    
    [Test]
    public void PaymentOnOpenOrder_Throws_OrderStillUnpaid()
    {
        var order = new Order();
        order.AddLine(100m, 1);

        Assert.Throws<InvalidOperationException>(() => order.Pay(100m));

        Assert.That(order.IsCompleted, Is.False);
        Assert.That(order.AmountPaid, Is.EqualTo(0m));
    }

    [Test]
    public void CompletedOrder_PartialPayment_IsNotFullyPaid()
    {
        var order = new Order();
        order.AddLine(100m, 1);
        order.Complete();

        order.Pay(40m);

        Assert.That(order.AmountPaid, Is.EqualTo(40m));
        Assert.That(order.Outstanding, Is.EqualTo(60m));
        Assert.That(order.IsFullyPaid, Is.False);
    }

    [Test]
    public void CompletedOrder_PaymentsSummingToTotal_IsFullyPaid()
    {
        var order = new Order();
        order.AddLine(100m, 1);
        order.Complete();

        order.Pay(40m);
        order.Pay(60m);

        Assert.That(order.Payments, Is.EqualTo(new[] { 40m, 60m }));
        Assert.That(order.Outstanding, Is.EqualTo(0m));
        Assert.That(order.IsFullyPaid, Is.True);
    }

    [Test]
    public void Overpayment_Throws_AmountPaidUnchanged()
    {
        var order = new Order();
        order.AddLine(100m, 1);
        order.Complete();
        order.Pay(100m);

        Assert.Throws<InvalidOperationException>(() => order.Pay(10m));

        Assert.That(order.AmountPaid, Is.EqualTo(100m));
        Assert.That(order.IsFullyPaid, Is.True);
    }
}
