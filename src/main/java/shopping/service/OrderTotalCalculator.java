package shopping.service;

import shopping.model.ShippingOption;

public class OrderTotalCalculator {
    private final TaxCalculator taxCalculator;
    private final ShippingCalculator shippingCalculator;

    public OrderTotalCalculator(TaxCalculator taxCalculator, ShippingCalculator shippingCalculator) {
        this.taxCalculator = taxCalculator;
        this.shippingCalculator = shippingCalculator;
    }

    public double calculateSubtotal(double subtotal) {
        return round(subtotal);
    }

    public double calculateTax(String state, double subtotal) {
        return taxCalculator.calculateTax(state, subtotal);
    }

    public double calculateShipping(ShippingOption option, double subtotal) {
        return shippingCalculator.calculateShipping(option, subtotal);
    }

    public double calculateTotal(String state, ShippingOption shippingOption, double subtotal) {
        double tax = calculateTax(state, subtotal);
        double shipping = calculateShipping(shippingOption, subtotal);
        return round(subtotal + tax + shipping);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
