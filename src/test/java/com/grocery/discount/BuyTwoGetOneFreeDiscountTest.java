package com.grocery.discount;

import com.grocery.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the BuyTwoGetOneFreeDiscount class.
 *
 * @author Grocery System
 * @version 1.0.0
 */
class BuyTwoGetOneFreeDiscountTest {
    private BuyTwoGetOneFreeDiscount discount;
    private Item banana;

    @BeforeEach
    void setUp() {
        banana = new Item("Banana", new BigDecimal("0.50"));
        discount = new BuyTwoGetOneFreeDiscount(banana);
    }

    @Test
    void testDiscountCreation() {
        assertEquals(banana, discount.getItem());
        assertEquals("Buy 2 Get 1 Free", discount.getDiscountName());
    }

    @Test
    void testNoDiscountForOneItem() {
        DiscountResult result = discount.calculateDiscount(1, new BigDecimal("0.50"));
        assertFalse(result.isApplicable());
        assertEquals(BigDecimal.ZERO, result.getDiscountAmount());
    }

    @Test
    void testNoDiscountForTwoItems() {
        DiscountResult result = discount.calculateDiscount(2, new BigDecimal("0.50"));
        assertFalse(result.isApplicable());
        assertEquals(BigDecimal.ZERO, result.getDiscountAmount());
    }

    @Test
    void testDiscountForThreeItems() {
        DiscountResult result = discount.calculateDiscount(3, new BigDecimal("0.50"));
        assertTrue(result.isApplicable());
        assertEquals(new BigDecimal("0.50"), result.getDiscountAmount());
        assertEquals(1, result.getItemsApplied());
    }

    @Test
    void testDiscountForSixItems() {
        // 6 items: 2 groups of 3, so 2 free items
        DiscountResult result = discount.calculateDiscount(6, new BigDecimal("0.50"));
        assertTrue(result.isApplicable());
        assertEquals(new BigDecimal("1.00"), result.getDiscountAmount());
        assertEquals(2, result.getItemsApplied());
    }

    @Test
    void testDiscountForFourItems() {
        // 4 items: 1 group of 3 + 1 remaining, so 1 free item
        DiscountResult result = discount.calculateDiscount(4, new BigDecimal("0.50"));
        assertTrue(result.isApplicable());
        assertEquals(new BigDecimal("0.50"), result.getDiscountAmount());
        assertEquals(1, result.getItemsApplied());
    }

    @Test
    void testDiscountCalculationWithDifferentPrice() {
        DiscountResult result = discount.calculateDiscount(3, new BigDecimal("1.00"));
        assertEquals(new BigDecimal("1.00"), result.getDiscountAmount());
    }

    @Test
    void testDiscountWithNullPrice() {
        assertThrows(NullPointerException.class, () -> discount.calculateDiscount(3, null));
    }
}
