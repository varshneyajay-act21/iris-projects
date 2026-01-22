package com.grocery.integration;

import com.grocery.catalog.ItemCatalog;
import com.grocery.discount.BulkDiscountDiscount;
import com.grocery.discount.BuyTwoGetOneFreeDiscount;
import com.grocery.discount.DiscountRegistry;
import com.grocery.model.Basket;
import com.grocery.receipt.ReceiptFormatter;
import com.grocery.receipt.ReceiptItem;
import com.grocery.service.CheckoutResult;
import com.grocery.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the complete checkout flow.
 * These tests verify end-to-end functionality of the system.
 *
 * @author Grocery System
 * @version 1.0.0
 */
class CheckoutIntegrationTest {
    private CheckoutService checkoutService;
    private ReceiptFormatter receiptFormatter;
    private DiscountRegistry discountRegistry;

    @BeforeEach
    void setUp() {
        // Initialize with production-like configuration
        discountRegistry = new DiscountRegistry();
        discountRegistry.registerDiscount(new BuyTwoGetOneFreeDiscount(ItemCatalog.BANANAS));
        discountRegistry.registerDiscount(new BulkDiscountDiscount(
                ItemCatalog.ORANGES,
                3,
                new BigDecimal("0.75")
        ));

        checkoutService = new CheckoutService(discountRegistry);
        receiptFormatter = new ReceiptFormatter();
    }

    @Test
    void testExample_ThreebananasFourOrangesOneApple() {
        // Based on the example in the requirements
        Basket basket = new Basket();
        basket.addItem(ItemCatalog.BANANAS, 3);
        basket.addItem(ItemCatalog.ORANGES, 4);
        basket.addItem(ItemCatalog.APPLES, 1);

        CheckoutResult result = checkoutService.processCheckout(basket);

        // Bananas: 3 * 0.50 = 1.50, discount = 0.50 (Buy 2 Get 1 Free)
        // Oranges: 4 * 0.30 = 1.20, discount = 0.15 (3 for 0.75 + 1 @ 0.30)
        // Apples: 1 * 0.60 = 0.60, no discount
        // Subtotal: 3.30, Total Discount: 0.65, Total: 2.65
        assertEquals(new BigDecimal("3.30"), result.getSubtotal());
        assertEquals(new BigDecimal("0.65"), result.getTotalDiscountAmount());
        assertEquals(new BigDecimal("2.65"), result.getTotal());
    }

    @Test
    void testCheckout_OnlyBananasWithDiscount() {
        Basket basket = new Basket();
        basket.addItem(ItemCatalog.BANANAS, 3);

        CheckoutResult result = checkoutService.processCheckout(basket);

        // 3 Bananas: 3 * 0.50 = 1.50
        // Discount: 0.50 (Buy 2 Get 1 Free)
        // Total: 1.00
        assertEquals(new BigDecimal("1.50"), result.getSubtotal());
        assertEquals(new BigDecimal("0.50"), result.getTotalDiscountAmount());
        assertEquals(new BigDecimal("1.00"), result.getTotal());
        assertEquals(1, result.getDiscounts().size());
    }

    @Test
    void testCheckout_OnlyOrangesWithDiscount() {
        Basket basket = new Basket();
        basket.addItem(ItemCatalog.ORANGES, 3);

        CheckoutResult result = checkoutService.processCheckout(basket);

        // 3 Oranges: 3 * 0.30 = 0.90
        // Discount: 0.15 (3 for 0.75)
        // Total: 0.75
        assertEquals(new BigDecimal("0.90"), result.getSubtotal());
        assertEquals(new BigDecimal("0.15"), result.getTotalDiscountAmount());
        assertEquals(new BigDecimal("0.75"), result.getTotal());
    }

    @Test
    void testCheckout_MixedItemsNoDiscounts() {
        Basket basket = new Basket();
        basket.addItem(ItemCatalog.APPLES, 2);
        basket.addItem(ItemCatalog.LEMONS, 4);
        basket.addItem(ItemCatalog.PEACHES, 1);

        CheckoutResult result = checkoutService.processCheckout(basket);

        // Apples: 2 * 0.60 = 1.20
        // Lemons: 4 * 0.25 = 1.00
        // Peaches: 1 * 0.75 = 0.75
        // Total: 2.95, no discounts
        assertEquals(0, result.getSubtotal().compareTo(new BigDecimal("2.95")));
        assertEquals(0, result.getTotalDiscountAmount().compareTo(BigDecimal.ZERO));
        assertEquals(0, result.getTotal().compareTo(new BigDecimal("2.95")));
        assertFalse(result.hasDiscounts());
    }

    @Test
    void testCheckout_BananasJustBelowDiscount() {
        Basket basket = new Basket();
        basket.addItem(ItemCatalog.BANANAS, 2);

        CheckoutResult result = checkoutService.processCheckout(basket);

        // 2 Bananas: 2 * 0.50 = 1.00, no discount
        assertEquals(0, result.getSubtotal().compareTo(new BigDecimal("1.00")));
        assertEquals(0, result.getTotalDiscountAmount().compareTo(BigDecimal.ZERO));
        assertEquals(0, result.getTotal().compareTo(new BigDecimal("1.00")));
    }

    @Test
    void testCheckout_OrangesJustBelowDiscount() {
        Basket basket = new Basket();
        basket.addItem(ItemCatalog.ORANGES, 2);

        CheckoutResult result = checkoutService.processCheckout(basket);

        // 2 Oranges: 2 * 0.30 = 0.60, no discount
        assertEquals(0, result.getSubtotal().compareTo(new BigDecimal("0.60")));
        assertEquals(0, result.getTotalDiscountAmount().compareTo(BigDecimal.ZERO));
        assertEquals(0, result.getTotal().compareTo(new BigDecimal("0.60")));
    }

