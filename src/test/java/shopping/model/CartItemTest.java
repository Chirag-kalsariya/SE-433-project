package shopping.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CartItemTest {

    @Test
    void calculatesLineTotalAndAllowsQuantityUpdate() {
        Item item = new Item("Mouse", 10.00);
        CartItem cartItem = new CartItem(item, 2);

        assertEquals("Mouse", cartItem.getName());
        assertEquals(20.00, cartItem.getLineTotal());

        cartItem.setQuantity(3);
        assertEquals(30.00, cartItem.getLineTotal());
    }
}
