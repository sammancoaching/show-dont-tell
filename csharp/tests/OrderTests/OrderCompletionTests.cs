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
}
