package shopping.cart;

import shopping.catalog.ItemCatalog;
import shopping.model.CartItem;
import shopping.model.Customer;
import shopping.model.Item;
import shopping.model.ShippingOption;
import shopping.service.OrderTotalCalculator;
import shopping.validation.OrderValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ShoppingCart {
    private final ItemCatalog catalog;
    private final OrderTotalCalculator totalCalculator;
    private Customer customer;
    private ShippingOption shippingOption;
    private final Map<String, CartItem> items = new LinkedHashMap<>();

    public ShoppingCart(ItemCatalog catalog, OrderTotalCalculator totalCalculator) {
        this.catalog = catalog;
        this.totalCalculator = totalCalculator;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setShippingOption(ShippingOption shippingOption) {
        this.shippingOption = shippingOption;
    }

    public Optional<Customer> getCustomer() {
        return Optional.ofNullable(customer);
    }

    public Optional<ShippingOption> getShippingOption() {
        return Optional.ofNullable(shippingOption);
    }

    public int addItem(String itemName, int quantity) {
        OrderValidator.validateQuantity(quantity);
        Item item = catalog.findByName(itemName)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemName));

        CartItem existing = items.get(item.getName().toLowerCase());
        int newQuantity = quantity;
        if (existing != null) {
            newQuantity = existing.getQuantity() + quantity;
        }

        validateOrderTotalWithQuantity(item, newQuantity);
        items.put(item.getName().toLowerCase(), new CartItem(item, newQuantity));
        return getTotalItemCount();
    }

    public int getTotalItemCount() {
        return items.values().stream().mapToInt(CartItem::getQuantity).sum();
    }

    public List<CartItem> getContents() {
        return Collections.unmodifiableList(new ArrayList<>(items.values()));
    }

    public void editQuantity(String itemName, int quantity) {
        OrderValidator.validateQuantity(quantity);
        CartItem cartItem = findCartItem(itemName)
                .orElseThrow(() -> new IllegalArgumentException("Item not in cart: " + itemName));

        validateOrderTotalWithQuantity(cartItem.getItem(), quantity);
        cartItem.setQuantity(quantity);
    }

    public void removeItem(String itemName) {
        CartItem removed = items.remove(itemName.trim().toLowerCase());
        if (removed == null) {
            throw new IllegalArgumentException("Item not in cart: " + itemName);
        }
    }

    public double getSubtotal() {
        return items.values().stream().mapToDouble(CartItem::getLineTotal).sum();
    }

    public double getCurrentTotal() {
        ensureCheckoutReady();
        return totalCalculator.calculateTotal(customer.getState(), shippingOption, getSubtotal());
    }

    public void checkout() {
        ensureCheckoutReady();
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot checkout with an empty cart.");
        }
        double total = getCurrentTotal();
        OrderValidator.validatePurchaseAmount(total);
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    private void ensureCheckoutReady() {
        if (customer == null) {
            throw new IllegalStateException("Customer information is required.");
        }
        if (shippingOption == null) {
            throw new IllegalStateException("Shipping option is required.");
        }
    }

    private void validateOrderTotalWithQuantity(Item item, int quantity) {
        ensureCheckoutReady();
        double projectedSubtotal = getSubtotal();
        CartItem existing = items.get(item.getName().toLowerCase());
        if (existing != null) {
            projectedSubtotal -= existing.getLineTotal();
        }
        projectedSubtotal += item.getPrice() * quantity;
        double projectedTotal = totalCalculator.calculateTotal(
                customer.getState(), shippingOption, projectedSubtotal);
        OrderValidator.validatePurchaseAmount(projectedTotal);
    }

    private Optional<CartItem> findCartItem(String itemName) {
        if (itemName == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(items.get(itemName.trim().toLowerCase()));
    }
}
