package shopping.model;

public class Customer {
    private final String name;
    private final String state;

    public Customer(String name, String state) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name is required.");
        }
        if (state == null || state.isBlank()) {
            throw new IllegalArgumentException("State of residence is required.");
        }
        this.name = name.trim();
        this.state = state.trim().toUpperCase();
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }
}
