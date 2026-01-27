package com.grocery.service;

import com.grocery.discount.Discount;
import com.grocery.discount.DiscountRegistry;
import com.grocery.discount.DiscountResult;
import com.grocery.exception.GroceryException;
import com.grocery.model.Basket;
import com.grocery.model.BasketItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.*;

/**
 * Service for processing checkout operations and calculating totals with discounts.
 * This service coordinates the calculation of subtotals, discounts, and final totals.
 *
 * @author Grocery System
 * @version 1.0.0
 */
public class CheckoutService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckoutService.class);
    private final DiscountRegistry discountRegistry;

    /**
     * Creates a new CheckoutService with the specified discount registry.
     *
     * @param discountRegistry the registry of discounts (must not be null)
     * @throws NullPointerException if discountRegistry is null
     */
    public CheckoutService(DiscountRegistry discountRegistry) {
        this.discountRegistry = Objects.requireNonNull(discountRegistry, "Discount registry cannot be null");
        LOGGER.info("CheckoutService initialized with {} discounts", discountRegistry.size());
    }

    /**
     * Processes a checkout for the given basket.
     *
     * @param basket the basket to process (must not be null)
     * @return the checkout result with prices and discounts
     * @throws GroceryException if basket is null, empty, or processing fails
     */
    public CheckoutResult processCheckout(Basket basket) {
        if (basket == null) {
            throw new GroceryException("Basket cannot be null");
        }

        if (basket.isEmpty()) {
            LOGGER.warn("Attempting to process checkout for empty basket");
            throw new GroceryException("Cannot process checkout for empty basket");
        }

        LOGGER.info("Processing checkout for basket with {} unique items", basket.getUniqueItemCount());

        try {
            List<BasketItem> items = basket.getItems();
            BigDecimal subtotal = BigDecimal.ZERO;
            List<DiscountResult> appliedDiscounts = new ArrayList<>();

            // Calculate subtotal and apply discounts
            for (BasketItem basketItem : items) {
                BigDecimal itemTotal = basketItem.getItem().getPricePerUnit()
                        .multiply(BigDecimal.valueOf(basketItem.getQuantity()));
                subtotal = subtotal.add(itemTotal);

                LOGGER.debug(
                        "Item: {}, Quantity: {}, Unit Price: {}, Total: {}",
                        basketItem.getItem().getName(),
                        basketItem.getQuantity(),
                        basketItem.getItem().getPricePerUnit(),
                        itemTotal
                );

                // Apply applicable discounts
                for (Discount discount : discountRegistry.getDiscounts()) {
                    if (discount.getItem().equals(basketItem.getItem())) {
                        DiscountResult discountResult = discount.calculateDiscount(
                                basketItem.getQuantity(),
                                basketItem.getItem().getPricePerUnit()
                        );

                        if (discountResult.isApplicable()) {
                            appliedDiscounts.add(discountResult);
                            LOGGER.info(
                                    "Applied discount '{}' to {}: -£{}",
                                    discountResult.getDescription(),
                                    basketItem.getItem().getName(),
                                    discountResult.getDiscountAmount()
                            );
                        }
                    }
                }
            }

            // Calculate total discount
            BigDecimal totalDiscount = appliedDiscounts.stream()
                    .map(DiscountResult::getDiscountAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calculate final total
            BigDecimal total = subtotal.subtract(totalDiscount);

            LOGGER.info(
                    "Checkout processed - Subtotal: {}, Total Discount: {}, Final Total: {}",
                    subtotal,
                    totalDiscount,
                    total
            );

            return new CheckoutResult(subtotal, appliedDiscounts, total);
        } catch (GroceryException e) {
            // let our domain exceptions bubble up
            throw e;
        } catch (Exception e) {
            // Wrap unexpected exceptions to provide context
            throw new GroceryException("Failed to process checkout", e);
        }
    }
}
