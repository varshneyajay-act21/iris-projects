# Quick Start Guide

## Prerequisites
- Java 11 or higher
- Maven 3.6.0 or higher
- Git (optional, for cloning)

## Setup

### 1. Build the Project
```bash
cd grocery-store-checkout-system
mvn clean install
```

### 2. Run Tests
```bash
mvn test
```

Expected output: `Tests run: 76, Failures: 0, Errors: 0`

### 3. Run the Application
```bash
mvn exec:java -Dexec.mainClass="com.grocery.GroceryCheckoutApp"
```

## Example Usage

### Interactive Session

```
=== Grocery Store Checkout System ===
Available items:
  - Bananas: £0.50
  - Oranges: £0.30
  - Apples: £0.60
  - Lemons: £0.25
  - Peaches: £0.75

Special Offers:
  - Bananas: Buy 2 Get 1 Free
  - Oranges: 3 for £0.75

--- Options ---
1. Add item to basket
2. Remove item from basket
3. View basket
4. Checkout
5. Clear basket
6. Exit
Choose an option: 1
Enter item name: Bananas
Enter quantity: 3
Added 3 Bananas to basket.

--- Options ---
...
Choose an option: 1
Enter item name: Oranges
Enter quantity: 4
Added 4 Oranges to basket.

--- Options ---
...
Choose an option: 4

=====================================
GROCERY STORE CHECKOUT RECEIPT
=====================================
Timestamp: 2026-01-22 22:47:30

Item               Quantity         Price
=====================================
Bananas            3                £1.50
Oranges            4                £1.20
=====================================
Subtotal:                           £2.70

Discounts:
-------------------------------------
Bananas            (Buy 2 Get 1 Free)    -£0.50
Oranges            (3 for £0.75)        -£0.15
=====================================
Total Discount:                     £0.65

=====================================
TOTAL:                              £2.05
=====================================
```

## Programmatic Usage

```java
import com.grocery.catalog.ItemCatalog;
import com.grocery.discount.*;
import com.grocery.model.Basket;
import com.grocery.receipt.ReceiptFormatter;
import com.grocery.receipt.ReceiptItem;
import com.grocery.service.CheckoutService;
import com.grocery.service.CheckoutResult;
import java.math.BigDecimal;
import java.util.*;

public class Example {
    public static void main(String[] args) {
        // Setup discounts
        DiscountRegistry discountRegistry = new DiscountRegistry();
        discountRegistry.registerDiscount(
            new BuyTwoGetOneFreeDiscount(ItemCatalog.BANANAS)
        );
        discountRegistry.registerDiscount(
            new BulkDiscountDiscount(
                ItemCatalog.ORANGES,
                3,
                new BigDecimal("0.75")
            )
        );

        // Create service
        CheckoutService checkoutService = new CheckoutService(discountRegistry);

        // Create basket
        Basket basket = new Basket();
        basket.addItem(ItemCatalog.BANANAS, 3);
        basket.addItem(ItemCatalog.ORANGES, 4);

        // Process checkout
        CheckoutResult result = checkoutService.processCheckout(basket);

        // Print results
        System.out.println("Subtotal: £" + result.getSubtotal());
        System.out.println("Discount: £" + result.getTotalDiscountAmount());
        System.out.println("Total: £" + result.getTotal());

        // Generate receipt
        ReceiptFormatter formatter = new ReceiptFormatter();
        List<ReceiptItem> items = new ArrayList<>();
        basket.getItems().forEach(item ->
            items.add(new ReceiptItem(
                item.getItem().getName(),
                item.getQuantity(),
                item.getItem().getPricePerUnit()
            ))
        );

        String receipt = formatter.formatReceiptFromResult(result, items);
        System.out.println(receipt);
    }
}
```

## Test Results

Run the full test suite:
```bash
mvn test
```

### Test Summary
- **Total Tests**: 76
- **Unit Tests**: 
  - ItemTest: 9 tests
  - BasketTest: 11 tests
  - BuyTwoGetOneFreeDiscountTest: 8 tests
  - BulkDiscountDiscountTest: 8 tests
  - CheckoutServiceTest: 9 tests
  - ReceiptFormatterTest: 10 tests
  
- **Integration Tests**:
  - CheckoutIntegrationTest: 13 tests

### Running Specific Tests
```bash
# Run a specific test class
mvn test -Dtest=CheckoutServiceTest

# Run a specific test method
mvn test -Dtest=CheckoutServiceTest#testCheckoutWithBuyTwoGetOneFreeBananas

# Run tests matching a pattern
mvn test -Dtest=*Integration*
```

## Logging

Logs are written to:
- **Console**: Real-time output
- **File**: `logs/grocery-checkout.log`

Log levels:
- INFO: Checkout operations, user actions
- DEBUG: Discount calculations (useful for debugging)
- WARN: Invalid inputs
- ERROR: System errors

## Project Structure

```
src/main/java/com/grocery/
├── GroceryCheckoutApp.java       # Main entry point
├── model/                        # Domain models
│   ├── Item.java
│   ├── Basket.java
│   └── BasketItem.java
├── catalog/                      # Item catalog
│   └── ItemCatalog.java
├── discount/                     # Discount strategies
│   ├── Discount.java
│   ├── BuyTwoGetOneFreeDiscount.java
│   ├── BulkDiscountDiscount.java
│   ├── DiscountRegistry.java
│   └── DiscountResult.java
├── service/                      # Business logic
│   ├── CheckoutService.java
│   └── CheckoutResult.java
└── receipt/                      # Receipt formatting
    ├── ReceiptFormatter.java
    ├── BasketSummary.java
    └── ReceiptItem.java
```

## Building JAR

Create an executable JAR:
```bash
mvn clean package
java -jar target/grocery-store-checkout-system-1.0.0.jar
```

## Troubleshooting

### Build Issues
```bash
# Clean and rebuild
mvn clean install

# Check Java version
java -version

# Update Maven
mvn --version
```

### Test Issues
```bash
# Run with verbose output
mvn test -X

# Skip tests
mvn clean install -DskipTests
```

### Logging Issues
- Check `logs/` directory for log files
- Verify `src/main/resources/logback.xml` configuration
- Ensure `logs/` directory has write permissions

## Documentation

- **README.md**: Complete project documentation
- **Code Documentation**: JavaDoc comments on all public methods
- **Test Examples**: See test classes for usage examples

## Next Steps

1. Review the `README.md` for detailed documentation
2. Examine unit tests for usage examples
3. Check discount implementations for custom offer strategies
4. Extend with additional features as needed

---

For more information, see `README.md` in the project root.
