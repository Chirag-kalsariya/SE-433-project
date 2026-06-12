package shopping.catalog;

import org.junit.jupiter.api.Test;
import shopping.model.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemCatalogTest {

    @Test
    void findsItemsCaseInsensitively() {
        ItemCatalog catalog = new ItemCatalog();
        assertTrue(catalog.findByName("laptop").isPresent());
        assertTrue(catalog.findByName("LAPTOP").isPresent());
    }

    @Test
    void returnsEmptyForUnknownOrNullItem() {
        ItemCatalog catalog = new ItemCatalog();
        assertFalse(catalog.findByName("unknown").isPresent());
        assertFalse(catalog.findByName(null).isPresent());
    }

    @Test
    void supportsAddingCustomItems() {
        ItemCatalog catalog = new ItemCatalog();
        catalog.addItem(new Item("Custom", 1.00));
        assertTrue(catalog.findByName("custom").isPresent());
        assertFalse(catalog.getAllItems().isEmpty());
    }

    @Test
    void defaultCatalogContainsSixItems() {
        ItemCatalog catalog = new ItemCatalog();
        assertEquals(6, catalog.getAllItems().size());
        assertTrue(catalog.findByName("Laptop").isPresent());
        assertTrue(catalog.findByName("Monitor").isPresent());
    }
}
