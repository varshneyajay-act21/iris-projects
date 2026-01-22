package com.grocery.discount;

import com.grocery.model.Item;
import java.math.BigDecimal;

/**
 * Abstract base class for discount strategies.
 * Implementations should provide specific discount calculation logic.
 *
 * @author Grocery System
 * @version 1.0.0
 */
public abstract class Discount {

    /**
     * Gets the item this discount applies to.
     *
     * @return the item
     */
    public abstract Item getItem();

    /**
     * Calculates the discount for the given quantity of items.
     *
     * @param quantity the quantity of items
     * @param unitPrice the unit price of the item
     * @return the discount result, which may indicate no discount if conditions aren't met
     */
    public abstract DiscountResult calculateDiscount(int quantity, BigDecimal unitPrice);

    /**
     * Gets a human-readable name for this discount.
     *
     * @return the discount name
     */
    public abstract String getDiscountName();
}
