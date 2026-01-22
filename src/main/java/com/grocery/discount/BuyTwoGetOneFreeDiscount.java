package com.grocery.discount;

import com.grocery.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Discount strategy for "Buy 2 Get 1 Free" promotions.
 * For every 3 items of the specified type, the 3rd item is free.
 *
 * Example: 3 bananas at £0.50 each = £1.50, but with discount = £1.00
 * The discount is calculated as: (quantity / 3) * unitPrice
 *
 * @author Grocery System
 * @version 1.0.0
 */
public class BuyTwoGetOneFreeDiscount extends Discount {
    private static final Logger LOGGER = LoggerFactory.getLogger(BuyTwoGetOneFreeDiscount.class);
    private final Item item;

    /**
     * Creates a new BuyTwoGetOneFreeDiscount for the specified item.
     *
     * @param item the item this discount applies to (must not be null)
     * @throws NullPointerException if item is null
     */
    public BuyTwoGetOneFreeDiscount(Item item) {
        this.item = Objects.requireNonNull(item, "Item cannot be null");
        LOGGER.debug("Created BuyTwoGetOneFreeDiscount for item: {}", item.getName());
    }

    @Override
    public Item getItem() {
        return item;
    }

    @Override
    public DiscountResult calculateDiscount(int quantity, BigDecimal unitPrice) {
        Objects.requireNonNull(unitPrice, "Unit price cannot be null");

        if (quantity < 3) {
            LOGGER.debug("No discount applied for {} - quantity {} is less than 3", item.getName(), quantity);
            return new DiscountResult(
                    item,
                    getDiscountName(),
                    BigDecimal.ZERO,
                    0
            );
        }

        // Calculate number of free items (one free for every 3 items)
        int freeItems = quantity / 3;
        BigDecimal discountAmount = unitPrice.multiply(BigDecimal.valueOf(freeItems));

        LOGGER.debug(
                "Discount applied for {}: quantity={}, freeItems={}, discountAmount={}",
                item.getName(),
                quantity,
                freeItems,
                discountAmount
        );

        return new DiscountResult(
                item,
                getDiscountName(),
                discountAmount,
                freeItems
        );
    }

    @Override
    public String getDiscountName() {
        return "Buy 2 Get 1 Free";
    }
}
