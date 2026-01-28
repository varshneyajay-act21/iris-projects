# Grocery Store Checkout System

A comprehensive Java application for processing grocery store checkouts with support for special offers and itemized receipts.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [Architecture](#architecture)
- [Discounts](#discounts)
- [Testing](#testing)
- [Project Structure](#project-structure)

## Overview

This project implements a complete grocery store checkout system that calculates the total cost of a shopping basket, applies special promotional offers, and generates itemized receipts. The system is built with clean architecture principles, comprehensive logging, and extensive test coverage.

## Features

- **Item Management**: Support for 5 different grocery items with predefined prices
  - Bananas: £0.50 each
  - Oranges: £0.30 each
  - Apples: £0.60 each
  - Lemons: £0.25 each
  - Peaches: £0.75 each

- **Special Offers**:
  - **Bananas**: Buy 2 Get 1 Free (For every 3 bananas, you get 1 free)
  - **Oranges**: 3 for £0.75 (Save compared to buying individually)

- **Receipt Generation**: Detailed itemized receipts showing:
  - Each item with quantity and price
  - Individual discounts applied
  - Subtotal before discounts
  - Total discount amount
  - Final total

- **Error Handling**: Comprehensive validation and error handling throughout
- **Logging**: Detailed logging at appropriate levels (INFO, DEBUG, WARN, ERROR)
- **Testing**: Unit and integration tests with high code coverage

## Requirements

- **Java**: 11 or higher
- **Maven**: 3.6.0 or higher

## Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/grocery-store-checkout-system.git
   cd grocery-store-checkout-system
   ```

2. **Build the project**:
   ```bash
   mvn clean install
   ```

3. **Run tests**:
   ```bash
   mvn test
   ```

4. **Run the application**:
   ```bash
   mvn exec:java -Dexec.mainClass="com.grocery.GroceryCheckoutApp"
   ```

   Or if the JAR is built:
   ```bash
   java -jar target/grocery-store-checkout-system-1.0.0.jar
   ```

## Usage

### Interactive Mode

Run the application to start the interactive menu:

```bash
mvn exec:java -Dexec.mainClass="com.grocery.GroceryCheckoutApp"
```

The menu provides the following options:
1. **Add item to basket** - Add items with desired quantity
2. **Remove item from basket** - Remove items by name
3. **View basket** - Display current basket contents
4. **Checkout** - Process the checkout and display receipt
5. **Clear basket** - Empty the basket
6. **Exit** - Close the application

### Example Interaction

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
...
Choose an option: 1
Enter item name: Bananas
Enter quantity: 3
Added 3 Bananas to basket.
```

### Programmatic Usage

```java
// Create discount registry and register discounts
DiscountRegistry discountRegistry = new DiscountRegistry();
discountRegistry.registerDiscount(new BuyTwoGetOneFreeDiscount(ItemCatalog.BANANAS));
discountRegistry.registerDiscount(new BulkDiscountDiscount(
    ItemCatalog.ORANGES,
    3,
    new BigDecimal("0.75")
));

// Create checkout service
CheckoutService checkoutService = new CheckoutService(discountRegistry);

// Create basket and add items
Basket basket = new Basket();
basket.addItem(ItemCatalog.BANANAS, 3);
basket.addItem(ItemCatalog.ORANGES, 4);

// Process checkout
CheckoutResult result = checkoutService.processCheckout(basket);

// Generate receipt
ReceiptFormatter formatter = new ReceiptFormatter();
List<ReceiptItem> items = new ArrayList<>();
items.add(new ReceiptItem("Bananas", 3, ItemCatalog.BANANAS.getPricePerUnit()));
items.add(new ReceiptItem("Oranges", 4, ItemCatalog.ORANGES.getPricePerUnit()));

String receipt = formatter.formatReceiptFromResult(result, items);
System.out.println(receipt);
```

## Architecture

The system follows clean architecture principles with clear separation of concerns:

### Core Packages

- **`com.grocery.model`**: Domain models
  - `Item`: Represents a grocery item
  - `Basket`: Shopping basket container
  - `BasketItem`: Item with quantity

- **`com.grocery.catalog`**: Item catalog
  - `ItemCatalog`: Centralized item definitions and prices

- **`com.grocery.discount`**: Discount strategies
  - `Discount`: Abstract discount base class
  - `BuyTwoGetOneFreeDiscount`: Implementation for BOGO offers
  - `BulkDiscountDiscount`: Implementation for bulk/multi-buy offers
  - `DiscountRegistry`: Registry for managing discounts
  - `DiscountResult`: Discount calculation result

- **`com.grocery.service`**: Business logic
  - `CheckoutService`: Orchestrates checkout calculations
  - `CheckoutResult`: Encapsulates checkout results

- **`com.grocery.receipt`**: Receipt formatting
  - `ReceiptFormatter`: Generates formatted receipts
  - `BasketSummary`: Summary of basket items
  - `ReceiptItem`: Single receipt line item

### Design Patterns

- **Strategy Pattern**: Discount implementations
- **Registry Pattern**: Discount management
- **Immutability**: Domain objects are immutable for thread safety
- **Dependency Injection**: Services receive dependencies via constructor

## Discounts

### Buy 2 Get 1 Free (Bananas)

For every 3 items purchased, the 3rd one is free.

Example:
- 3 Bananas @ £0.50 = £1.50
- Discount: 1 free banana = -£0.50
- Total: £1.00

Calculation:
```
freeItems = quantity / 3
discountAmount = freeItems * unitPrice
```

### 3 for £0.75 (Oranges)

When you buy 3 or more oranges, the price is £0.75 per group of 3.

Example:
- 4 Oranges @ £0.30 = £1.20
- Cost with discount: (3 * £0.75) + (1 * £0.30) = £1.05
- Discount: £1.20 - £1.05 = £0.15
- Total: £1.05

Calculation:
```
groups = quantity / 3
remainder = quantity % 3
discountedCost = (groups * discountPrice) + (remainder * unitPrice)
discountAmount = (quantity * unitPrice) - discountedCost
```

## Testing

The project includes comprehensive unit and integration tests.

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=CheckoutServiceTest
```

### Test Coverage

- **Unit Tests**:
  - `ItemTest`: Item creation and validation
  - `BasketTest`: Basket operations
  - `BuyTwoGetOneFreeDiscountTest`: BOGO discount calculations
  - `BulkDiscountDiscountTest`: Bulk discount calculations
  - `CheckoutServiceTest`: Checkout processing
  - `ReceiptFormatterTest`: Receipt generation

- **Integration Tests**:
  - `CheckoutIntegrationTest`: End-to-end checkout flows
  - Example scenarios from requirements
  - Multiple discount combinations

### Test Examples

```java
// Unit test example
@Test
void testCheckoutWithBuyTwoGetOneFreeBananas() {
    Basket basket = new Basket();
    basket.addItem(banana, 3);
    
    CheckoutResult result = checkoutService.processCheckout(basket);
    assertEquals(new BigDecimal("1.50"), result.getSubtotal());
    assertEquals(new BigDecimal("0.50"), result.getTotalDiscountAmount());
    assertEquals(new BigDecimal("1.00"), result.getTotal());
}
```

## Test Coverage (JaCoCo)

This project is configured to generate code coverage reports using JaCoCo.

- Generate coverage report (HTML + XML):

```bash
# Run tests and create the report (HTML + XML)
mvn jacoco:report
# Or run the full build and report (report is generated in verify phase):
mvn verify
```

- Open the HTML report (macOS):

```bash
open target/site/jacoco/index.html
```

- Files produced:
  - `target/site/jacoco/index.html` — browsable HTML report
  - `target/site/jacoco/jacoco.xml` — XML coverage report for CI tools (Codecov, SonarQube)
  - `target/jacoco.exec` — JaCoCo execution data (binary)

- CI integration notes:
  - Upload `target/site/jacoco/jacoco.xml` to coverage services (Codecov, Coveralls) or point SonarQube at it.
  - To fail builds on low coverage, enable JaCoCo's `check` goal with thresholds (see JaCoCo docs). I can add a sample `check` configuration if you'd like.

## Project Structure

```
grocery-store-checkout-system/
├── pom.xml                                          # Maven configuration
├── README.md                                        # This file
├── .gitignore                                       # Git ignore rules
└── src/
    ├── main/java/com/grocery/
    │   ├── GroceryCheckoutApp.java                 # Main application entry point
    │   ├── model/
    │   │   ├── Item.java                           # Item domain model
    │   │   ├── Basket.java                         # Basket container
    │   │   └── BasketItem.java                     # Basket line item
    │   ├── catalog/
    │   │   └── ItemCatalog.java                    # Item definitions
    │   ├── discount/
    │   │   ├── Discount.java                       # Discount abstract class
    │   │   ├── BuyTwoGetOneFreeDiscount.java       # BOGO implementation
    │   │   ├── BulkDiscountDiscount.java           # Bulk discount implementation
    │   │   ├── DiscountRegistry.java               # Discount registry
    │   │   └── DiscountResult.java                 # Discount result DTO
    │   ├── service/
    │   │   ├── CheckoutService.java                # Checkout processor
    │   │   └── CheckoutResult.java                 # Checkout result DTO
    │   └── receipt/
    │       ├── ReceiptFormatter.java               # Receipt generator
    │       ├── BasketSummary.java                  # Basket summary DTO
    │       └── ReceiptItem.java                    # Receipt line item
    └── test/java/com/grocery/
        ├── model/
        │   ├── ItemTest.java
        │   └── BasketTest.java
        ├── discount/
        │   ├── BuyTwoGetOneFreeDiscountTest.java
        │   └── BulkDiscountDiscountTest.java
        ├── service/
        │   └── CheckoutServiceTest.java
        ├── receipt/
        │   └── ReceiptFormatterTest.java
        └── integration/
            └── CheckoutIntegrationTest.java
```

## Logging

The application uses SLF4J with Logback for logging. Configuration can be customized in `src/main/resources/logback.xml`.

### Log Levels

- **INFO**: Checkout operations, user actions
- **DEBUG**: Discount calculations, detailed operations
- **WARN**: Invalid inputs, edge cases
- **ERROR**: Failures, exceptions

## Dependencies

- **Logging**: SLF4J 2.0.11, Logback 1.4.14
- **Testing**: JUnit 5 (Jupiter), Mockito 5.7.0
- **Build**: Maven 3.11.0, Java Compiler 11

All dependencies are specified in `pom.xml` with fixed versions for stability and security.

## Error Handling

The system validates inputs and provides meaningful error messages:

- **Null Checks**: All public methods validate null inputs
- **Quantity Validation**: Quantities must be positive
- **Price Validation**: Prices cannot be negative
- **Basket Validation**: Cannot checkout with empty basket
- **Item Validation**: Items must exist in catalog

## Security Considerations

- **BigDecimal for Currency**: Avoids floating-point precision issues
- **Immutable Objects**: Domain objects are immutable, preventing accidental modifications
- **Null Safety**: Comprehensive null checking using Objects.requireNonNull()
- **Input Validation**: All inputs are validated before processing
- **Thread Safety**: Design allows safe concurrent use

## Future Enhancements

- Database persistence for transactions
- User authentication and account management
- Payment processing integration
- Inventory management
- Customer loyalty programs
- Admin interface for discount management
- Performance metrics and analytics

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Write tests for new features
5. Submit a pull request

## Generated files

Some build plugins produce generated files that should not be committed to source control. One example in this project is `dependency-reduced-pom.xml`, which is produced by the Maven Shade plugin when creating a shaded (uber) JAR.

- Purpose: the file records a POM with dependencies that the Shade plugin has packaged into the shaded JAR. It is generated automatically during packaging and is not part of your source.
- Action: The project `.gitignore` already contains an entry to ignore `/dependency-reduced-pom.xml` so the generated file won't be tracked.

If `dependency-reduced-pom.xml` has already been committed to the repository and you want to remove it from history, see the "Removing a generated file from Git history" instructions below.

## License

This project is provided as-is for educational purposes.

## Contact

For questions or issues, please contact the development team.

---

**Version**: 1.0.0  
**Last Updated**: January 2026
