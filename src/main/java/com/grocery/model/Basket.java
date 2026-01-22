package com.grocery.model;

import java.util.*;

/**
 * Represents a shopping basket containing multiple items.
 * This class manages the items in the basket and provides utilities for manipulation.
 *
 * @author Grocery System
 * @version 1.0.0
 */
public class Basket {
    private final Map<Item, Integer> items;

    /**
     * Creates a new empty basket.
     */
    public Basket() {
        this.items = new LinkedHashMap<>();
    }

    /**
     * Adds an item to the basket with the specified quantity.
     * If the item already exists, the quantities are combined.
     *
     * @param item the item to add (must not be null)
     * @param quantity the quantity to add (must be positive)
     * @throws NullPointerException if item is null
     * @throws IllegalArgumentException if quantity is not positive
     */
    public void addItem(Item item, int quantity) {
        Objects.requireNonNull(item, "Item cannot be null");

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive: " + quantity);
        }

        items.put(item, items.getOrDefault(item, 0) + quantity);
    }

    /**
     * Removes an item from the basket entirely.
     *
     * @param item the item to remove
     */
    public void removeItem(Item item) {
        items.remove(item);
    }

    /**
     * Clears all items from the basket.
     */
    public void clear() {
        items.clear();
    }

    /**
     * Gets all items in the basket.
     *
     * @return an unmodifiable list of basket items
     */
    public List<BasketItem> getItems() {
        return items.entrySet().stream()
                .map(entry -> new BasketItem(entry.getKey(), entry.getValue()))
                .toList();
    }

    /**
     * Gets the quantity of a specific item in the basket.
     *
     * @param item the item to query
     * @return the quantity, or 0 if not in basket
     */
    public int getQuantity(Item item) {
        return items.getOrDefault(item, 0);
    }

    /**
     * Checks if the basket is empty.
     *
     * @return true if the basket contains no items
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Gets the total number of items in the basket.
     *
     * @return the total quantity of all items
     */
    public int getTotalQuantity() {
        return items.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Gets the number of unique items in the basket.
     *
     * @return the count of unique items
     */
    public int getUniqueItemCount() {
        return items.size();
    }

    @Override
    public String toString() {
        return "Basket{" +
                "items=" + items +
                '}';
    }
}
