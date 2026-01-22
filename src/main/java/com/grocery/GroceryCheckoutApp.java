package com.grocery;

import com.grocery.catalog.ItemCatalog;
import com.grocery.discount.BulkDiscountDiscount;
import com.grocery.discount.BuyTwoGetOneFreeDiscount;
import com.grocery.discount.DiscountRegistry;
import com.grocery.model.Basket;
import com.grocery.receipt.ReceiptFormatter;
import com.grocery.receipt.BasketSummary;
import com.grocery.receipt.ReceiptItem;
import com.grocery.service.CheckoutService;
import com.grocery.service.CheckoutResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main application entry point for the Grocery Store Checkout System.
 * Provides an interactive command-line interface for processing grocery purchases.
 *
 * @author Grocery System
 * @version 1.0.0
 */
public class GroceryCheckoutApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroceryCheckoutApp.class);
    private final CheckoutService checkoutService;
    private final ReceiptFormatter receiptFormatter;

    /**
     * Constructs the application with a configured checkout service and receipt formatter.
     */
    public GroceryCheckoutApp() {
        // Initialize discount registry with available discounts
        DiscountRegistry discountRegistry = new DiscountRegistry();
        discountRegistry.registerDiscount(new BuyTwoGetOneFreeDiscount(ItemCatalog.BANANAS));
        discountRegistry.registerDiscount(new BulkDiscountDiscount(
                ItemCatalog.ORANGES,
                3,
                new BigDecimal("0.75")
        ));

        this.checkoutService = new CheckoutService(discountRegistry);
        this.receiptFormatter = new ReceiptFormatter();

        LOGGER.info("Grocery Checkout System initialized");
    }

    /**
     * Runs the interactive application.
     */
    public void run() {
        LOGGER.info("Starting Grocery Checkout System");
        System.out.println("\n=== Grocery Store Checkout System ===");
        System.out.println("Available items:");
        ItemCatalog.getAllItems().forEach((name, item) ->
                System.out.printf("  - %s: £%.2f%n", item.getName(), item.getPricePerUnit())
        );
        System.out.println("\nSpecial Offers:");
        System.out.println("  - Bananas: Buy 2 Get 1 Free");
        System.out.println("  - Oranges: 3 for £0.75");

        try (Scanner scanner = new Scanner(System.in)) {
            Basket basket = new Basket();
            boolean running = true;

            while (running) {
                System.out.println("\n--- Options ---");
                System.out.println("1. Add item to basket");
                System.out.println("2. Remove item from basket");
                System.out.println("3. View basket");
                System.out.println("4. Checkout");
                System.out.println("5. Clear basket");
                System.out.println("6. Exit");
                System.out.print("Choose an option: ");

                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        addItemToBasket(basket, scanner);
                        break;
                    case "2":
                        removeItemFromBasket(basket, scanner);
                        break;
                    case "3":
                        viewBasket(basket);
                        break;
                    case "4":
                        checkout(basket);
                        basket.clear();
                        break;
                    case "5":
                        basket.clear();
                        System.out.println("Basket cleared.");
                        LOGGER.info("Basket cleared by user");
                        break;
                    case "6":
                        running = false;
                        System.out.println("Thank you for using Grocery Checkout System. Goodbye!");
                        LOGGER.info("Application closed by user");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                        LOGGER.warn("Invalid menu option selected: {}", choice);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Application error", e);
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    /**
     * Handles adding an item to the basket.
     */
    private void addItemToBasket(Basket basket, Scanner scanner) {
        System.out.print("Enter item name: ");
        String itemName = scanner.nextLine().trim();

        if (!ItemCatalog.containsItem(itemName)) {
            System.out.println("Item not found in catalog.");
            LOGGER.warn("User attempted to add non-existent item: {}", itemName);
            return;
        }

        System.out.print("Enter quantity: ");
        try {
            int quantity = Integer.parseInt(scanner.nextLine().trim());
            if (quantity <= 0) {
                System.out.println("Quantity must be positive.");
                return;
            }

            basket.addItem(ItemCatalog.getItem(itemName), quantity);
            System.out.printf("Added %d %s(s) to basket.%n", quantity, itemName);
            LOGGER.info("Added {} {} to basket", quantity, itemName);
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity. Please enter a number.");
            LOGGER.warn("Invalid quantity entered: {}", scanner.nextLine(), e);
        }
    }

    /**
     * Handles removing an item from the basket.
     */
    private void removeItemFromBasket(Basket basket, Scanner scanner) {
        System.out.print("Enter item name to remove: ");
        String itemName = scanner.nextLine().trim();

        if (!ItemCatalog.containsItem(itemName)) {
            System.out.println("Item not found in catalog.");
            return;
        }

        basket.removeItem(ItemCatalog.getItem(itemName));
        System.out.printf("Removed %s from basket.%n", itemName);
        LOGGER.info("Removed {} from basket", itemName);
    }

    /**
     * Displays the current contents of the basket.
     */
    private void viewBasket(Basket basket) {
        if (basket.isEmpty()) {
            System.out.println("Basket is empty.");
            return;
        }

        System.out.println("\n--- Basket Contents ---");
        basket.getItems().forEach(item ->
                System.out.printf("  %s: %d x £%.2f = £%.2f%n",
                        item.getItem().getName(),
                        item.getQuantity(),
                        item.getItem().getPricePerUnit(),
                        item.getItem().getPricePerUnit().multiply(BigDecimal.valueOf(item.getQuantity()))
                )
        );
        System.out.printf("Total items: %d%n", basket.getTotalQuantity());
    }

    /**
     * Processes the checkout for the basket.
     */
    private void checkout(Basket basket) {
        if (basket.isEmpty()) {
            System.out.println("Basket is empty. Cannot checkout.");
            LOGGER.warn("Attempted checkout with empty basket");
            return;
        }

        try {
            CheckoutResult result = checkoutService.processCheckout(basket);

            // Build receipt items list
            List<ReceiptItem> receiptItems = new ArrayList<>();
            basket.getItems().forEach(item ->
                    receiptItems.add(new ReceiptItem(
                            item.getItem().getName(),
                            item.getQuantity(),
                            item.getItem().getPricePerUnit()
                    ))
            );

            // Generate and display receipt
            String receipt = receiptFormatter.formatReceiptFromResult(result, receiptItems);
            System.out.println("\n" + receipt);

            LOGGER.info("Checkout completed - Total: £{}", result.getTotal());
        } catch (IllegalArgumentException e) {
            System.out.println("Checkout error: " + e.getMessage());
            LOGGER.error("Checkout error", e);
        }
    }

    /**
     * Main method - entry point for the application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        LOGGER.info("Starting Grocery Store Checkout System application");
        GroceryCheckoutApp app = new GroceryCheckoutApp();
        app.run();
    }
}
