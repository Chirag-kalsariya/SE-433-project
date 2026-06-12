package shopping.service;

import shopping.model.ShippingOption;

public class ShippingCalculator {
    private static final double FREE_STANDARD_SHIPPING_THRESHOLD = 50.00;

    public double calculateShipping(ShippingOption option, double subtotal) {
        if (option == ShippingOption.STANDARD && subtotal > FREE_STANDARD_SHIPPING_THRESHOLD) {
            return 0.0;
        }
        return option.getBaseCost();
    }
}
