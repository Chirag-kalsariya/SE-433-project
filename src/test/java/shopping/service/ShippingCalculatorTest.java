package shopping.service;

import org.junit.jupiter.api.Test;
import shopping.model.ShippingOption;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShippingCalculatorTest {

    private final ShippingCalculator calculator = new ShippingCalculator();

    @Test
    void standardShippingCostsTenWhenSubtotalIsFiftyOrLess() {
        assertEquals(10.00, calculator.calculateShipping(ShippingOption.STANDARD, 50.00));
        assertEquals(10.00, calculator.calculateShipping(ShippingOption.STANDARD, 10.00));
    }

    @Test
    void standardShippingIsFreeWhenSubtotalIsOverFifty() {
        assertEquals(0.0, calculator.calculateShipping(ShippingOption.STANDARD, 50.01));
        assertEquals(0.0, calculator.calculateShipping(ShippingOption.STANDARD, 100.00));
    }

    @Test
    void nextDayShippingAlwaysCostsTwentyFive() {
        assertEquals(25.00, calculator.calculateShipping(ShippingOption.NEXT_DAY, 10.00));
        assertEquals(25.00, calculator.calculateShipping(ShippingOption.NEXT_DAY, 500.00));
    }
}
