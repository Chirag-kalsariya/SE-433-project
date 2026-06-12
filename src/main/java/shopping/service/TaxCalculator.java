package shopping.service;

import java.util.Set;

public class TaxCalculator {
    private static final double TAX_RATE = 0.06;
    private static final Set<String> TAXABLE_STATES = Set.of("IL", "CA", "NY");

    public double calculateTax(String state, double subtotal) {
        if (state == null || !TAXABLE_STATES.contains(state.trim().toUpperCase())) {
            return 0.0;
        }
        return round(subtotal * TAX_RATE);
    }

    public boolean isTaxableState(String state) {
        return state != null && TAXABLE_STATES.contains(state.trim().toUpperCase());
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
