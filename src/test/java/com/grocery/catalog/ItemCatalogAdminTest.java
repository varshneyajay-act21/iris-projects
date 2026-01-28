package com.grocery.catalog;

import com.grocery.model.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ItemCatalogAdminTest {

    @AfterEach
    void cleanup() {
        // Reset by removing anything we added (ignore failures)
        try {
            ItemCatalog.removeItem("TestItem");
        } catch (Exception ignored) {}
    }

    @Test
    void addAndRemoveItemWorks() {
        Item test = new Item("TestItem", new BigDecimal("1.23"));
        ItemCatalog.addItem(test);
        assertTrue(ItemCatalog.containsItem("TestItem"));

        Item found = ItemCatalog.getItem("TestItem");
        assertEquals(test.getName(), found.getName());

        Item removed = ItemCatalog.removeItem("TestItem");
        assertEquals(test.getName(), removed.getName());
    }
}
