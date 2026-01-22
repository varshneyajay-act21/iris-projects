package com.grocery.catalog;

import com.grocery.model.Item;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Catalog of available grocery items with their prices.
 * Provides a centralized place to manage item definitions and prices.
 * This catalog is thread-safe and immutable after initialization.
 *
 * @author Grocery System
 * @version 1.0.0
 */
public class ItemCatalog {
    // Predefined items
    public static final Item BANANAS = new Item("Bananas", new BigDecimal("0.50"));
    public static final Item ORANGES = new Item("Oranges", new BigDecimal("0.30"));
    public static final Item APPLES = new Item("Apples", new BigDecimal("0.60"));
    public static final Item LEMONS = new Item("Lemons", new BigDecimal("0.25"));
    public static final Item PEACHES = new Item("Peaches", new BigDecimal("0.75"));

    private static final Map<String, Item> CATALOG;

    static {
        Map<String, Item> catalog = new HashMap<>();
        catalog.put(BANANAS.getName().toLowerCase(), BANANAS);
        catalog.put(ORANGES.getName().toLowerCase(), ORANGES);
        catalog.put(APPLES.getName().toLowerCase(), APPLES);
        catalog.put(LEMONS.getName().toLowerCase(), LEMONS);
        catalog.put(PEACHES.getName().toLowerCase(), PEACHES);
        CATALOG = Collections.unmodifiableMap(catalog);
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private ItemCatalog() {
        throw new AssertionError("Cannot instantiate ItemCatalog");
    }

    /**
     * Retrieves an item by name.
     *
     * @param name the item name (case-insensitive)
     * @return the Item, or null if not found
     */
    public static Item getItem(String name) {
        Objects.requireNonNull(name, "Item name cannot be null");
        return CATALOG.get(name.toLowerCase());
    }

    /**
     * Gets all available items in the catalog.
     *
     * @return an unmodifiable collection of all items
     */
    public static Map<String, Item> getAllItems() {
        return CATALOG;
    }

    /**
     * Checks if an item exists in the catalog.
     *
     * @param name the item name (case-insensitive)
     * @return true if the item exists
     */
    public static boolean containsItem(String name) {
        Objects.requireNonNull(name, "Item name cannot be null");
        return CATALOG.containsKey(name.toLowerCase());
    }
}
