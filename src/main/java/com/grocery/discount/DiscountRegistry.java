package com.grocery.discount;

import java.util.*;

/**
 * Registry for managing all available discounts.
 * This registry maintains a centralized list of active discounts that can be applied to items.
 *
 * @author Grocery System
 * @version 1.0.0
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
    public void registerDiscount(Discount discount) {
        Objects.requireNonNull(discount, "Discount cannot be null");
        discounts.add(discount);
    }

    /**
     * Gets all registered discounts.
     *
     * @return an unmodifiable list of all discounts
     */
    public List<Discount> getDiscounts() {
        return Collections.unmodifiableList(discounts);
    }

    /**
     * Clears all registered discounts.
     */
    public void clear() {
        discounts.clear();
    }

    /**
     * Gets the number of registered discounts.
     *
     * @return the count of discounts
     */
    public int size() {
        return discounts.size();
    }
}
