package shopping.model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ItemTest {

    @Test
    void equalsAndHashCodeUseNameAndPrice() {
        Item first = new Item("Mouse", 10.00);
        Item same = new Item("Mouse", 10.00);
        Item differentName = new Item("Keyboard", 10.00);
        Item differentPrice = new Item("Mouse", 11.00);

        assertEquals(first, same);
        assertEquals(first, first);
        assertEquals(first.hashCode(), same.hashCode());
        assertNotEquals(first, differentName);
        assertNotEquals(first, differentPrice);
        assertNotEquals(first, null);
        assertNotEquals(first, "Mouse");

        Map<Item, String> byItem = new HashMap<>();
        byItem.put(first, "found");
        assertEquals("found", byItem.get(same));
        assertEquals("found", byItem.get(first));
        assertNotEquals(first.hashCode(), differentName.hashCode());
    }
}
