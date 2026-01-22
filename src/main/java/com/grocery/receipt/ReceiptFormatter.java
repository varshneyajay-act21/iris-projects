package com.grocery.receipt;

import com.grocery.discount.DiscountResult;
import com.grocery.service.CheckoutResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Formatter for generating itemized receipts.
 * Produces formatted receipt text suitable for display or printing.
 *
 * @author Grocery System
 * @version 1.0.0
 */
public class ReceiptFormatter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiptFormatter.class);
    private static final String SEPARATOR = "=====================================";
    private static final int ITEM_COL_WIDTH = 15;
    private static final int QUANTITY_COL_WIDTH = 12;
    private static final int PRICE_COL_WIDTH = 12;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Formats a receipt from a basket summary.
     *
     * @param summary the basket summary (must not be null)
     * @return a formatted receipt string
     * @throws NullPointerException if summary is null
     */
    public String formatReceipt(BasketSummary summary) {
        Objects.requireNonNull(summary, "Basket summary cannot be null");

        LOGGER.debug("Formatting receipt for basket with {} items", summary.getItems().size());

        StringBuilder receipt = new StringBuilder();

        // Header
        receipt.append(SEPARATOR).append("\n");
        receipt.append("GROCERY STORE CHECKOUT RECEIPT\n");
        receipt.append(SEPARATOR).append("\n");
        receipt.append("Timestamp: ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("\n\n");

        // Items section
        receipt.append(String.format(
                "%-" + ITEM_COL_WIDTH + "s %-" + QUANTITY_COL_WIDTH + "s %" + PRICE_COL_WIDTH + "s\n",
                "Item", "Quantity", "Price"
        ));
        receipt.append(SEPARATOR).append("\n");

        for (BasketSummary.ItemSummary item : summary.getItems()) {
            receipt.append(String.format(
                    "%-" + ITEM_COL_WIDTH + "s %-" + QUANTITY_COL_WIDTH + "s £%" + (PRICE_COL_WIDTH - 1) + ".2f\n",
                    item.getItemName(),
                    item.getQuantity(),
                    item.getTotalPrice()
            ));
        }

        receipt.append(SEPARATOR).append("\n");

        // Subtotal
        receipt.append(String.format(
                "%-" + ITEM_COL_WIDTH + "s %" + (QUANTITY_COL_WIDTH + PRICE_COL_WIDTH) + ".2f\n",
                "Subtotal:",
                summary.getCheckoutResult().getSubtotal()
        )).append("\n");

        // Discounts section
        if (summary.getCheckoutResult().hasDiscounts()) {
            receipt.append("Discounts:\n");
            receipt.append("-".repeat(SEPARATOR.length())).append("\n");

            for (DiscountResult discount : summary.getCheckoutResult().getDiscounts()) {
                receipt.append(String.format(
                        "%-" + ITEM_COL_WIDTH + "s (%-20s) -£%" + (PRICE_COL_WIDTH - 1) + ".2f\n",
                        discount.getItem().getName(),
                        discount.getDescription(),
                        discount.getDiscountAmount()
                ));
            }

            receipt.append(SEPARATOR).append("\n");
            receipt.append(String.format(
                    "%-" + ITEM_COL_WIDTH + "s %" + (QUANTITY_COL_WIDTH + PRICE_COL_WIDTH) + ".2f\n",
                    "Total Discount:",
                    summary.getCheckoutResult().getTotalDiscountAmount()
            )).append("\n");
        }

        // Final total
        receipt.append(SEPARATOR).append("\n");
        receipt.append(String.format(
                "%-" + ITEM_COL_WIDTH + "s %" + (QUANTITY_COL_WIDTH + PRICE_COL_WIDTH) + ".2f\n",
                "TOTAL:",
                summary.getCheckoutResult().getTotal()
        ));
        receipt.append(SEPARATOR).append("\n");

        return receipt.toString();
    }

    /**
     * Formats a receipt from a checkout result.
     * This method is a convenience method when the item details are already available.
     *
     * @param result the checkout result (must not be null)
     * @param items the items in the basket (must not be null)
     * @return a formatted receipt string
     */
    public String formatReceiptFromResult(CheckoutResult result, java.util.List<ReceiptItem> items) {
        Objects.requireNonNull(result, "Checkout result cannot be null");
        Objects.requireNonNull(items, "Items list cannot be null");

        LOGGER.debug("Formatting receipt from checkout result with {} items", items.size());

        StringBuilder receipt = new StringBuilder();

        // Header
        receipt.append(SEPARATOR).append("\n");
        receipt.append("GROCERY STORE CHECKOUT RECEIPT\n");
        receipt.append(SEPARATOR).append("\n");
        receipt.append("Timestamp: ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("\n\n");

        // Items section
        receipt.append(String.format(
                "%-" + ITEM_COL_WIDTH + "s %-" + QUANTITY_COL_WIDTH + "s %" + PRICE_COL_WIDTH + "s\n",
                "Item", "Quantity", "Price"
        ));
        receipt.append(SEPARATOR).append("\n");

        for (ReceiptItem item : items) {
            BigDecimal itemTotal = item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            receipt.append(String.format(
                    "%-" + ITEM_COL_WIDTH + "s %-" + QUANTITY_COL_WIDTH + "s £%" + (PRICE_COL_WIDTH - 1) + ".2f\n",
                    item.getItemName(),
                    item.getQuantity(),
                    itemTotal
            ));
        }

        receipt.append(SEPARATOR).append("\n");

        // Subtotal
        receipt.append(String.format(
                "%-" + ITEM_COL_WIDTH + "s %" + (QUANTITY_COL_WIDTH + PRICE_COL_WIDTH) + ".2f\n",
                "Subtotal:",
                result.getSubtotal()
        )).append("\n");

        // Discounts section
        if (result.hasDiscounts()) {
            receipt.append("Discounts:\n");
            receipt.append("-".repeat(SEPARATOR.length())).append("\n");

            for (DiscountResult discount : result.getDiscounts()) {
                receipt.append(String.format(
                        "%-" + ITEM_COL_WIDTH + "s (%-20s) -£%" + (PRICE_COL_WIDTH - 1) + ".2f\n",
                        discount.getItem().getName(),
                        discount.getDescription(),
                        discount.getDiscountAmount()
                ));
            }

            receipt.append(SEPARATOR).append("\n");
            receipt.append(String.format(
                    "%-" + ITEM_COL_WIDTH + "s %" + (QUANTITY_COL_WIDTH + PRICE_COL_WIDTH) + ".2f\n",
                    "Total Discount:",
                    result.getTotalDiscountAmount()
            )).append("\n");
        }

        // Final total
        receipt.append(SEPARATOR).append("\n");
        receipt.append(String.format(
                "%-" + ITEM_COL_WIDTH + "s %" + (QUANTITY_COL_WIDTH + PRICE_COL_WIDTH) + ".2f\n",
                "TOTAL:",
                result.getTotal()
        ));
        receipt.append(SEPARATOR).append("\n");

        return receipt.toString();
    }
}
