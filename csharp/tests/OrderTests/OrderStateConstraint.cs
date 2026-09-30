using System.Linq;
using NUnit.Framework.Constraints;
using OrderDomain;

namespace OrderTests;

/// <summary>
/// Custom NUnit matcher that bundles multiple order assertions into one.
/// Participants: you do not need to understand how this works —
/// just recognize the pattern and use it as an example for the agent.
/// </summary>
public static class Has
{
    public static IResolveConstraint OrderState(decimal expectedTotal, decimal[] expectedLineValues)
        => new OrderStateConstraint(expectedTotal, expectedLineValues);
}

public class OrderStateConstraint : Constraint
{
    private readonly decimal _expectedTotal;
    private readonly decimal[] _expectedLineValues;

    public OrderStateConstraint(decimal expectedTotal, decimal[] expectedLineValues)
    {
        _expectedTotal = expectedTotal;
        _expectedLineValues = expectedLineValues;
    }

    public override string Description =>
        $"order with total {_expectedTotal} and line values [{string.Join(", ", _expectedLineValues)}]";

    public override ConstraintResult ApplyTo<TActual>(TActual actual)
    {
        var order = (Order)(object)actual!;
        var actualTotal = order.Total;
        var actualLineValues = order.Lines.Select(l => l.Value).ToArray();

        var details = new List<string>();
        if (actualTotal != _expectedTotal)
            details.Add($"Total: expected {_expectedTotal}, but was {actualTotal}");
        if (!actualLineValues.SequenceEqual(_expectedLineValues))
            details.Add(
                $"Line values: expected [{string.Join(", ", _expectedLineValues)}], " +
                $"but was [{string.Join(", ", actualLineValues)}]");

        return new OrderStateResult(this, actual!, details.Count == 0, details);
    }

    /// <summary>
    /// A ConstraintResult that carries the per-field mismatch details and
    /// writes them as additional lines in the failure message, e.g.
    ///
    ///   Expected: order with total 210 and line values [160, 50]
    ///   But was:  &lt;Order&gt;
    ///     Total: expected 210, but was 250
    ///     Line values: expected [160, 50], but was [200, 50]
    /// </summary>
    private sealed class OrderStateResult : ConstraintResult
    {
        private readonly IReadOnlyList<string> _details;

        public OrderStateResult(Constraint constraint, object? actual, bool isSuccess, IReadOnlyList<string> details)
            : base(constraint, actual, isSuccess)
        {
            _details = details;
        }

        public override void WriteAdditionalLinesTo(MessageWriter writer)
        {
            foreach (var detail in _details)
                writer.WriteMessageLine(detail);
        }
    }
}
