package com.grocery;

import com.grocery.catalog.ItemCatalog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class GroceryCheckoutAppAdminTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void listCatalogItems_printsKnownItems() throws Exception {
        // Ensure catalog has at least default items
        assertTrue(ItemCatalog.containsItem("Bananas"));

        GroceryCheckoutApp app = new GroceryCheckoutApp();

        // Use reflection to call private method listCatalogItems
        Method m = GroceryCheckoutApp.class.getDeclaredMethod("listCatalogItems");
        m.setAccessible(true);
        m.invoke(app);

        String output = outContent.toString();
        assertTrue(output.contains("Catalog items:"));
        assertTrue(output.toLowerCase().contains("bananas"));
        assertTrue(output.toLowerCase().contains("oranges"));
    }
}
