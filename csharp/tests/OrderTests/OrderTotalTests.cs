using NUnit.Framework;
using OrderDomain;

namespace OrderTests;

public class OrderTotalTests
{
    [Test]
    public void DiscountOnFirstLine_TotalAndLineValues()
    {
        var order = new Order();
        order.AddLine(100m, 2);  // line 0: 200 → 160 with discount
        order.AddLine(50m, 1);   // line 1: 50

        order.ApplyDiscount(0);

        Assert.That(order.Total, Is.EqualTo(210m));
        Assert.That(order.Lines[0].Value, Is.EqualTo(160m));
        Assert.That(order.Lines[1].Value, Is.EqualTo(50m));
    }

    [Test]
    public void DiscountOnSecondLine_TotalAndLineValues()
    {
        var order = new Order();
        order.AddLine(100m, 2);  // line 0: 200
        order.AddLine(50m, 1);   // line 1: 50 → 40 with discount

        order.ApplyDiscount(1);

        Assert.That(order.Total, Is.EqualTo(240m));
        Assert.That(order.Lines[0].Value, Is.EqualTo(200m));
        Assert.That(order.Lines[1].Value, Is.EqualTo(40m));
    }

    [Test]
    public void DiscountOnBothLines_TotalAndLineValues()
    {
        var order = new Order();
        order.AddLine(100m, 2);  // line 0: 200 → 160
        order.AddLine(50m, 1);   // line 1: 50 → 40

        order.ApplyDiscount(0);
        order.ApplyDiscount(1);

        Assert.That(order.Total, Is.EqualTo(200m));
        Assert.That(order.Lines[0].Value, Is.EqualTo(160m));
        Assert.That(order.Lines[1].Value, Is.EqualTo(40m));
    }

    [Test]
    public void NoDiscount_TotalAndLineValues()
    {
        var order = new Order();
        order.AddLine(100m, 2);  // line 0: 200
        order.AddLine(50m, 1);   // line 1: 50

        Assert.That(order.Total, Is.EqualTo(250m));
        Assert.That(order.Lines[0].Value, Is.EqualTo(200m));
        Assert.That(order.Lines[1].Value, Is.EqualTo(50m));
    }

    [Test]
    public void SingleLineWithDiscount_TotalAndValue()
    {
        var order = new Order();
        order.AddLine(75m, 4);  // line 0: 300 → 240

        order.ApplyDiscount(0);

        Assert.That(order.Total, Is.EqualTo(240m));
        Assert.That(order.Lines[0].Value, Is.EqualTo(240m));
    }

    [Test]
    public void ThreeLinesOneDiscount_TotalAndLineValues()
    {
        var order = new Order();
        order.AddLine(20m, 1);   // line 0: 20
        order.AddLine(30m, 2);   // line 1: 60 → 48
        order.AddLine(10m, 3);   // line 2: 30

        order.ApplyDiscount(1);

        Assert.That(order.Total, Is.EqualTo(98m));
        Assert.That(order.Lines[0].Value, Is.EqualTo(20m));
        Assert.That(order.Lines[1].Value, Is.EqualTo(48m));
        Assert.That(order.Lines[2].Value, Is.EqualTo(30m));
    }
}
