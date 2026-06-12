package shopping.validation;

public final class OrderValidator {
    public static final double MIN_PURCHASE_AMOUNT = 1.00;
    public static final double MAX_PURCHASE_AMOUNT = 99_999.99;

    private OrderValidator() {
    }

    public static void validateQuantity(int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be an integer of at least 1.");
        }
    }

    public static void validatePurchaseAmount(double amount) {
        if (amount < MIN_PURCHASE_AMOUNT) {
            throw new IllegalArgumentException(
                    "Purchase amount must be at least $" + String.format("%.2f", MIN_PURCHASE_AMOUNT) + ".");
        }
        if (amount > MAX_PURCHASE_AMOUNT) {
            throw new IllegalArgumentException(
                    "Purchase amount must not exceed $" + String.format("%.2f", MAX_PURCHASE_AMOUNT) + ".");
        }
    }
}
