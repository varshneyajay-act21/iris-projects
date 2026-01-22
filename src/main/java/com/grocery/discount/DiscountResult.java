package com.grocery.discount;

import com.grocery.model.Item;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents the result of applying a discount to items in a basket.
 * Contains information about the discount amount and the item(s) it applies to.
 *
 * @author Grocery System
 * @version 1.0.0
 */
public class DiscountResult {
    private final Item item;
    private final String description;
    private final BigDecimal discountAmount;
    private final int itemsApplied;

    /**
     * Creates a new DiscountResult.
     *
     * @param item the item this discount applies to
     * @param description a human-readable description of the discount
     * @param discountAmount the amount of discount (must be non-negative)
     * @param itemsApplied the number of items this discount applies to
     */
    public DiscountResult(Item item, String description, BigDecimal discountAmount, int itemsApplied) {
        this.item = Objects.requireNonNull(item, "Item cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.discountAmount = Objects.requireNonNull(discountAmount, "Discount amount cannot be null");

        if (discountAmount.signum() < 0) {
            throw new IllegalArgumentException("Discount amount cannot be negative");
        }
        if (itemsApplied < 0) {
            throw new IllegalArgumentException("Items applied cannot be negative");
        }

        this.itemsApplied = itemsApplied;
    }

    /**
     * Gets the item this discount applies to.
     *
     * @return the item
     */
    public Item getItem() {
        return item;
    }

    /**
     * Gets the discount description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the discount amount.
     *
     * @return the discount amount
     */
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    /**
     * Gets the number of items this discount applies to.
     *
     * @return the count of items
     */
    public int getItemsApplied() {
        return itemsApplied;
    }

    /**
     * Checks if this discount actually applies (amount > 0).
     *
     * @return true if discountAmount is greater than 0
     */
    public boolean isApplicable() {
        return discountAmount.signum() > 0;
    }

    @Override
    public String toString() {
        return "DiscountResult{" +
                "item=" + item.getName() +
                ", description='" + description + '\'' +
                ", discountAmount=" + discountAmount +
                ", itemsApplied=" + itemsApplied +
                '}';
    }
}
