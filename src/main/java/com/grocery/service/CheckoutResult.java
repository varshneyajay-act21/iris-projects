package com.grocery.service;

import com.grocery.discount.DiscountResult;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the result of a checkout operation.
 * Contains the subtotal, applied discounts, and final total.
 *
 * @author Grocery System
 * @version 1.0.0
 */
public class CheckoutResult {
    private final BigDecimal subtotal;
    private final List<DiscountResult> discounts;
    private final BigDecimal total;

    /**
     * Creates a new CheckoutResult.
     *
     * @param subtotal the subtotal before discounts (must not be null)
     * @param discounts the list of applied discounts (must not be null)
     * @param total the final total (must not be null)
     */
    public CheckoutResult(BigDecimal subtotal, List<DiscountResult> discounts, BigDecimal total) {
        this.subtotal = Objects.requireNonNull(subtotal, "Subtotal cannot be null");
        this.discounts = Collections.unmodifiableList(
                Objects.requireNonNull(discounts, "Discounts list cannot be null")
        );
        this.total = Objects.requireNonNull(total, "Total cannot be null");
    }

    /**
     * Gets the subtotal before discounts.
     *
     * @return the subtotal
     */
    public BigDecimal getSubtotal() {
        return subtotal;
    }

    /**
     * Gets the list of applied discounts.
     *
     * @return an unmodifiable list of discount results
     */
    public List<DiscountResult> getDiscounts() {
        return discounts;
    }

    /**
     * Gets the total discount amount.
     *
     * @return the sum of all discounts
     */
    public BigDecimal getTotalDiscountAmount() {
        return discounts.stream()
                .map(DiscountResult::getDiscountAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Gets the final total after discounts.
     *
     * @return the final total
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * Checks if any discounts were applied.
     *
     * @return true if the discounts list is not empty
     */
    public boolean hasDiscounts() {
        return !discounts.isEmpty();
    }

    @Override
    public String toString() {
        return "CheckoutResult{" +
                "subtotal=" + subtotal +
                ", discounts=" + discounts +
                ", total=" + total +
                '}';
    }
}
