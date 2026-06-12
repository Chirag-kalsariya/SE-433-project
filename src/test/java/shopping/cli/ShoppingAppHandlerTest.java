package shopping.cli;

import org.junit.jupiter.api.Test;
import shopping.cart.ShoppingCart;
import shopping.catalog.ItemCatalog;
import shopping.model.Customer;
import shopping.model.ShippingOption;
import shopping.service.OrderTotalCalculator;
import shopping.service.ShippingCalculator;
import shopping.service.TaxCalculator;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShoppingAppHandlerTest {

    @Test
    void parseQuantityRejectsBlankZeroAndNegativeValues() {
        ShoppingApp app = new ShoppingApp(new Scanner(System.in));
        assertThrows(IllegalArgumentException.class, () -> app.parseQuantity(""));
        assertThrows(IllegalArgumentException.class, () -> app.parseQuantity(null));
        assertThrows(IllegalArgumentException.class, () -> app.parseQuantity("0"));
        assertThrows(IllegalArgumentException.class, () -> app.parseQuantity("-3"));
        assertEquals(5, app.parseQuantity("5"));
    }

    @Test
    void getTotalShowsErrorWhenCustomerInformationMissing() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ShoppingCart cart = newCart();
        ShoppingApp app = new ShoppingApp(new Scanner(System.in), new PrintStream(output), cart, new ItemCatalog());

        app.handleGetTotal();

        assertTrue(output.toString(StandardCharsets.UTF_8).contains("Customer information is required."));
    }

    @Test
    void editAndRemoveOnEmptyCartShowMessage() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ShoppingCart cart = newCart();
        cart.setCustomer(new Customer("Alex", "TX"));
        cart.setShippingOption(ShippingOption.STANDARD);
        ShoppingApp app = new ShoppingApp(new Scanner(System.in), new PrintStream(output), cart, new ItemCatalog());

        app.handleEditQuantity();
        app.handleRemoveItem();

        String result = output.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("Your shopping cart is empty.\n\n"));
    }

    @Test
    void getTotalPrintsSubtotalAndTrailingBlankLine() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ShoppingCart cart = newCart();
        cart.setCustomer(new Customer("Alex", "IL"));
        cart.setShippingOption(ShippingOption.STANDARD);
        cart.addItem("Mouse", 2);
        ShoppingApp app = new ShoppingApp(new Scanner(System.in), new PrintStream(output), cart, new ItemCatalog());

        app.handleGetTotal();

        String result = output.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("Subtotal: $49.98"));
        assertTrue(result.contains("Current total (including tax and shipping): $62.98"));
        assertTrue(result.endsWith("\n\n"));
    }

    @Test
    void editQuantityPrintsCartAndPromptsBeforeUpdating() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ShoppingCart cart = newCart();
        cart.setCustomer(new Customer("Alex", "TX"));
        cart.setShippingOption(ShippingOption.STANDARD);
        cart.addItem("Mouse", 1);
        ShoppingApp app = new ShoppingApp(
                new Scanner("Mouse\n3\n"),
                new PrintStream(output),
                cart,
                new ItemCatalog());

        app.handleEditQuantity();

        String result = output.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("Shopping cart contents:"));
        assertTrue(result.contains("Enter item name to edit: "));
        assertTrue(result.contains("Enter new quantity: "));
        assertTrue(result.contains("Quantity updated."));
        assertTrue(result.endsWith("\n\n"));
        assertEquals(3, cart.getContents().get(0).getQuantity());
    }

    @Test
    void removeItemPrintsCartAndPromptBeforeRemoving() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ShoppingCart cart = newCart();
        cart.setCustomer(new Customer("Alex", "TX"));
        cart.setShippingOption(ShippingOption.STANDARD);
        cart.addItem("Mouse", 1);
        ShoppingApp app = new ShoppingApp(
                new Scanner("Mouse\n"),
                new PrintStream(output),
                cart,
                new ItemCatalog());

        app.handleRemoveItem();

        String result = output.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("Shopping cart contents:"));
        assertTrue(result.contains("Enter item name to remove: "));
        assertTrue(result.contains("Item removed."));
        assertTrue(result.endsWith("\n\n"));
        assertTrue(cart.isEmpty());
    }

    @Test
    void checkoutPrintsTransactionAndTrailingBlankLine() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ShoppingCart cart = newCart();
        cart.setCustomer(new Customer("Alex", "TX"));
        cart.setShippingOption(ShippingOption.STANDARD);
        cart.addItem("Notebook", 1);
        ShoppingApp app = new ShoppingApp(new Scanner(System.in), new PrintStream(output), cart, new ItemCatalog());

        app.handleCheckout();

        String result = output.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("transaction completed"));
        assertTrue(result.endsWith("\n\n"));
    }

    @Test
    void removeAndEditShowErrorsForUnknownItems() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ShoppingCart cart = newCart();
        cart.setCustomer(new Customer("Alex", "TX"));
        cart.setShippingOption(ShippingOption.STANDARD);
        cart.addItem("Mouse", 1);
        ShoppingApp app = new ShoppingApp(
                new Scanner("Unknown\n2\nUnknown\n"),
                new PrintStream(output),
                cart,
                new ItemCatalog());

        app.handleEditQuantity();
        app.handleRemoveItem();

        String result = output.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("Item not in cart: Unknown"));
    }

    @Test
    void checkoutShowsErrorForEmptyCart() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ShoppingCart cart = newCart();
        cart.setCustomer(new Customer("Alex", "TX"));
        cart.setShippingOption(ShippingOption.STANDARD);
        ShoppingApp app = new ShoppingApp(new Scanner(System.in), new PrintStream(output), cart, new ItemCatalog());

        app.handleCheckout();

        assertTrue(output.toString(StandardCharsets.UTF_8).contains("Cannot checkout with an empty cart."));
    }

    @Test
    void parseShippingChoiceAcceptsValidOptions() {
        ShoppingApp app = new ShoppingApp(new Scanner(System.in));
        assertEquals(ShippingOption.STANDARD, app.parseShippingChoice("1"));
        assertEquals(ShippingOption.NEXT_DAY, app.parseShippingChoice(" 2 "));
    }

    @Test
    void mainStartsApplicationWithProvidedInput() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        java.io.InputStream originalIn = System.in;
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            System.setIn(new java.io.ByteArrayInputStream("Alex\nIL\n1\n7\n".getBytes(StandardCharsets.UTF_8)));
            ShoppingApp.main(new String[]{});
            String result = output.toString(StandardCharsets.UTF_8);
            assertTrue(result.contains("=== Shopping Application ==="));
            assertTrue(result.contains("Thank you for shopping with us!"));
        } finally {
            System.setOut(originalOut);
            System.setIn(originalIn);
        }
    }

    private ShoppingCart newCart() {
        return new ShoppingCart(new ItemCatalog(),
                new OrderTotalCalculator(new TaxCalculator(), new ShippingCalculator()));
    }
}
