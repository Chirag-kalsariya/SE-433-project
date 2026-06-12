package shopping.service;

import org.junit.jupiter.api.Test;
import shopping.model.ShippingOption;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderTotalCalculatorTest {

    private final OrderTotalCalculator calculator =
            new OrderTotalCalculator(new TaxCalculator(), new ShippingCalculator());

    @Test
    void calculatesTotalWithTaxAndShipping() {
        double total = calculator.calculateTotal("IL", ShippingOption.STANDARD, 100.00);
        assertEquals(106.00, total);
    }

    @Test
    void delegatesSubtotalTaxAndShippingCalculations() {
        assertEquals(40.00, calculator.calculateSubtotal(40.00));
        assertEquals(2.40, calculator.calculateTax("CA", 40.00));
        assertEquals(0.0, calculator.calculateShipping(ShippingOption.STANDARD, 60.00));
    }

    @Test
    void roundsSubtotalAndTotalToTwoDecimalPlaces() {
        assertEquals(10.01, calculator.calculateSubtotal(10.005));
        double total = calculator.calculateTotal("NY", ShippingOption.NEXT_DAY, 10.005);
        assertEquals(35.61, total);
    }
}
