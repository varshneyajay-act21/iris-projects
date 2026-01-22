package com.grocery.receipt;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a single item on a receipt.
 *
 * @author Grocery System
 * @version 1.0.0
 */
public class ReceiptItem {
    private final String itemName;
    private final int quantity;
    private final BigDecimal unitPrice;

    /**
     * Creates a new ReceiptItem.
     *
     * @param itemName the item name (must not be null)
     * @param quantity the quantity (must be positive)
     * @param unitPrice the unit price (must not be null)
     */
    public ReceiptItem(String itemName, int quantity, BigDecimal unitPrice) {
        this.itemName = Objects.requireNonNull(itemName, "Item name cannot be null");
        this.quantity = quantity;
        this.unitPrice = Objects.requireNonNull(unitPrice, "Unit price cannot be null");

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
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
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}
