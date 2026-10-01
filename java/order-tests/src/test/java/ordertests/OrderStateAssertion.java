package ordertests;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.AbstractAssert;

import orderdomain.Order;
import orderdomain.OrderLine;

/**
 * Custom AssertJ matcher that bundles multiple order assertions into one.
 * <p>
 * Participants: you do not need to understand how this works —
 * just recognize the pattern and use it as an example for the agent.
 */
public class OrderStateAssertion extends AbstractAssert<OrderStateAssertion, Order> {

    private OrderStateAssertion(Order actual) {
        super(actual, OrderStateAssertion.class);
    }

    public static OrderStateAssertion assertThat(Order actual) {
        return new OrderStateAssertion(actual);
    }

    public OrderStateAssertion hasState(BigDecimal expectedTotal, List<BigDecimal> expectedLineValues) {
        isNotNull();

        List<String> details = new ArrayList<>();

        BigDecimal actualTotal = actual.getTotal();
        if (actualTotal.compareTo(expectedTotal) != 0) {
            details.add("Total: expected " + expectedTotal + ", but was " + actualTotal);
        }

        List<BigDecimal> actualLineValues = new ArrayList<>();
        for (OrderLine line : actual.getLines()) {
            actualLineValues.add(line.getValue());
        }
        if (!lineValuesMatch(actualLineValues, expectedLineValues)) {
            details.add("Line values: expected " + expectedLineValues
                + ", but was " + actualLineValues);
        }

        if (!details.isEmpty()) {
            failWithMessage("Expected order with total " + expectedTotal
                + " and line values " + expectedLineValues + " but was:\n"
                + String.join("\n", details));
        }

        return this;
    }

    private static boolean lineValuesMatch(List<BigDecimal> actual, List<BigDecimal> expected) {
        if (actual.size() != expected.size()) {
            return false;
        }
        for (int i = 0; i < expected.size(); i++) {
            if (actual.get(i).compareTo(expected.get(i)) != 0) {
                return false;
            }
        }
        return true;
    }
}
