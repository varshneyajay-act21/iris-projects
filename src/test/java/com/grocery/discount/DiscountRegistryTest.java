package com.grocery.discount;

import com.grocery.model.Item;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DiscountRegistryTest {

    @Test
    void registerFindAndUnregisterDiscounts() {
        DiscountRegistry registry = new DiscountRegistry();
        Item apple = new Item("Apple", new BigDecimal("0.60"));

        BuyTwoGetOneFreeDiscount d1 = new BuyTwoGetOneFreeDiscount(apple);
        registry.registerDiscount(d1);

        List<Discount> found = registry.findDiscountsByDescription("Buy 2");
        assertFalse(found.isEmpty());

        int removed = registry.unregisterDiscountsForItem("Apple");
        assertTrue(removed >= 1);
    }
}
