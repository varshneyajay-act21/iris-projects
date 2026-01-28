package com.grocery.receipt;

import com.grocery.service.CheckoutResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class BasketSummaryTest {

    @Test
    void itemSummaryGetters() {
        BasketSummary.ItemSummary s = new BasketSummary.ItemSummary("Banana", 2, new BigDecimal("0.50"), new BigDecimal("1.00"));
        assertEquals("Banana", s.getItemName());
        assertEquals(2, s.getQuantity());
        assertEquals(new BigDecimal("0.50"), s.getUnitPrice());
        assertEquals(new BigDecimal("1.00"), s.getTotalPrice());
    }

    @Test
    void basketSummaryGetters() {
        CheckoutResult result = new CheckoutResult(new BigDecimal("1.00"), Collections.emptyList(), new BigDecimal("1.00"));
        BasketSummary.ItemSummary s = new BasketSummary.ItemSummary("Banana", 2, new BigDecimal("0.50"), new BigDecimal("1.00"));
        BasketSummary summary = new BasketSummary(Collections.singletonList(s), result);
        assertEquals(1, summary.getItems().size());
        assertSame(result, summary.getCheckoutResult());
    }
}
