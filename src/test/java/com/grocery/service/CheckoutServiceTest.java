package com.grocery.service;

import com.grocery.discount.BulkDiscountDiscount;
import com.grocery.discount.BuyTwoGetOneFreeDiscount;
import com.grocery.discount.DiscountRegistry;
import com.grocery.model.Basket;
import com.grocery.model.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the CheckoutService class.
 *
 * @author Grocery System
 * @version 1.0.0
 */
class CheckoutServiceTest {
    private CheckoutService checkoutService;
    private DiscountRegistry discountRegistry;
    private Item banana;
    private Item orange;
    private Item apple;

    @BeforeEach
    void setUp() {
        banana = new Item("Banana", new BigDecimal("0.50"));
        orange = new Item("Orange", new BigDecimal("0.30"));
        apple = new Item("Apple", new BigDecimal("0.60"));

        discountRegistry = new DiscountRegistry();
        discountRegistry.registerDiscount(new BuyTwoGetOneFreeDiscount(banana));
        discountRegistry.registerDiscount(new BulkDiscountDiscount(orange, 3, new BigDecimal("0.75")));

        checkoutService = new CheckoutService(discountRegistry);
    }

    @Test
    void testCheckoutWithNullBasket() {
        assertThrows(NullPointerException.class, () -> checkoutService.processCheckout(null));
    }

    @Test
    void testCheckoutWithEmptyBasket() {
        Basket basket = new Basket();
        assertThrows(IllegalArgumentException.class, () -> checkoutService.processCheckout(basket));
    }

    @Test
    void testCheckoutWithSingleItem() {
        Basket basket = new Basket();
        basket.addItem(apple, 1);

        CheckoutResult result = checkoutService.processCheckout(basket);
        assertEquals(0, result.getSubtotal().compareTo(new BigDecimal("0.60")));
        assertEquals(0, result.getTotalDiscountAmount().compareTo(BigDecimal.ZERO));
        assertEquals(0, result.getTotal().compareTo(new BigDecimal("0.60")));
    }

    @Test
    void testCheckoutWithMultipleItems_NoDiscount() {
        Basket basket = new Basket();
        basket.addItem(apple, 2);

        CheckoutResult result = checkoutService.processCheckout(basket);
        assertEquals(0, result.getSubtotal().compareTo(new BigDecimal("1.20")));
        assertEquals(0, result.getTotalDiscountAmount().compareTo(BigDecimal.ZERO));
        assertEquals(0, result.getTotal().compareTo(new BigDecimal("1.20")));
    }

    @Test
    void testCheckoutWithBuyTwoGetOneFreeBananas() {
        Basket basket = new Basket();
        basket.addItem(banana, 3);

        CheckoutResult result = checkoutService.processCheckout(basket);
        assertEquals(new BigDecimal("1.50"), result.getSubtotal());
        assertEquals(new BigDecimal("0.50"), result.getTotalDiscountAmount());
        assertEquals(new BigDecimal("1.00"), result.getTotal());
        assertTrue(result.hasDiscounts());
    }

    @Test
    void testCheckoutWithBulkOranges() {
        Basket basket = new Basket();
        basket.addItem(orange, 3);

        CheckoutResult result = checkoutService.processCheckout(basket);
        assertEquals(new BigDecimal("0.90"), result.getSubtotal());
        assertEquals(new BigDecimal("0.15"), result.getTotalDiscountAmount());
        assertEquals(new BigDecimal("0.75"), result.getTotal());
    }

    @Test
    void testCheckoutWithMultipleDiscounts() {
        Basket basket = new Basket();
        basket.addItem(banana, 3);
        basket.addItem(orange, 3);

        CheckoutResult result = checkoutService.processCheckout(basket);
        // Bananas: 3 * 0.50 = 1.50, discount = 0.50, total = 1.00
        // Oranges: 3 * 0.30 = 0.90, discount = 0.15, total = 0.75
        // Total subtotal = 2.40, total discount = 0.65, total = 1.75
        assertEquals(new BigDecimal("2.40"), result.getSubtotal());
        assertEquals(new BigDecimal("0.65"), result.getTotalDiscountAmount());
        assertEquals(new BigDecimal("1.75"), result.getTotal());
        assertEquals(2, result.getDiscounts().size());
    }

    @Test
    void testCheckoutWithMixedItems() {
        Basket basket = new Basket();
        basket.addItem(banana, 2);
        basket.addItem(apple, 1);
        basket.addItem(orange, 4);

        CheckoutResult result = checkoutService.processCheckout(basket);

        // Bananas: 2 * 0.50 = 1.00, no discount (need 3)
        // Apple: 1 * 0.60 = 0.60, no discount
        // Oranges: 4 * 0.30 = 1.20, discount = 0.15 (3 for 0.75 + 1 @ 0.30 = 1.05)
        assertEquals(new BigDecimal("2.80"), result.getSubtotal());
        assertEquals(new BigDecimal("0.15"), result.getTotalDiscountAmount());
        assertEquals(new BigDecimal("2.65"), result.getTotal());
    }

    @Test
    void testCheckoutWithServiceInitialization() {
        assertThrows(NullPointerException.class, () -> new CheckoutService(null));
    }
}
