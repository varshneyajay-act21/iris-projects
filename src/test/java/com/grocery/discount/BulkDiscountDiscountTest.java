package com.grocery.discount;

import com.grocery.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BulkDiscount class.
 *
 * @author Grocery System
 * @version 1.0.0
 */
class BulkDiscountTest {
    private BulkDiscount discount;
    private Item orange;

    @BeforeEach
    void setUp() {
        orange = new Item("Orange", new BigDecimal("0.30"));
        discount = new BulkDiscount(orange, 3, new BigDecimal("0.75"));
    }

    @Test
    void testDiscountCreation() {
        assertEquals(orange, discount.getItem());
        assertEquals("3 for £0.75", discount.getDiscountName());
    }

    @Test
    void testNoDiscountForFewerItems() {
        DiscountResult result = discount.calculateDiscount(2, new BigDecimal("0.30"));
        assertFalse(result.isApplicable());
        assertEquals(BigDecimal.ZERO, result.getDiscountAmount());
    }

    @Test
    void testDiscountForExactQuantity() {
        // 3 oranges: normally 3 * 0.30 = 0.90, with discount = 0.75
        DiscountResult result = discount.calculateDiscount(3, new BigDecimal("0.30"));
        assertTrue(result.isApplicable());
        assertEquals(new BigDecimal("0.15"), result.getDiscountAmount());
        assertEquals(3, result.getItemsApplied());
    }

    @Test
    void testDiscountForFourItems() {
        // 4 oranges: 1 group of 3 @ 0.75 + 1 @ 0.30 = 1.05
        // Normal: 4 * 0.30 = 1.20, Discount: 1.20 - 1.05 = 0.15
        DiscountResult result = discount.calculateDiscount(4, new BigDecimal("0.30"));
        assertTrue(result.isApplicable());
        assertEquals(new BigDecimal("0.15"), result.getDiscountAmount());
    }

    @Test
    void testDiscountForSixItems() {
        // 6 oranges: 2 groups of 3 @ 0.75 each = 1.50
        // Normal: 6 * 0.30 = 1.80, Discount: 1.80 - 1.50 = 0.30
        DiscountResult result = discount.calculateDiscount(6, new BigDecimal("0.30"));
        assertTrue(result.isApplicable());
        assertEquals(new BigDecimal("0.30"), result.getDiscountAmount());
        assertEquals(6, result.getItemsApplied());
    }

    @Test
    void testDiscountCreationWithInvalidItemCount() {
        assertThrows(IllegalArgumentException.class, () ->
            new BulkDiscount(orange, 0, new BigDecimal("0.75")));
    }

    @Test
    void testDiscountCreationWithNegativePrice() {
        assertThrows(IllegalArgumentException.class, () ->
            new BulkDiscount(orange, 3, new BigDecimal("-0.75")));
    }

    @Test
    void testDiscountCreationWithNullItem() {
        assertThrows(NullPointerException.class, () ->
            new BulkDiscount(null, 3, new BigDecimal("0.75")));
    }

    @Test
    void testDiscountCreationWithNullPrice() {
        assertThrows(NullPointerException.class, () ->
            new BulkDiscount(orange, 3, null));
    }

    @Test
    void testDiscountWithNullUnitPrice() {
        assertThrows(NullPointerException.class, () -> discount.calculateDiscount(3, null));
    }
}
