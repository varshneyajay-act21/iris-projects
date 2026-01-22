package com.grocery.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Basket class.
 *
 * @author Grocery System
 * @version 1.0.0
 */
class BasketTest {
    private Basket basket;
    private Item banana;
    private Item orange;

    @BeforeEach
    void setUp() {
        basket = new Basket();
        banana = new Item("Banana", new BigDecimal("0.50"));
        orange = new Item("Orange", new BigDecimal("0.30"));
    }

    @Test
    void testEmptyBasketCreation() {
        assertTrue(basket.isEmpty());
        assertEquals(0, basket.getTotalQuantity());
        assertEquals(0, basket.getUniqueItemCount());
    }

    @Test
    void testAddSingleItem() {
        basket.addItem(banana, 1);
        assertFalse(basket.isEmpty());
        assertEquals(1, basket.getTotalQuantity());
        assertEquals(1, basket.getUniqueItemCount());
        assertEquals(1, basket.getQuantity(banana));
    }

    @Test
    void testAddMultipleItemsOfSameType() {
        basket.addItem(banana, 3);
        assertEquals(3, basket.getTotalQuantity());
        assertEquals(1, basket.getUniqueItemCount());
        assertEquals(3, basket.getQuantity(banana));
    }

    @Test
    void testAddMultipleDifferentItems() {
        basket.addItem(banana, 2);
        basket.addItem(orange, 3);
        assertEquals(5, basket.getTotalQuantity());
        assertEquals(2, basket.getUniqueItemCount());
    }

    @Test
    void testAddMoreOfExistingItem() {
        basket.addItem(banana, 2);
        basket.addItem(banana, 1);
        assertEquals(3, basket.getTotalQuantity());
        assertEquals(1, basket.getUniqueItemCount());
        assertEquals(3, basket.getQuantity(banana));
    }

    @Test
    void testAddItemWithNullItem() {
        assertThrows(NullPointerException.class, () -> basket.addItem(null, 1));
    }

    @Test
    void testAddItemWithZeroQuantity() {
        assertThrows(IllegalArgumentException.class, () -> basket.addItem(banana, 0));
    }

    @Test
    void testAddItemWithNegativeQuantity() {
        assertThrows(IllegalArgumentException.class, () -> basket.addItem(banana, -1));
    }

    @Test
    void testRemoveItem() {
        basket.addItem(banana, 2);
        basket.removeItem(banana);
        assertTrue(basket.isEmpty());
        assertEquals(0, basket.getQuantity(banana));
    }

    @Test
    void testRemoveNonExistentItem() {
        basket.addItem(banana, 1);
        basket.removeItem(orange);
        assertEquals(1, basket.getQuantity(banana));
    }

    @Test
    void testClearBasket() {
        basket.addItem(banana, 2);
        basket.addItem(orange, 3);
        basket.clear();
        assertTrue(basket.isEmpty());
        assertEquals(0, basket.getTotalQuantity());
    }

    @Test
    void testGetItems() {
        basket.addItem(banana, 2);
        basket.addItem(orange, 3);
        var items = basket.getItems();
        assertEquals(2, items.size());
    }

    @Test
    void testGetQuantityOfNonExistentItem() {
        assertEquals(0, basket.getQuantity(banana));
    }

    @Test
    void testGetItems_UnmodifiableList() {
        basket.addItem(banana, 1);
        var items = basket.getItems();
        assertThrows(UnsupportedOperationException.class, () -> items.add(null));
    }
}
