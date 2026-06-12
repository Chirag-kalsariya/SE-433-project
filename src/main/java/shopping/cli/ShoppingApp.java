package shopping.cli;

import shopping.cart.ShoppingCart;
import shopping.catalog.ItemCatalog;
import shopping.model.CartItem;
import shopping.model.Customer;
import shopping.model.Item;
import shopping.model.ShippingOption;
import shopping.service.OrderTotalCalculator;
import shopping.service.ShippingCalculator;
import shopping.service.TaxCalculator;

import java.io.PrintStream;
import java.util.Scanner;

public class ShoppingApp {
    private final Scanner scanner;
    private final PrintStream out;
    private final ItemCatalog catalog;
    private final ShoppingCart cart;

    public ShoppingApp(Scanner scanner) {
        this(scanner, System.out, new ItemCatalog(),
                new OrderTotalCalculator(new TaxCalculator(), new ShippingCalculator()));
    }

    ShoppingApp(Scanner scanner, PrintStream out, ItemCatalog catalog, OrderTotalCalculator totalCalculator) {
        this.scanner = scanner;
        this.out = out;
        this.catalog = catalog;
        this.cart = new ShoppingCart(catalog, totalCalculator);
    }

    ShoppingApp(Scanner scanner, PrintStream out, ShoppingCart cart, ItemCatalog catalog) {
        this.scanner = scanner;
        this.out = out;
        this.cart = cart;
        this.catalog = catalog;
    }

    public static void main(String[] args) {
        ShoppingApp app = new ShoppingApp(new Scanner(System.in));
        app.run();
    }

    public void run() {
        printWelcome();
        collectCustomerInfo();
        collectShippingOption();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            if ("1".equals(choice)) {
                handleAddItem();
            } else if ("2".equals(choice)) {
                handleGetTotal();
            } else if ("3".equals(choice)) {
                handleViewCart();
            } else if ("4".equals(choice)) {
                handleEditQuantity();
            } else if ("5".equals(choice)) {
                handleRemoveItem();
            } else if ("6".equals(choice)) {
                handleCheckout();
                running = false;
            } else if ("7".equals(choice)) {
                running = false;
            } else {
                out.println("Invalid option. Please choose 1-7.");
            }
        }
        out.println("Thank you for shopping with us!");
    }

    private void printWelcome() {
        out.println("=== Shopping Application ===");
        out.println();
    }

    private void collectCustomerInfo() {
        String name = readCustomerName();
        String state = readCustomerState();
        cart.setCustomer(new Customer(name, state));
        out.println("Welcome, " + cart.getCustomer().orElseThrow().getName() + "!");
        out.println();
    }

    private String readCustomerName() {
        while (true) {
            out.print("Enter your name: ");
            String name = scanner.nextLine();
            if (name != null && !name.isBlank()) {
                return name;
            }
            out.println("Error: Customer name is required.");
        }
    }

    private String readCustomerState() {
        while (true) {
            out.print("Enter your state of residence (e.g. IL, CA, NY, TX): ");
            String state = scanner.nextLine();
            if (state != null && !state.isBlank()) {
                return state;
            }
            out.println("Error: State of residence is required.");
        }
    }

    private void collectShippingOption() {
        while (true) {
            out.println("Select a shipping option:");
            out.println("1. Standard");
            out.println("2. Next day");
            out.print("Enter choice (1 or 2): ");
            String choice = scanner.nextLine();
            try {
                ShippingOption option = parseShippingChoice(choice);
                cart.setShippingOption(option);
                out.println("Shipping option set to: " + option.getDisplayName());
                out.println();
                return;
            } catch (IllegalArgumentException ex) {
                out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void printMenu() {
        out.println("--- Menu ---");
        out.println("1. Add item to shopping cart");
        out.println("2. Get current total");
        out.println("3. See contents of shopping cart");
        out.println("4. Edit quantity of items in shopping cart");
        out.println("5. Remove items from shopping cart");
        out.println("6. Checkout");
        out.println("7. Exit");
        out.print("Select an action: ");
    }

    private void printAvailableItems() {
        out.println("Available items:");
        for (Item item : catalog.getAllItems()) {
            out.printf("  - %s ($%.2f)%n", item.getName(), item.getPrice());
        }
    }

    private void handleAddItem() {
        printAvailableItems();
        out.print("Enter item name: ");
        String itemName = scanner.nextLine();
        out.print("Enter quantity: ");
        try {
            int quantity = parseQuantity(scanner.nextLine());
            int count = cart.addItem(itemName, quantity);
            out.println("Item added. Current count of items in cart: " + count);
        } catch (IllegalArgumentException ex) {
            out.println("Error: " + ex.getMessage());
        }
        out.println();
    }

    void handleGetTotal() {
        try {
            double subtotal = cart.getSubtotal();
            double total = cart.getCurrentTotal();
            out.printf("Subtotal: $%.2f%n", subtotal);
            out.printf("Current total (including tax and shipping): $%.2f%n", total);
        } catch (IllegalStateException ex) {
            out.println("Error: " + ex.getMessage());
        }
        out.println();
    }

    private void handleViewCart() {
        if (cart.isEmpty()) {
            out.println("Your shopping cart is empty.");
        } else {
            out.println("Shopping cart contents:");
            for (CartItem cartItem : cart.getContents()) {
                out.printf(
                        "  - %s | Qty: %d | Line total: $%.2f%n",
                        cartItem.getName(),
                        cartItem.getQuantity(),
                        cartItem.getLineTotal());
            }
        }
        out.println();
    }

    void handleEditQuantity() {
        if (cart.isEmpty()) {
            out.println("Your shopping cart is empty.");
            out.println();
            return;
        }
        handleViewCart();
        out.print("Enter item name to edit: ");
        String itemName = scanner.nextLine();
        out.print("Enter new quantity: ");
        try {
            int quantity = parseQuantity(scanner.nextLine());
            cart.editQuantity(itemName, quantity);
            out.println("Quantity updated.");
        } catch (IllegalArgumentException ex) {
            out.println("Error: " + ex.getMessage());
        }
        out.println();
    }

    void handleRemoveItem() {
        if (cart.isEmpty()) {
            out.println("Your shopping cart is empty.");
            out.println();
            return;
        }
        handleViewCart();
        out.print("Enter item name to remove: ");
        String itemName = scanner.nextLine();
        try {
            cart.removeItem(itemName);
            out.println("Item removed.");
        } catch (IllegalArgumentException ex) {
            out.println("Error: " + ex.getMessage());
        }
        out.println();
    }

    void handleCheckout() {
        try {
            cart.checkout();
            out.println("transaction completed");
        } catch (IllegalStateException | IllegalArgumentException ex) {
            out.println("Error: " + ex.getMessage());
        }
        out.println();
    }

    ShippingOption parseShippingChoice(String choice) {
        String normalized = choice.trim();
        if ("1".equals(normalized)) {
            return ShippingOption.STANDARD;
        }
        if ("2".equals(normalized)) {
            return ShippingOption.NEXT_DAY;
        }
        throw new IllegalArgumentException("Invalid shipping choice.");
    }

    int parseQuantity(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Quantity must be an integer of at least 1.");
        }
        String trimmed = input.trim();
        if (!trimmed.matches("-?\\d+")) {
            throw new IllegalArgumentException("Quantity must be an integer of at least 1.");
        }
        int quantity = Integer.parseInt(trimmed);
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be an integer of at least 1.");
        }
        return quantity;
    }
}
