package com.grocery.discount;

import java.util.*;

/**
 * Registry for managing all available discounts.
 * This registry maintains a centralized list of active discounts that can be applied to items.
 * It supports runtime modification to add or remove discounts (admin operations).
 *
 * @author Grocery System
 * @version 1.0.1
 */
public class DiscountRegistry {
    private final List<Discount> discounts;

    /**
     * Creates a new DiscountRegistry.
     */
    public DiscountRegistry() {
        this.discounts = new ArrayList<>();
    }

    /**
     * Registers a discount in the registry.
     *
     * @param discount the discount to register (must not be null)
     * @throws NullPointerException if discount is null
     */
    public synchronized void registerDiscount(Discount discount) {
        Objects.requireNonNull(discount, "Discount cannot be null");
        discounts.add(discount);
    }

    /**
     * Unregisters (removes) a discount from the registry.
     *
     * @param discount the discount to remove
     * @return true if removed, false otherwise
     */
    public synchronized boolean unregisterDiscount(Discount discount) {
        return discounts.remove(discount);
    }

    /**
     * Unregisters discounts that apply to the given item name (case-insensitive).
     *
     * @param itemName the item name
     * @return number of removed discounts
     */
    public synchronized int unregisterDiscountsForItem(String itemName) {
        if (itemName == null) return 0;
        int before = discounts.size();
        discounts.removeIf(d -> d.getItem().getName().equalsIgnoreCase(itemName));
        return before - discounts.size();
    }

    /**
     * Finds discounts by their human-readable description (contains, case-insensitive).
     *
     * @param description part or all of the description
     * @return list of matching discounts
     */
    public synchronized List<Discount> findDiscountsByDescription(String description) {
        if (description == null) return Collections.emptyList();
        String lower = description.toLowerCase();
        List<Discount> results = new ArrayList<>();
        for (Discount d : discounts) {
            if (d.getDiscountName().toLowerCase().contains(lower)) {
                results.add(d);
            }
        }
        return results;
    }

    /**
     * Gets all registered discounts.
     *
     * @return an unmodifiable list of all discounts
     */
    public synchronized List<Discount> getDiscounts() {
        return Collections.unmodifiableList(new ArrayList<>(discounts));
    }

    /**
     * Clears all registered discounts.
     */
    public synchronized void clear() {
        discounts.clear();
    }

    /**
     * Gets the number of registered discounts.
     *
     * @return the count of discounts
     */
    public synchronized int size() {
        return discounts.size();
    }
}
