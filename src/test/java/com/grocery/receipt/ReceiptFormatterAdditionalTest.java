package com.grocery.receipt;

import com.grocery.discount.DiscountResult;
import com.grocery.model.Item;
import com.grocery.receipt.ReceiptFormatter;
import com.grocery.receipt.ReceiptItem;
import com.grocery.receipt.BasketSummary;
import com.grocery.service.CheckoutResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class ReceiptFormatterAdditionalTest {

    @Test
    void formatReceiptFromResult_includesSubtotalAndTotalAndItemsAndDiscounts() {
        Item banana = new Item("Banana", new BigDecimal("0.50"));
        DiscountResult discount = new DiscountResult(banana, "Buy 2 Get 1 Free", new BigDecimal("0.50"), 1);

        CheckoutResult result = new CheckoutResult(new BigDecimal("1.50"), Collections.singletonList(discount), new BigDecimal("1.00"));

        ReceiptItem r1 = new ReceiptItem("Banana", 3, banana.getPricePerUnit());

        ReceiptFormatter formatter = new ReceiptFormatter();
        String receipt = formatter.formatReceiptFromResult(result, Collections.singletonList(r1));

        assertTrue(receipt.contains("Subtotal"));
        assertTrue(receipt.contains("TOTAL:"));
        assertTrue(receipt.contains("Banana"));
        assertTrue(receipt.contains("Buy 2 Get 1 Free"));
    }

    @Test
    void formatReceipt_withBasketSummary_handlesDiscountsAndItems() {
        Item apple = new Item("Apple", new BigDecimal("0.60"));
        DiscountResult discount = new DiscountResult(apple, "TestDisc", new BigDecimal("0.20"), 1);
        CheckoutResult result = new CheckoutResult(new BigDecimal("1.20"), Collections.singletonList(discount), new BigDecimal("1.00"));

        BasketSummary.ItemSummary itemSummary = new BasketSummary.ItemSummary("Apple", 2, apple.getPricePerUnit(), apple.getPricePerUnit().multiply(new BigDecimal(2)));
        BasketSummary summary = new BasketSummary(Collections.singletonList(itemSummary), result);

        ReceiptFormatter formatter = new ReceiptFormatter();
        String receipt = formatter.formatReceipt(summary);

        assertTrue(receipt.contains("Apple"));
        assertTrue(receipt.contains("Subtotal:"));
        assertTrue(receipt.contains("TestDisc"));
        assertTrue(receipt.contains("TOTAL:"));
    }
}
