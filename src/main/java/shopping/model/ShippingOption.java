package shopping.model;

public enum ShippingOption {
    STANDARD("Standard", 10.00),
    NEXT_DAY("Next day", 25.00);

    private final String displayName;
    private final double baseCost;

    ShippingOption(String displayName, double baseCost) {
        this.displayName = displayName;
        this.baseCost = baseCost;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getBaseCost() {
        return baseCost;
    }

    public static ShippingOption fromInput(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Shipping option is required.");
        }
        String normalized = input.trim().toUpperCase().replace(' ', '_');
        if ("STANDARD".equals(normalized)) {
            return STANDARD;
        }
        if ("NEXT_DAY".equals(normalized) || "NEXTDAY".equals(normalized)) {
            return NEXT_DAY;
        }
        throw new IllegalArgumentException("Invalid shipping option. Choose Standard or Next day.");
    }
}
