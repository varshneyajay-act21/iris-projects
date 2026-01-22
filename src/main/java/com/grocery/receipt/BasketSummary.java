package com.grocery.receipt;

import com.grocery.service.CheckoutResult;
import java.util.List;
import java.util.Objects;

/**
 * Represents a summary of items in a basket for receipt display.
 * This class aggregates item details with the checkout result.
 *
 * @author Grocery System
 * @version 1.0.0
 */
public class BasketSummary {
    private final List<ItemSummary> items;
    private final CheckoutResult checkoutResult;

    /**
     * Creates a new BasketSummary.
     *
     * @param items the items in the summary (must not be null)
     * @param checkoutResult the checkout result (must not be null)
     */
    public BasketSummary(List<ItemSummary> items, CheckoutResult checkoutResult) {
        this.items = Objects.requireNonNull(items, "Items list cannot be null");
        this.checkoutResult = Objects.requireNonNull(checkoutResult, "Checkout result cannot be null");
    }

    /**
     * Gets the items in the summary.
     *
     * @return the list of item summaries
     */
    public List<ItemSummary> getItems() {
        return items;
    }

    /**
     * Gets the checkout result.
     *
     * @return the checkout result
     */
    public CheckoutResult getCheckoutResult() {
        return checkoutResult;
    }

    /**
     * Represents a summary of a single item in the basket.
     */
    public static class ItemSummary {
        private final String itemName;
        private final int quantity;
        private final java.math.BigDecimal unitPrice;
        private final java.math.BigDecimal totalPrice;

        /**
         * Creates a new ItemSummary.
         *
         * @param itemName the item name (must not be null)
         * @param quantity the quantity (must be positive)
         * @param unitPrice the unit price (must not be null)
         * @param totalPrice the total price for quantity (must not be null)
         */
        public ItemSummary(String itemName, int quantity, java.math.BigDecimal unitPrice, java.math.BigDecimal totalPrice) {
            this.itemName = Objects.requireNonNull(itemName, "Item name cannot be null");
            this.quantity = quantity;
            this.unitPrice = Objects.requireNonNull(unitPrice, "Unit price cannot be null");
            this.totalPrice = Objects.requireNonNull(totalPrice, "Total price cannot be null");
        }

        /**
         * Gets the item name.
         */
        public String getItemName() {
            return itemName;
        }

        /**
         * Gets the quantity.
         */
        public int getQuantity() {
            return quantity;
        }

        /**
         * Gets the unit price.
         */
        public java.math.BigDecimal getUnitPrice() {
            return unitPrice;
        }

        /**
         * Gets the total price.
         */
        public java.math.BigDecimal getTotalPrice() {
            return totalPrice;
        }
    }
}
