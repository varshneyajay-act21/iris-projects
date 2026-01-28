package com.grocery.config;

/**
 * Central place for application-wide constants. Keep secrets out of source control.
 * - Admin password is read from the system property `ADMIN_PASSWORD` with a safe default.
 * Add other reuseable constants here so they can be referenced across the codebase.
 */
public final class Constants {
    private Constants() { throw new AssertionError("No instances"); }

    /**
     * Admin password. Can be overridden with -DADMIN_PASSWORD=yourpw when launching the JVM.
     * Kept in a single place for reuse and easier future secure handling (e.g., environment or vault).
     */
    public static final String ADMIN_PASSWORD = System.getProperty("ADMIN_PASSWORD", "admin");

    /** Application display name. */
    public static final String APP_NAME = "Grocery Store Checkout System";

    /** Currency symbol used for display. */
    public static final String CURRENCY_SYMBOL = "\u00A3"; // £

    // --- Main menu option codes (String) ---
    public static final String MAIN_OPT_ADD_ITEM = "1";
    public static final String MAIN_OPT_REMOVE_ITEM = "2";
    public static final String MAIN_OPT_VIEW_BASKET = "3";
    public static final String MAIN_OPT_CHECKOUT = "4";
    public static final String MAIN_OPT_CLEAR = "5";
    public static final String MAIN_OPT_ADMIN_MENU = "6";
    public static final String MAIN_OPT_EXIT = "7";

    // --- Admin menu option codes (String) ---
    public static final String ADMIN_OPT_ADD_CATALOG = "1";
    public static final String ADMIN_OPT_REMOVE_CATALOG = "2";
    public static final String ADMIN_OPT_ADD_DISCOUNT = "3";
    public static final String ADMIN_OPT_REMOVE_DISCOUNTS = "4";
    public static final String ADMIN_OPT_LIST_DISCOUNTS = "5";
    public static final String ADMIN_OPT_VIEW_CATALOG = "6";
    public static final String ADMIN_OPT_BACK = "7";

    // Discount type codes used in admin addDiscount menu
    public static final String DISCOUNT_TYPE_BOGO = "1"; // Buy 2 Get 1 Free
    public static final String DISCOUNT_TYPE_BULK = "2"; // Bulk price
}