    @Test
    void testCheckout_LargeQuantitiesBananas() {
        Basket basket = new Basket();
        basket.addItem(ItemCatalog.BANANAS, 10);

        CheckoutResult result = checkoutService.processCheckout(basket);

        // 10 Bananas: 10 * 0.50 = 5.00
        // Discount: 3 free items (10 / 3 = 3) = 3 * 0.50 = 1.50
        // Total: 3.50
        assertEquals(new BigDecimal("5.00"), result.getSubtotal());
        assertEquals(new BigDecimal("1.50"), result.getTotalDiscountAmount());
        assertEquals(new BigDecimal("3.50"), result.getTotal());
    }

    @Test
    void testCheckout_LargeQuantitiesOranges() {
        Basket basket = new Basket();
        basket.addItem(ItemCatalog.ORANGES, 12);

        CheckoutResult result = checkoutService.processCheckout(basket);

        // 12 Oranges: 12 * 0.30 = 3.60
        // Discount: 4 groups of 3 @ 0.75 = 3.00, total = 3.00
        // Discount amount: 3.60 - 3.00 = 0.60
        assertEquals(new BigDecimal("3.60"), result.getSubtotal());
        assertEquals(new BigDecimal("0.60"), result.getTotalDiscountAmount());
        assertEquals(new BigDecimal("3.00"), result.getTotal());
    }

    @Test
    void testCheckout_AllItemsWithMultipleDiscounts() {
        Basket basket = new Basket();
        basket.addItem(ItemCatalog.BANANAS, 6);
        basket.addItem(ItemCatalog.ORANGES, 9);
        basket.addItem(ItemCatalog.APPLES, 2);
        basket.addItem(ItemCatalog.LEMONS, 3);
        basket.addItem(ItemCatalog.PEACHES, 1);

        CheckoutResult result = checkoutService.processCheckout(basket);

        // Bananas: 6 * 0.50 = 3.00, discount = 1.00 (2 free)
        // Oranges: 9 * 0.30 = 2.70, discount = 0.45 (3 groups of 3 @ 0.75)
        // Apples: 2 * 0.60 = 1.20, no discount
        // Lemons: 3 * 0.25 = 0.75, no discount
        // Peaches: 1 * 0.75 = 0.75, no discount
        // Total subtotal: 8.40, Total discount: 1.45, Total: 6.95
        assertEquals(new BigDecimal("8.40"), result.getSubtotal());
        assertEquals(new BigDecimal("1.45"), result.getTotalDiscountAmount());
        assertEquals(new BigDecimal("6.95"), result.getTotal());
        assertEquals(2, result.getDiscounts().size());
    }

    @Test
    void testReceiptGeneration() {
        Basket basket = new Basket();
        basket.addItem(ItemCatalog.BANANAS, 3);
        basket.addItem(ItemCatalog.ORANGES, 3);

        CheckoutResult result = checkoutService.processCheckout(basket);
        List<ReceiptItem> items = new ArrayList<>();
        items.add(new ReceiptItem("Bananas", 3, ItemCatalog.BANANAS.getPricePerUnit()));
        items.add(new ReceiptItem("Oranges", 3, ItemCatalog.ORANGES.getPricePerUnit()));

        String receipt = receiptFormatter.formatReceiptFromResult(result, items);

        assertNotNull(receipt);
        assertFalse(receipt.isEmpty());
        assertTrue(receipt.contains("GROCERY STORE CHECKOUT RECEIPT"));
        assertTrue(receipt.contains("Bananas"));
        assertTrue(receipt.contains("Oranges"));
        assertTrue(receipt.contains("Subtotal"));
        assertTrue(receipt.contains("Discounts"));
        assertTrue(receipt.contains("Total Discount"));
        assertTrue(receipt.contains("TOTAL"));
    }

    @Test
    void testCompleteCheckoutFlow() {
        // Simulate complete user flow
        Basket basket = new Basket();

        // User adds items
        basket.addItem(ItemCatalog.BANANAS, 4);
        basket.addItem(ItemCatalog.ORANGES, 5);

        // Verify basket state
        assertEquals(2, basket.getUniqueItemCount());
        assertEquals(9, basket.getTotalQuantity());

        // Process checkout
        CheckoutResult result = checkoutService.processCheckout(basket);

        // Verify calculations
        // Bananas: 4 * 0.50 = 2.00, discount = 0.50 (1 free)
        // Oranges: 5 * 0.30 = 1.50, discount = 0.15 (3 for 0.75 + 2 @ 0.30)
        assertEquals(new BigDecimal("3.50"), result.getSubtotal());
        assertEquals(new BigDecimal("0.65"), result.getTotalDiscountAmount());
        assertEquals(new BigDecimal("2.85"), result.getTotal());
        assertEquals(2, result.getDiscounts().size());

        // Generate receipt
        List<ReceiptItem> items = new ArrayList<>();
        basket.getItems().forEach(item ->
            items.add(new ReceiptItem(
                item.getItem().getName(),
                item.getQuantity(),
                item.getItem().getPricePerUnit()
            ))
        );

        String receipt = receiptFormatter.formatReceiptFromResult(result, items);
        assertNotNull(receipt);
        assertTrue(receipt.contains("2.85"));
    }
}
