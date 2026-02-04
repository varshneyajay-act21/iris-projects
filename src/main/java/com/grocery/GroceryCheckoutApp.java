package com.grocery;

import com.grocery.catalog.ItemCatalog;
import com.grocery.discount.BulkDiscount;
import com.grocery.discount.BuyTwoGetOneFreeDiscount;
import com.grocery.discount.DiscountRegistry;
import com.grocery.exception.CatalogException;
import com.grocery.exception.GroceryException;
import com.grocery.model.Basket;
import com.grocery.model.Item;
import com.grocery.receipt.ReceiptFormatter;
import com.grocery.receipt.ReceiptItem;
import com.grocery.service.CheckoutService;
import com.grocery.service.CheckoutResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.grocery.config.Constants;

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
    private final DiscountRegistry discountRegistry;

    // Reuse admin password from central Constants (can be overridden via -DADMIN_PASSWORD)
    private static final String ADMIN_PASSWORD = Constants.ADMIN_PASSWORD;

    /**
     * Constructs the application with a configured checkout service and receipt formatter.
     */
    public GroceryCheckoutApp() {
        // Initialize discount registry with available discounts
        this.discountRegistry = new DiscountRegistry();
        discountRegistry.registerDiscount(new BuyTwoGetOneFreeDiscount(ItemCatalog.BANANAS));
        discountRegistry.registerDiscount(new BulkDiscount(
                ItemCatalog.ORANGES,
                3,
                new BigDecimal("0.75")
        ));

        this.checkoutService = new CheckoutService(discountRegistry);
        this.receiptFormatter = new ReceiptFormatter();

        LOGGER.info("{} initialized", Constants.APP_NAME);
    }

    /**
     * Runs the interactive application.
     */
    public void run() {
        LOGGER.info("Starting {}", Constants.APP_NAME);
        System.out.println("\n=== " + Constants.APP_NAME + " ===");
        printAvailableItemsAndOffers();

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
                System.out.println("6. Admin menu");
                System.out.println("7. Exit");
                System.out.print("Choose an option: ");

                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case Constants.MAIN_OPT_ADD_ITEM:
                        addItemToBasket(basket, scanner);
                        break;
                    case Constants.MAIN_OPT_REMOVE_ITEM:
                        removeItemFromBasket(basket, scanner);
                        break;
                    case Constants.MAIN_OPT_VIEW_BASKET:
                        viewBasket(basket);
                        break;
                    case Constants.MAIN_OPT_CHECKOUT:
                        checkout(basket);
                        basket.clear();
                        break;
                    case Constants.MAIN_OPT_CLEAR:
                        basket.clear();
                        System.out.println("Basket cleared.");
                        LOGGER.info("Basket cleared by user");
                        break;
                    case Constants.MAIN_OPT_ADMIN_MENU:
                        adminMenu(scanner);
                        break;
                    case Constants.MAIN_OPT_EXIT:
                        running = false;
                        System.out.println("Thank you for using Grocery Checkout System. Goodbye!");
                        LOGGER.info("Application closed by user");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                        LOGGER.warn("Invalid menu option selected: {}", choice);
                }
            }
        } catch (GroceryException e) {
            LOGGER.error("Application error: {}", e.getMessage(), e);
            System.err.println("An application error occurred: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Unexpected application error", e);
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void printAvailableItemsAndOffers() {
        System.out.println("Available items:");
        ItemCatalog.getAllItems().forEach((name, item) ->
                System.out.printf("  - %s: %s%.2f%n", item.getName(), Constants.CURRENCY_SYMBOL, item.getPricePerUnit())
        );
        System.out.println("\nSpecial Offers:");
        System.out.println("  - Bananas: Buy 2 Get 1 Free");
        System.out.println("  - Oranges: 3 for £0.75");
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
            LOGGER.warn("Invalid quantity entered", e);
        } catch (CatalogException e) {
            // Defensive: getItem may throw if itemName was null or removed concurrently
            System.out.println("Item not available: " + e.getMessage());
            LOGGER.warn("Attempted to add item failed: {}", e.getMessage());
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

        try {
            basket.removeItem(ItemCatalog.getItem(itemName));
            System.out.printf("Removed %s from basket.%n", itemName);
            LOGGER.info("Removed {} from basket", itemName);
        } catch (CatalogException e) {
            System.out.println("Item not available: " + e.getMessage());
            LOGGER.warn("Attempted to remove item failed: {}", e.getMessage());
        }
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
                System.out.printf("  %s: %d x %.2f = %.2f%n",
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

            LOGGER.info("Checkout completed - Total: {}{}", Constants.CURRENCY_SYMBOL, result.getTotal());
        } catch (GroceryException e) {
            System.out.println("Checkout error: " + e.getMessage());
            LOGGER.error("Checkout error", e);
        }
    }

    // --- Admin related methods ---
    private void adminMenu(Scanner scanner) {
        System.out.println("\n--- Admin Menu ---");

        // Simple password check before showing admin actions
        boolean authenticated = false;
        int attempts = 0;
        while (attempts < 3 && !authenticated) {
            System.out.print("Enter admin password (attempt " + (attempts + 1) + "/3): ");
            String entered = scanner.nextLine().trim();
            if (ADMIN_PASSWORD.equals(entered)) {
                authenticated = true;
            } else {
                System.out.println("Incorrect password.");
                LOGGER.warn("Failed admin login attempt {}", attempts + 1);
                attempts++;
            }
        }

        if (!authenticated) {
            System.out.println("Access denied. Returning to main menu.");
            LOGGER.warn("Admin access denied after {} attempts", attempts);
            return;
        }

        boolean back = false;
        while (!back) {
            System.out.println("1. Add catalog item");
            System.out.println("2. Remove catalog item");
            System.out.println("3. Add discount");
            System.out.println("4. Remove discounts for item");
            System.out.println("5. List discounts");
            System.out.println("6. View catalog items");
            System.out.println("7. Back");
            System.out.print("Choose an admin option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case Constants.ADMIN_OPT_ADD_CATALOG:
                    addCatalogItem(scanner);
                    break;
                case Constants.ADMIN_OPT_REMOVE_CATALOG:
                    removeCatalogItem(scanner);
                    break;
                case Constants.ADMIN_OPT_ADD_DISCOUNT:
                    addDiscount(scanner);
                    break;
                case Constants.ADMIN_OPT_REMOVE_DISCOUNTS:
                    removeDiscountsForItem(scanner);
                    break;
                case Constants.ADMIN_OPT_LIST_DISCOUNTS:
                    listDiscounts();
                    break;
                case Constants.ADMIN_OPT_VIEW_CATALOG:
                    listCatalogItems();
                    break;
                case Constants.ADMIN_OPT_BACK:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid admin option. Please try again.");
            }
        }
    }

    private void addCatalogItem(Scanner scanner) {
        try {
            System.out.print("Enter new item name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter price (e.g. 0.99): ");
            String priceStr = scanner.nextLine().trim();
            BigDecimal price = new BigDecimal(priceStr);
            Item item = new Item(name, price);
            ItemCatalog.addItem(item);
            System.out.println("Item added: " + item.getName());
            LOGGER.info("Admin added catalog item: {} @ {}", item.getName(), item.getPricePerUnit());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
            LOGGER.warn("Invalid admin input when adding item", e);
        } catch (CatalogException e) {
            System.out.println("Cannot add item: " + e.getMessage());
            LOGGER.warn("Failed to add catalog item: {}", e.getMessage());
        }
    }

    private void removeCatalogItem(Scanner scanner) {
        System.out.print("Enter item name to remove: ");
        String name = scanner.nextLine().trim();
        try {
            Item removed = ItemCatalog.removeItem(name);
            System.out.println("Removed item: " + removed.getName());
            LOGGER.info("Admin removed catalog item: {}", removed.getName());
            // Also remove any discounts that reference this item
            int removedDiscounts = discountRegistry.unregisterDiscountsForItem(removed.getName());
            if (removedDiscounts > 0) {
                System.out.println("Also removed " + removedDiscounts + " discounts associated with the item.");
                LOGGER.info("Removed {} discounts for deleted item {}", removedDiscounts, removed.getName());
            }
        } catch (CatalogException e) {
            System.out.println("Cannot remove item: " + e.getMessage());
            LOGGER.warn("Failed to remove catalog item: {}", e.getMessage());
        }
    }

    private void addDiscount(Scanner scanner) {
        System.out.println("Select discount type:");
        System.out.println("1. Buy 2 Get 1 Free");
        System.out.println("2. Bulk (X for £Y)");
        System.out.print("Choice: ");
        String type = scanner.nextLine().trim();

        try {
            System.out.print("Enter item name the discount applies to: ");
            String itemName = scanner.nextLine().trim();
            if (!ItemCatalog.containsItem(itemName)) {
                System.out.println("Item not found in catalog.");
                return;
            }
            Item item = ItemCatalog.getItem(itemName);

            switch (type) {
                case Constants.DISCOUNT_TYPE_BOGO:
                    discountRegistry.registerDiscount(new BuyTwoGetOneFreeDiscount(item));
                    System.out.println("Buy 2 Get 1 Free discount added for " + item.getName());
                    LOGGER.info("Admin added BuyTwoGetOneFreeDiscount for {}", item.getName());
                    break;
                case Constants.DISCOUNT_TYPE_BULK:
                    System.out.print("Enter item count required for bulk (e.g. 3): ");
                    int count = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter total price for the group (e.g. 0.75): ");
                    BigDecimal groupPrice = new BigDecimal(scanner.nextLine().trim());
                    discountRegistry.registerDiscount(new BulkDiscount(item, count, groupPrice));
                    System.out.println("Bulk discount added for " + item.getName());
                    LOGGER.info("Admin added BulkDiscount for {}: {} for £{}", item.getName(), count, groupPrice);
                    break;
                default:
                    System.out.println("Unknown discount type selected.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
            LOGGER.warn("Invalid admin input when adding discount", e);
        } catch (CatalogException e) {
            System.out.println("Item error: " + e.getMessage());
            LOGGER.warn("Failed to add discount - item error: {}", e.getMessage());
        }
    }

    private void removeDiscountsForItem(Scanner scanner) {
        System.out.print("Enter item name to remove discounts for: ");
        String itemName = scanner.nextLine().trim();
        int removed = discountRegistry.unregisterDiscountsForItem(itemName);
        System.out.println("Removed " + removed + " discount(s) for item: " + itemName);
        LOGGER.info("Admin removed {} discounts for item {}", removed, itemName);
    }

    /**
     * Lists all catalog items to the admin console.
     */
    private void listCatalogItems() {
        System.out.println("Catalog items:");
        try {
            ItemCatalog.getAllItems().forEach((key, item) ->
                    System.out.printf(" - %s: %s%.2f%n", item.getName(), Constants.CURRENCY_SYMBOL, item.getPricePerUnit())
            );
        } catch (Exception e) {
            System.out.println("Failed to list catalog items: " + e.getMessage());
            LOGGER.warn("Failed to list catalog items", e);
        }
    }

    private void listDiscounts() {
        List<com.grocery.discount.Discount> discounts = discountRegistry.getDiscounts();
        if (discounts.isEmpty()) {
            System.out.println("No discounts configured.");
            return;
        }
        System.out.println("Configured discounts:");
        for (com.grocery.discount.Discount d : discounts) {
            System.out.printf(" - %s: %s\n", d.getItem().getName(), d.getDiscountName());
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
