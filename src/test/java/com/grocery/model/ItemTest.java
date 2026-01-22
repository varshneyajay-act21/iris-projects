package com.grocery.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Item class.
 *
 * @author Grocery System
 * @version 1.0.0
 */
class ItemTest {

    @Test
    void testItemCreation() {
        Item item = new Item("Banana", new BigDecimal("0.50"));
        assertEquals("Banana", item.getName());
        assertEquals(new BigDecimal("0.50"), item.getPricePerUnit());
    }

    @Test
    void testItemWithTrimmedName() {
        Item item = new Item("  Banana  ", new BigDecimal("0.50"));
        assertEquals("Banana", item.getName());
    }

    @Test
    void testItemWithNullName() {
        assertThrows(NullPointerException.class, () -> new Item(null, new BigDecimal("0.50")));
    }

    @Test
    void testItemWithEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> new Item("", new BigDecimal("0.50")));
    }

    @Test
    void testItemWithNullPrice() {
        assertThrows(NullPointerException.class, () -> new Item("Banana", null));
    }

    @Test
    void testItemWithNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> new Item("Banana", new BigDecimal("-0.50")));
    }

    @Test
    void testItemWithZeroPrice() {
        Item item = new Item("Free Item", BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, item.getPricePerUnit());
    }

    @Test
    void testItemEquality() {
        Item item1 = new Item("Banana", new BigDecimal("0.50"));
        Item item2 = new Item("Banana", new BigDecimal("0.50"));
        assertEquals(item1, item2);
    }

    @Test
    void testItemInequality() {
        Item item1 = new Item("Banana", new BigDecimal("0.50"));
        Item item2 = new Item("Orange", new BigDecimal("0.30"));
        assertNotEquals(item1, item2);
    }

    @Test
    void testItemHashCode() {
        Item item1 = new Item("Banana", new BigDecimal("0.50"));
        Item item2 = new Item("Banana", new BigDecimal("0.50"));
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Item", "123 Item", "Item 123", "Item-Name"})
    void testValidItemNames(String name) {
        Item item = new Item(name, new BigDecimal("0.50"));
        assertNotNull(item);
        assertEquals(name, item.getName());
    }
}
