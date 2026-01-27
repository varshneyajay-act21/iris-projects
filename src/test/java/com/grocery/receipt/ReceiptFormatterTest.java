package com.grocery.receipt;

import com.grocery.discount.BuyTwoGetOneFreeDiscount;
import com.grocery.discount.DiscountRegistry;
import com.grocery.model.Basket;
import com.grocery.model.Item;
import com.grocery.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ReceiptFormatter class.
 *
 * @author Grocery System
 * @version 1.0.0
 */
class ReceiptFormatterTest {
    private ReceiptFormatter receiptFormatter;
    private Item banana;
    private Item orange;

    @BeforeEach
    void setUp() {
        receiptFormatter = new ReceiptFormatter();
        banana = new Item("Banana", new BigDecimal("0.50"));
        orange = new Item("Orange", new BigDecimal("0.30"));
    }

    @Test
    void testReceiptFormattingWithNullSummary() {
        assertThrows(NullPointerException.class, () -> receiptFormatter.formatReceipt(null));
    }

    @Test
    void testReceiptFormattingWithNullResult() {
        List<ReceiptItem> items = new ArrayList<>();
        assertThrows(NullPointerException.class,
            () -> receiptFormatter.formatReceiptFromResult(null, items));
    }

    @Test
    void testReceiptFormattingWithNullItems() {
        DiscountRegistry registry = new DiscountRegistry();
        CheckoutService service = new CheckoutService(registry);
        Basket basket = new Basket();
        basket.addItem(banana, 1);

        var result = service.processCheckout(basket);
        assertThrows(NullPointerException.class,
            () -> receiptFormatter.formatReceiptFromResult(result, null));
    }

    @Test
    void testReceiptFormattingBasicOutput() {
        DiscountRegistry registry = new DiscountRegistry();
        CheckoutService service = new CheckoutService(registry);
        Basket basket = new Basket();
        basket.addItem(banana, 1);

        var result = service.processCheckout(basket);
        List<ReceiptItem> items = new ArrayList<>();
        items.add(new ReceiptItem("Banana", 1, new BigDecimal("0.50")));

        String receipt = receiptFormatter.formatReceiptFromResult(result, items);

        assertNotNull(receipt);
        assertFalse(receipt.isEmpty());
        assertTrue(receipt.contains("Banana"));
        assertTrue(receipt.contains("GROCERY STORE CHECKOUT RECEIPT"));
        assertTrue(receipt.contains("TOTAL"));
    }

    @Test
    void testReceiptFormattingWithMultipleItems() {
        DiscountRegistry registry = new DiscountRegistry();
        registry.registerDiscount(new BuyTwoGetOneFreeDiscount(banana));
        CheckoutService service = new CheckoutService(registry);
        Basket basket = new Basket();
        basket.addItem(banana, 3);
        basket.addItem(orange, 2);

        var result = service.processCheckout(basket);
        List<ReceiptItem> items = new ArrayList<>();
        items.add(new ReceiptItem("Banana", 3, new BigDecimal("0.50")));
        items.add(new ReceiptItem("Orange", 2, new BigDecimal("0.30")));

        String receipt = receiptFormatter.formatReceiptFromResult(result, items);

        assertTrue(receipt.contains("Banana"));
        assertTrue(receipt.contains("Orange"));
        assertTrue(receipt.contains("Subtotal"));
        assertTrue(receipt.contains("TOTAL"));
    }

    @Test
    void testReceiptFormattingIncludesDiscounts() {
        DiscountRegistry registry = new DiscountRegistry();
        registry.registerDiscount(new BuyTwoGetOneFreeDiscount(banana));
        CheckoutService service = new CheckoutService(registry);
        Basket basket = new Basket();
        basket.addItem(banana, 3);

        var result = service.processCheckout(basket);
        List<ReceiptItem> items = new ArrayList<>();
        items.add(new ReceiptItem("Banana", 3, new BigDecimal("0.50")));

        String receipt = receiptFormatter.formatReceiptFromResult(result, items);

        assertTrue(receipt.contains("Discounts"));
        assertTrue(receipt.contains("Total Discount"));
    }

    @Test
    void testReceiptItemCreation() {
        ReceiptItem item = new ReceiptItem("Test Item", 5, new BigDecimal("1.50"));
        assertEquals("Test Item", item.getItemName());
        assertEquals(5, item.getQuantity());
        assertEquals(new BigDecimal("1.50"), item.getUnitPrice());
    }

    @Test
    void testReceiptItemWithInvalidQuantity() {
        assertThrows(IllegalArgumentException.class,
            () -> new ReceiptItem("Test Item", 0, new BigDecimal("1.50")));
    }

    @Test
    void testReceiptItemWithNullName() {
        assertThrows(NullPointerException.class,
            () -> new ReceiptItem(null, 1, new BigDecimal("1.50")));
    }

    @Test
    void testReceiptItemWithNullPrice() {
        assertThrows(NullPointerException.class,
            () -> new ReceiptItem("Test Item", 1, null));
    }
}
