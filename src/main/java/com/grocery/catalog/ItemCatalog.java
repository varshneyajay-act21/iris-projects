package com.grocery.catalog;

import com.grocery.model.Item;
import com.grocery.exception.CatalogException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Catalog of available grocery items with their prices.
 * Provides a centralized place to manage item definitions and prices.
 * This catalog is thread-safe and supports runtime modification via admin operations.
 *
 * @author Grocery System
 * @version 1.0.1
 */
public class ItemCatalog {
    // Predefined items
    public static final Item BANANAS = new Item("Bananas", new BigDecimal("0.50"));
    public static final Item ORANGES = new Item("Oranges", new BigDecimal("0.30"));
    public static final Item APPLES = new Item("Apples", new BigDecimal("0.60"));
    public static final Item LEMONS = new Item("Lemons", new BigDecimal("0.25"));
    public static final Item PEACHES = new Item("Peaches", new BigDecimal("0.75"));

    // Backing map is concurrent for thread-safety and atomic operations
    private static final Map<String, Item> CATALOG = new ConcurrentHashMap<>();

    static {
        CATALOG.put(BANANAS.getName().toLowerCase(), BANANAS);
        CATALOG.put(ORANGES.getName().toLowerCase(), ORANGES);
        CATALOG.put(APPLES.getName().toLowerCase(), APPLES);
        CATALOG.put(LEMONS.getName().toLowerCase(), LEMONS);
        CATALOG.put(PEACHES.getName().toLowerCase(), PEACHES);
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
     * @return the Item
     * @throws CatalogException if name is null or item not found
     */
    public static Item getItem(String name) {
        if (name == null) {
            throw new CatalogException("Item name cannot be null");
        }

        Item item = CATALOG.get(name.toLowerCase());
        if (item == null) {
            throw new CatalogException("Item not found in catalog: " + name);
        }
        return item;
    }

    /**
     * Gets all available items in the catalog.
     *
     * @return an unmodifiable snapshot of all items
     */
    public static Map<String, Item> getAllItems() {
        return Collections.unmodifiableMap(new java.util.HashMap<>(CATALOG));
    }

    /**
     * Checks if an item exists in the catalog.
     *
     * @param name the item name (case-insensitive)
     * @return true if the item exists
     * @throws CatalogException if name is null
     */
    public static boolean containsItem(String name) {
        if (name == null) {
            throw new CatalogException("Item name cannot be null");
        }
        return CATALOG.containsKey(name.toLowerCase());
    }

    /**
     * Adds a new item to the catalog. Uses atomic putIfAbsent to avoid race conditions.
     *
     * @param item the item to add (must not be null and name must not already exist)
     * @throws CatalogException if item is null or already exists
     */
    public static void addItem(Item item) {
        Objects.requireNonNull(item, "Item cannot be null");
        String key = item.getName().toLowerCase();
        Item existing = CATALOG.putIfAbsent(key, item);
        if (existing != null) {
            throw new CatalogException("Item already exists in catalog: " + item.getName());
        }
    }

    /**
     * Removes an item from the catalog by name. Returns the removed Item.
     *
     * @param name the item name to remove (case-insensitive)
     * @return the removed Item
     * @throws CatalogException if name is null or item not found
     */
    public static Item removeItem(String name) {
        if (name == null) {
            throw new CatalogException("Item name cannot be null");
        }
        Item removed = CATALOG.remove(name.toLowerCase());
        if (removed == null) {
            throw new CatalogException("Item not found in catalog: " + name);
        }
        return removed;
    }
}
