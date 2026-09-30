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

        Assert.That(order.IsFullyPaid(100m), Is.True);
    }
}
