package shopping.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaxCalculatorTest {

    private final TaxCalculator calculator = new TaxCalculator();

    @Test
    void appliesSixPercentTaxForTaxableStates() {
        assertEquals(6.00, calculator.calculateTax("IL", 100.00));
        assertEquals(6.00, calculator.calculateTax("ca", 100.00));
        assertEquals(6.00, calculator.calculateTax(" NY ", 100.00));
        assertEquals(0.60, calculator.calculateTax("IL", 10.005));
    }

    @Test
    void appliesNoTaxForOtherStates() {
        assertEquals(0.0, calculator.calculateTax("TX", 100.00));
        assertEquals(0.0, calculator.calculateTax(null, 100.00));
    }

    @Test
    void identifiesTaxableStates() {
        assertTrue(calculator.isTaxableState("IL"));
        assertFalse(calculator.isTaxableState("TX"));
        assertFalse(calculator.isTaxableState(null));
    }
}
