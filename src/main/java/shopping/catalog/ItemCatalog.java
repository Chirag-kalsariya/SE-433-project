package shopping.catalog;

import shopping.model.Item;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ItemCatalog {
    private final Map<String, Item> itemsByName = new LinkedHashMap<>();

    public ItemCatalog() {
        addItem(new Item("Laptop", 999.99));
        addItem(new Item("Mouse", 24.99));
        addItem(new Item("Keyboard", 49.99));
        addItem(new Item("USB Cable", 9.99));
        addItem(new Item("Monitor", 199.99));
        addItem(new Item("Notebook", 4.99));
    }

    public void addItem(Item item) {
        itemsByName.put(item.getName().toLowerCase(), item);
    }

    public Optional<Item> findByName(String name) {
        if (name == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(itemsByName.get(name.trim().toLowerCase()));
    }

    public Collection<Item> getAllItems() {
        return itemsByName.values();
    }
}
