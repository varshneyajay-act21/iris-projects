package com.grocery.discount;

import com.grocery.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Discount strategy for "X for £Y" bulk purchase promotions.
 * For example, "3 Oranges for £0.75" means when you buy 3 or more oranges,
 * you pay £0.75 per group of 3 (instead of individual prices).
 *
 * Calculation: (quantity / itemCount) * discountPrice + (quantity % itemCount) * unitPrice
 *
 * @author Grocery System
 * @version 1.0.0
 */
public class BulkDiscountDiscount extends Discount {
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkDiscountDiscount.class);
    private final Item item;
    private final int itemCount;
    private final BigDecimal discountPrice;

    /**
     * Creates a new BulkDiscountDiscount.
     *
     * @param item the item this discount applies to (must not be null)
     * @param itemCount the number of items required for the discount (must be positive)
     * @param discountPrice the total price for itemCount items (must not be null or negative)
     * @throws NullPointerException if item or discountPrice is null
     * @throws IllegalArgumentException if itemCount is not positive or discountPrice is negative
     */
    public BulkDiscountDiscount(Item item, int itemCount, BigDecimal discountPrice) {
        this.item = Objects.requireNonNull(item, "Item cannot be null");
        this.discountPrice = Objects.requireNonNull(discountPrice, "Discount price cannot be null");

        if (itemCount <= 0) {
            throw new IllegalArgumentException("Item count must be positive: " + itemCount);
        }
        if (discountPrice.signum() < 0) {
            throw new IllegalArgumentException("Discount price cannot be negative: " + discountPrice);
        }

        this.itemCount = itemCount;
        LOGGER.debug(
                "Created BulkDiscountDiscount for item: {}, {} for £{}",
                item.getName(),
                itemCount,
                discountPrice
        );
    }

    @Override
    public Item getItem() {
        return item;
    }

    @Override
    public DiscountResult calculateDiscount(int quantity, BigDecimal unitPrice) {
        Objects.requireNonNull(unitPrice, "Unit price cannot be null");

        if (quantity < itemCount) {
            LOGGER.debug(
                    "No discount applied for {} - quantity {} is less than required {}",
                    item.getName(),
                    quantity,
                    itemCount
            );
            return new DiscountResult(
                    item,
                    getDiscountName(),
                    BigDecimal.ZERO,
                    0
            );
        }

        // Calculate how many complete discount groups we have
        int discountGroups = quantity / itemCount;
        int remainder = quantity % itemCount;

        // Cost without discount for all items
        BigDecimal totalWithoutDiscount = unitPrice.multiply(BigDecimal.valueOf(quantity));

        // Cost with discount: discountGroups * discountPrice + remainder * unitPrice
        BigDecimal discountedCost = discountPrice.multiply(BigDecimal.valueOf(discountGroups))
                .add(unitPrice.multiply(BigDecimal.valueOf(remainder)));

        // Discount amount
        BigDecimal discountAmount = totalWithoutDiscount.subtract(discountedCost);

        LOGGER.debug(
                "Discount applied for {}: quantity={}, discountGroups={}, remainder={}, discountAmount={}",
                item.getName(),
                quantity,
                discountGroups,
                remainder,
                discountAmount
        );

        return new DiscountResult(
                item,
                getDiscountName(),
                discountAmount,
                discountGroups * itemCount
        );
    }

    @Override
    public String getDiscountName() {
        return itemCount + " for £" + discountPrice;
    }
}
