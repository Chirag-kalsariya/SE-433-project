package shopping.cli;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ShoppingAppTest {

    @Test
    void fullSessionPrintsAllExpectedPromptsAndMessages() {
        String input = "Pat\nNY\n2\n1\nKeyboard\n1\n2\n3\n6\n";
        String result = runApp(input);

        assertTrue(result.contains("=== Shopping Application ==="));
        assertTrue(result.contains("Enter your name:"));
        assertTrue(result.contains("Enter your state of residence"));
        assertTrue(result.contains("Select a shipping option:"));
        assertTrue(result.contains("1. Standard"));
        assertTrue(result.contains("2. Next day"));
        assertTrue(result.contains("--- Menu ---"));
        assertTrue(result.contains("1. Add item to shopping cart"));
        assertTrue(result.contains("2. Get current total"));
        assertTrue(result.contains("3. See contents of shopping cart"));
        assertTrue(result.contains("4. Edit quantity of items in shopping cart"));
        assertTrue(result.contains("5. Remove items from shopping cart"));
        assertTrue(result.contains("6. Checkout"));
        assertTrue(result.contains("7. Exit"));
        assertTrue(result.contains("Available items:"));
        assertTrue(result.contains("Keyboard ($49.99)"));
        assertTrue(result.contains("Thank you for shopping with us!"));
    }

    @Test
    void acceptanceFlowAddsItemShowsTotalAndChecksOut() {
        String input = String.join("\n",
                "Alex",
                "IL",
                "1",
                "1",
                "Mouse",
                "2",
                "2",
                "3",
                "6",
                "7"
        ) + "\n";

        String result = runApp(input);
        assertTrue(result.contains("Welcome, Alex!"));
        assertTrue(result.contains("Current count of items in cart: 2"));
        assertTrue(result.contains("Current total (including tax and shipping)"));
        assertTrue(result.contains("Shopping cart contents:"));
        assertTrue(result.contains("transaction completed"));
        assertTrue(result.contains("$62.98"));
        assertTrue(result.contains("Shipping option set to: Standard"));
    }

    @Test
    void systemFlowHandlesInvalidMenuQuantityAndCartActions() {
        String input = String.join("\n",
                "Sam",
                "TX",
                "1",
                "2",
                "9",
                "1",
                "Notebook",
                "abc",
                "1",
                "Notebook",
                "2",
                "4",
                "Notebook",
                "1",
                "5",
                "Notebook",
                "3",
                "7"
        ) + "\n";

        String result = runApp(input);
        assertTrue(result.contains("Invalid option. Please choose 1-7."));
        assertTrue(result.contains("Quantity must be an integer of at least 1."));
        assertTrue(result.contains("Quantity updated."));
        assertTrue(result.contains("Item removed."));
        assertTrue(result.contains("Your shopping cart is empty."));
    }

    @Test
    void cliPrintsAllPromptsBlankLinesAndCartDetails() {
        String input = String.join("\n",
                "Alex",
                "IL",
                "1",
                "1",
                "Mouse",
                "1",
                "2",
                "3",
                "4",
                "Mouse",
                "2",
                "6",
                "7"
        ) + "\n";

        String result = runApp(input);
        assertTrue(result.contains("=== Shopping Application ===\n\n"));
        assertTrue(result.contains("Welcome, Alex!\n\n"));
        assertTrue(result.contains("Enter choice (1 or 2): "));
        assertTrue(result.contains("Shipping option set to: Standard\n\n"));
        assertTrue(result.contains("Select an action: "));
        assertTrue(result.contains("Enter item name: "));
        assertTrue(result.contains("Enter quantity: "));
        assertTrue(result.contains("Item added. Current count of items in cart: 1\n\n"));
        assertTrue(result.contains("Subtotal: $24.99"));
        assertTrue(result.contains("Current total (including tax and shipping): $36.49\n\n"));
        assertTrue(result.contains("  - Mouse | Qty: 1 | Line total: $24.99\n\n"));
        assertTrue(result.contains("Enter item name to edit: "));
        assertTrue(result.contains("Enter new quantity: "));
        assertTrue(result.contains("transaction completed\n\n"));
    }

    @Test
    void integrationFlowRetriesInvalidCustomerAndShippingInput() {
        String input = String.join("\n",
                " ",
                "Alex",
                " ",
                "CA",
                "9",
                "2",
                "7"
        ) + "\n";

        String result = runApp(input);
        assertTrue(result.contains("Customer name is required."));
        assertTrue(result.contains("State of residence is required."));
        assertTrue(result.contains("Invalid shipping choice."));
        assertTrue(result.contains("Shipping option set to: Next day"));
    }

    private String runApp(String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(output, true, StandardCharsets.UTF_8);
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        ShoppingApp app = new ShoppingApp(scanner, printStream, new shopping.catalog.ItemCatalog(),
                new shopping.service.OrderTotalCalculator(
                        new shopping.service.TaxCalculator(),
                        new shopping.service.ShippingCalculator()));
        app.run();
        return output.toString(StandardCharsets.UTF_8);
    }
}
