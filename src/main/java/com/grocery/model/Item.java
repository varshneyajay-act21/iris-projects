package com.grocery.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a grocery item with its name and unit price.
 * This class is immutable to ensure thread safety and prevent accidental modifications.
 *
 * @author Grocery System
 * @version 1.0.0
 */
public class Item {
    private final String name;
    private final BigDecimal pricePerUnit;

    /**
     * Creates a new Item with the specified name and price.
     *
     * @param name the item name (must not be null or empty)
     * @param pricePerUnit the unit price (must not be null or negative)
     * @throws IllegalArgumentException if name is null/empty or price is invalid
     */
    public Item(String name, BigDecimal pricePerUnit) {
        Objects.requireNonNull(name, "Item name cannot be null");
        Objects.requireNonNull(pricePerUnit, "Price cannot be null");

        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be empty");
        }
        if (pricePerUnit.signum() < 0) {
            throw new IllegalArgumentException("Price cannot be negative: " + pricePerUnit);
        }

        this.name = name.trim();
        this.pricePerUnit = pricePerUnit;
    }

    /**
     * Gets the item name.
     *
     * @return the item name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the price per unit.
     *
     * @return the unit price
     */
    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(name, item.name) &&
               Objects.equals(pricePerUnit, item.pricePerUnit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, pricePerUnit);
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", pricePerUnit=" + pricePerUnit +
                '}';
    }
}
