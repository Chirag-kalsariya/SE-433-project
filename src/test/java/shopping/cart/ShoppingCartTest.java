package shopping.cart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import shopping.catalog.ItemCatalog;
import shopping.model.Customer;
import shopping.model.ShippingOption;
import shopping.service.OrderTotalCalculator;
import shopping.service.ShippingCalculator;
import shopping.service.TaxCalculator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShoppingCartTest {

    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        ItemCatalog catalog = new ItemCatalog();
        OrderTotalCalculator totalCalculator =
                new OrderTotalCalculator(new TaxCalculator(), new ShippingCalculator());
        cart = new ShoppingCart(catalog, totalCalculator);
        cart.setCustomer(new Customer("Alex", "IL"));
        cart.setShippingOption(ShippingOption.STANDARD);
    }

    @Test
    void addItemIncreasesTotalItemCountAndSupportsDuplicates() {
        int firstAdd = cart.addItem("Mouse", 2);
        int secondAdd = cart.addItem("mouse", 1);

        assertEquals(2, firstAdd);
        assertEquals(3, secondAdd);
        assertEquals(1, cart.getContents().size());
    }

    @Test
    void addItemRejectsInvalidQuantityAndUnknownItem() {
        assertThrows(IllegalArgumentException.class, () -> cart.addItem("Mouse", 0));
        assertThrows(IllegalArgumentException.class, () -> cart.addItem("Unknown", 1));
    }

    @Test
    void editQuantityUpdatesItemAndRejectsInvalidValues() {
        cart.addItem("Mouse", 1);
        cart.editQuantity("Mouse", 4);
        assertEquals(4, cart.getContents().get(0).getQuantity());

        assertThrows(IllegalArgumentException.class, () -> cart.editQuantity("Mouse", 0));
        assertThrows(IllegalArgumentException.class, () -> cart.editQuantity("Unknown", 2));
    }

    @Test
    void removeItemDeletesFromCart() {
        cart.addItem("Mouse", 1);
        cart.removeItem("Mouse");
        assertTrue(cart.isEmpty());

        assertThrows(IllegalArgumentException.class, () -> cart.removeItem("Mouse"));
    }

    @Test
    void getCurrentTotalIncludesTaxAndShipping() {
        cart.addItem("Mouse", 2);
        assertEquals(49.98, cart.getSubtotal());
        assertEquals(62.98, cart.getCurrentTotal());
    }

    @Test
    void checkoutClearsCartAfterValidation() {
        cart.addItem("Mouse", 1);
        cart.checkout();
        assertTrue(cart.isEmpty());
    }

    @Test
    void checkoutRequiresCustomerShippingAndItems() {
        ShoppingCart emptySetupCart = new ShoppingCart(new ItemCatalog(),
                new OrderTotalCalculator(new TaxCalculator(), new ShippingCalculator()));

        IllegalStateException missingCustomer = assertThrows(
                IllegalStateException.class, emptySetupCart::checkout);
        assertEquals("Customer information is required.", missingCustomer.getMessage());

        emptySetupCart.setCustomer(new Customer("Alex", "TX"));
        emptySetupCart.setShippingOption(ShippingOption.NEXT_DAY);
        IllegalStateException emptyCart = assertThrows(
                IllegalStateException.class, emptySetupCart::checkout);
        assertEquals("Cannot checkout with an empty cart.", emptyCart.getMessage());
    }

    @Test
    void checkoutValidatesPurchaseAmountBeforeClearingCart() {
        OrderTotalCalculator calculator = new OrderTotalCalculator(
                new TaxCalculator(), new ShippingCalculator()) {
            private int calls;

            @Override
            public double calculateTotal(String state, ShippingOption option, double subtotal) {
                calls++;
                if (calls == 2) {
                    return 0.50;
                }
                return super.calculateTotal(state, option, subtotal);
            }
        };
        ShoppingCart validatingCart = new ShoppingCart(new ItemCatalog(), calculator);
        validatingCart.setCustomer(new Customer("Alex", "TX"));
        validatingCart.setShippingOption(ShippingOption.STANDARD);
        validatingCart.addItem("Notebook", 1);

        assertThrows(IllegalArgumentException.class, validatingCart::checkout);
        assertFalse(validatingCart.isEmpty());
    }

    @Test
    void editQuantityAllowsMaximumPurchaseAtBoundary() {
        cart.setCustomer(new Customer("Alex", "TX"));
        cart.setShippingOption(ShippingOption.STANDARD);
        cart.addItem("Laptop", 99);
        cart.editQuantity("Laptop", 100);
        assertEquals(100, cart.getContents().get(0).getQuantity());
        assertEquals(99999.00, cart.getSubtotal(), 0.01);
    }

    @Test
    void rejectsPurchaseAboveMaximumAmount() {
        cart.setCustomer(new Customer("Alex", "TX"));
        cart.setShippingOption(ShippingOption.STANDARD);
        assertThrows(IllegalArgumentException.class, () -> cart.addItem("Mouse", 4002));
    }

    @Test
    void requiresCustomerAndShippingBeforeAddingItems() {
        ShoppingCart newCart = new ShoppingCart(new ItemCatalog(),
                new OrderTotalCalculator(new TaxCalculator(), new ShippingCalculator()));

        assertThrows(IllegalStateException.class, () -> newCart.addItem("Mouse", 1));
        assertFalse(newCart.getCustomer().isPresent());
        assertFalse(newCart.getShippingOption().isPresent());
    }

    @Test
    void requiresShippingOptionBeforeCalculatingTotal() {
        ShoppingCart partialCart = new ShoppingCart(new ItemCatalog(),
                new OrderTotalCalculator(new TaxCalculator(), new ShippingCalculator()));
        partialCart.setCustomer(new Customer("Alex", "TX"));

        assertThrows(IllegalStateException.class, partialCart::getCurrentTotal);
    }

    @Test
    void editQuantityRejectsNullItemName() {
        cart.addItem("Mouse", 1);
        assertThrows(IllegalArgumentException.class, () -> cart.editQuantity(null, 2));
    }

    @Test
    void getCustomerAndShippingOptionReturnValuesWhenSet() {
        assertTrue(cart.getCustomer().isPresent());
        assertTrue(cart.getShippingOption().isPresent());
        assertEquals("Alex", cart.getCustomer().orElseThrow().getName());
        assertEquals(ShippingOption.STANDARD, cart.getShippingOption().orElseThrow());
    }

    @Test
    void getContentsReturnsUnmodifiableList() {
        cart.addItem("Mouse", 1);
        List<shopping.model.CartItem> contents = cart.getContents();
        assertThrows(UnsupportedOperationException.class,
                () -> contents.add(new shopping.model.CartItem(
                        new shopping.model.Item("Keyboard", 1.0), 1)));
    }

    @Test
    void removeItemTrimsWhitespaceFromName() {
        cart.addItem("Mouse", 1);
        cart.removeItem("  Mouse  ");
        assertTrue(cart.isEmpty());
    }

    @Test
    void editQuantityRejectsIncreaseAboveMaximumPurchase() {
        cart.setCustomer(new Customer("Alex", "TX"));
        cart.addItem("Mouse", 1);
        assertThrows(IllegalArgumentException.class, () -> cart.editQuantity("Mouse", 4002));
    }

    @Test
    void checkoutRequiresShippingOption() {
        cart.addItem("Mouse", 1);
        cart.setShippingOption(null);
        assertThrows(IllegalStateException.class, () -> cart.checkout());
    }

    @Test
    void editQuantityRevalidatesTotalWhenReplacingExistingLine() {
        cart.setCustomer(new Customer("Alex", "IL"));
        cart.addItem("Mouse", 2);
        cart.editQuantity("Mouse", 1);
        assertEquals(1, cart.getContents().get(0).getQuantity());
    }

    @Test
    void findCartItemTrimsWhitespace() {
        cart.addItem("Mouse", 1);
        cart.editQuantity("  mouse  ", 2);
        assertEquals(2, cart.getContents().get(0).getQuantity());
    }
}
