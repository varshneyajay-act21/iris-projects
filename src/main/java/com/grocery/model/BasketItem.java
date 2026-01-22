package com.grocery.model;

import java.util.Objects;

/**
 * Represents a line item in the basket with an item and its quantity.
 * This class is immutable to ensure thread safety.
 *
 * @author Grocery System
 * @version 1.0.0
 */
public class BasketItem {
    private final Item item;
    private final int quantity;

    /**
     * Creates a new BasketItem with the specified item and quantity.
     *
     * @param item the item (must not be null)
     * @param quantity the quantity (must be positive)
     * @throws NullPointerException if item is null
     * @throws IllegalArgumentException if quantity is not positive
     */
    public BasketItem(Item item, int quantity) {
        Objects.requireNonNull(item, "Item cannot be null");

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive: " + quantity);
        }

        this.item = item;
        this.quantity = quantity;
    }

    /**
     * Gets the item.
     *
     * @return the item
     */
    public Item getItem() {
        return item;
    }

    /**
     * Gets the quantity.
     *
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasketItem that = (BasketItem) o;
        return quantity == that.quantity && Objects.equals(item, that.item);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, quantity);
    }

    @Override
    public String toString() {
        return "BasketItem{" +
                "item=" + item +
                ", quantity=" + quantity +
                '}';
    }
}
