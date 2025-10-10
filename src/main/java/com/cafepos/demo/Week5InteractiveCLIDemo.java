package com.cafepos.demo;

import com.cafepos.catalog.Catalog;
import com.cafepos.catalog.InMemoryCatalog;
import com.cafepos.common.Money;
import com.cafepos.common.Product;
import com.cafepos.common.SimpleProduct;
import com.cafepos.common.Priced;
import com.cafepos.decorator.*;
import com.cafepos.domain.*;
import com.cafepos.factory.ProductFactory;
import com.cafepos.payment.*;

import java.util.Scanner;

/**
 * Interactive Cafe POS System
 * Demonstrates product customization, recipe-based ordering, and order management
 */
public final class Week5InteractiveCLIDemo {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ProductFactory factory = new ProductFactory();
    private static Order currentOrder;
    private static int orderCounter = 1000;

    public static void main(String[] args) {
        printWelcomeBanner();
        initializeNewOrder();

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> addProductToOrder();
                case "2" -> addCustomProductToOrder();
                case "3" -> useRecipeBuilder();
                case "4" -> viewCurrentOrder();
                case "5" -> checkout();
                case "6" -> showCustomizationGuide();
                case "7" -> showRecipeGuide();
                case "8" -> {
                    running = false;
                    System.out.println("\nThank you for visiting! Have a great day!\n");
                }
                default -> System.out.println("Invalid choice. Please try again.\n");
            }
        }

        scanner.close();
    }

    private static void printWelcomeBanner() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("             CAFE POS SYSTEM");
        System.out.println("=".repeat(60));
        System.out.println("Features:");
        System.out.println("  - Multiple payment methods");
        System.out.println("  - Real-time order notifications");
        System.out.println("  - Product customization");
        System.out.println("  - Quick recipe-based ordering");
        System.out.println("=".repeat(60) + "\n");
    }

    private static void initializeNewOrder() {
        currentOrder = new Order(++orderCounter);

        // register system components to receive order updates
        currentOrder.register(new KitchenDisplay());
        currentOrder.register(new DeliveryDesk());
        currentOrder.register(new CustomerNotifier());

        System.out.println("New order created: Order #" + currentOrder.id());
        System.out.println("System ready: Kitchen, Delivery, Customer notifications active\n");
    }

    private static void printMainMenu() {
        System.out.println("+---------------------------------------------------------+");
        System.out.println("|                    MAIN MENU                            |");
        System.out.println("+---------------------------------------------------------+");
        System.out.println("| 1. Add Standard Product                                 |");
        System.out.println("| 2. Build Custom Product                                 |");
        System.out.println("| 3. Quick Order (Recipe Code)                            |");
        System.out.println("| 4. View Current Order                                   |");
        System.out.println("| 5. Checkout & Pay                                       |");
        System.out.println("| 6. Customization Guide                                  |");
        System.out.println("| 7. Recipe Code Reference                                |");
        System.out.println("| 8. Exit                                                 |");
        System.out.println("+---------------------------------------------------------+");
        System.out.print("Choose an option: ");
    }

    private static void addProductToOrder() {
        System.out.println("\nAVAILABLE PRODUCTS:");
        System.out.println("  1. Espresso     - EUR 2.50");
        System.out.println("  2. Latte        - EUR 3.20");
        System.out.println("  3. Cappuccino   - EUR 3.00");
        System.out.print("\nSelect product (1-3): ");

        String choice = scanner.nextLine().trim();
        Product product = switch (choice) {
            case "1" -> new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
            case "2" -> new SimpleProduct("P-LAT", "Latte", Money.of(3.20));
            case "3" -> new SimpleProduct("P-CAP", "Cappuccino", Money.of(3.00));
            default -> null;
        };

        if (product == null) {
            System.out.println("Invalid selection.\n");
            return;
        }

        System.out.print("Quantity: ");
        int quantity = readInt();

        currentOrder.addItem(new LineItem(product, quantity));
        System.out.println("Added: " + product.name() + " x" + quantity + "\n");
    }

    private static void addCustomProductToOrder() {
        System.out.println("\nCUSTOM PRODUCT BUILDER");
        System.out.println("=".repeat(50));

        // choose base product
        System.out.println("\nSTEP 1: Choose base product");
        System.out.println("  1. Espresso     - EUR 2.50");
        System.out.println("  2. Latte        - EUR 3.20");
        System.out.println("  3. Cappuccino   - EUR 3.00");
        System.out.print("Select: ");

        String choice = scanner.nextLine().trim();
        Product product = switch (choice) {
            case "1" -> new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
            case "2" -> new SimpleProduct("P-LAT", "Latte", Money.of(3.20));
            case "3" -> new SimpleProduct("P-CAP", "Cappuccino", Money.of(3.00));
            default -> null;
        };

        if (product == null) {
            System.out.println("Invalid selection.\n");
            return;
        }

        System.out.println("\nBase: " + product.name() + " - EUR " + product.basePrice());

        // add customizations
        System.out.println("\nSTEP 2: Add customizations");
        System.out.println("Choose add-ons (you can select multiple):");

        boolean addingCustomizations = true;
        while (addingCustomizations) {
            System.out.println("\n  1. Extra Shot   (+EUR 0.80)");
            System.out.println("  2. Oat Milk     (+EUR 0.50)");
            System.out.println("  3. Syrup        (+EUR 0.40)");
            System.out.println("  4. Large Size   (+EUR 0.70)");
            System.out.println("  5. Done - Add to order");
            System.out.print("Select: ");

            String addon = scanner.nextLine().trim();
            switch (addon) {
                case "1" -> {
                    product = new ExtraShot(product);
                    System.out.println("Added: Extra Shot");
                }
                case "2" -> {
                    product = new OatMilk(product);
                    System.out.println("Added: Oat Milk");
                }
                case "3" -> {
                    product = new Syrup(product);
                    System.out.println("Added: Syrup");
                }
                case "4" -> {
                    product = new SizeLarge(product);
                    System.out.println("Added: Large Size");
                }
                case "5" -> addingCustomizations = false;
                default -> System.out.println("Invalid choice");
            }

            if (product instanceof Priced priced) {
                System.out.println("Current price: EUR " + priced.price());
            }
        }

        System.out.println("\nFinal product: " + product.name());
        if (product instanceof Priced priced) {
            System.out.println("Final price: EUR " + priced.price());
        }

        System.out.print("\nQuantity: ");
        int quantity = readInt();

        currentOrder.addItem(new LineItem(product, quantity));
        System.out.println("\nAdded to order!\n");
    }

    private static void useRecipeBuilder() {
        System.out.println("\nQUICK ORDER - RECIPE CODE");
        System.out.println("=".repeat(50));
        System.out.println("Use recipe codes for fast ordering!");
        System.out.println("\nFORMAT: BASE+ADDON1+ADDON2+...");
        System.out.println("\nBase Products:");
        System.out.println("  ESP - Espresso (EUR 2.50)");
        System.out.println("  LAT - Latte (EUR 3.20)");
        System.out.println("  CAP - Cappuccino (EUR 3.00)");
        System.out.println("\nAdd-ons:");
        System.out.println("  SHOT - Extra Shot (+EUR 0.80)");
        System.out.println("  OAT  - Oat Milk (+EUR 0.50)");
        System.out.println("  SYP  - Syrup (+EUR 0.40)");
        System.out.println("  L    - Large Size (+EUR 0.70)");
        System.out.println("\nExamples:");
        System.out.println("  ESP+SHOT          -> Espresso with Extra Shot");
        System.out.println("  LAT+OAT+L         -> Large Latte with Oat Milk");
        System.out.println("  CAP+SHOT+SYP+L    -> Large Cappuccino with Extra Shot and Syrup");

        System.out.print("\nEnter your recipe: ");
        String recipe = scanner.nextLine().trim();

        try {
            Product product = factory.create(recipe);
            System.out.println("\nCreated: " + product.name());

            if (product instanceof Priced priced) {
                System.out.println("Price: EUR " + priced.price());
            } else {
                System.out.println("Base Price: EUR " + product.basePrice());
            }

            System.out.print("\nQuantity: ");
            int quantity = readInt();

            currentOrder.addItem(new LineItem(product, quantity));
            System.out.println("\nAdded to order!\n");

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Please check your recipe format.\n");
        }
    }

    private static void viewCurrentOrder() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("CURRENT ORDER #" + currentOrder.id());
        System.out.println("=".repeat(60));

        if (currentOrder.items().isEmpty()) {
            System.out.println("Order is empty. Add some items!\n");
            return;
        }

        System.out.println("\nITEMS:");
        int itemNum = 1;
        for (LineItem item : currentOrder.items()) {
            Product p = item.product();
            Money lineTotal = item.lineTotal();
            System.out.printf("%d. %s x%d = EUR %s%n",
                    itemNum++, p.name(), item.quantity(), lineTotal);
        }

        System.out.println("\n" + "-".repeat(60));
        System.out.println("Subtotal:        EUR " + currentOrder.subtotal());
        System.out.println("Tax (10%):       EUR " + currentOrder.taxAtPercent(10));
        System.out.println("TOTAL:           EUR " + currentOrder.totalWithTax(10));
        System.out.println("=".repeat(60) + "\n");
    }

    private static void checkout() {
        if (currentOrder.items().isEmpty()) {
            System.out.println("\nCannot checkout - order is empty!\n");
            return;
        }

        viewCurrentOrder();

        System.out.println("\nPAYMENT METHOD");
        System.out.println("  1. Cash Payment");
        System.out.println("  2. Card Payment");
        System.out.println("  3. Wallet Payment");
        System.out.print("\nSelect payment method: ");

        String choice = scanner.nextLine().trim();
        PaymentStrategy strategy = switch (choice) {
            case "1" -> new CashPayment();
            case "2" -> {
                System.out.print("Enter card number: ");
                String cardNum = scanner.nextLine().trim();
                yield new CardPayment(cardNum);
            }
            case "3" -> {
                System.out.print("Enter wallet ID: ");
                String walletId = scanner.nextLine().trim();
                yield new WalletPayment(walletId);
            }
            default -> null;
        };

        if (strategy == null) {
            System.out.println("Invalid payment method.\n");
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("PROCESSING PAYMENT...");
        System.out.println("=".repeat(60) + "\n");

        // process payment and notify observers
        currentOrder.pay(strategy);

        System.out.println("\nPayment successful!");

        // mark order as ready
        System.out.println("\nPreparing order for delivery...\n");
        currentOrder.markReady();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Order #" + currentOrder.id() + " COMPLETE!");
        System.out.println("=".repeat(60) + "\n");

        // start a new order
        System.out.println("Starting new order...\n");
        initializeNewOrder();
    }

    private static void showCustomizationGuide() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PRODUCT CUSTOMIZATION GUIDE");
        System.out.println("=".repeat(60));
        System.out.println("\nOur system allows you to customize any base product with");
        System.out.println("multiple add-ons, each applied individually to your order.\n");

        System.out.println("> Creating base Espresso (EUR 2.50)...");
        Product espresso = new SimpleProduct("P-ESP", "Espresso", Money.of(2.50));
        System.out.println("  Product: " + espresso.name());
        System.out.println("  Base Price: EUR " + espresso.basePrice());

        System.out.println("\n> Adding Extra Shot (+EUR 0.80)...");
        Product withShot = new ExtraShot(espresso);
        System.out.println("  Product: " + withShot.name());
        System.out.println("  Price: EUR " + ((Priced) withShot).price());

        System.out.println("\n> Adding Oat Milk (+EUR 0.50)...");
        Product withOat = new OatMilk(withShot);
        System.out.println("  Product: " + withOat.name());
        System.out.println("  Price: EUR " + ((Priced) withOat).price());

        System.out.println("\n> Upgrading to Large Size (+EUR 0.70)...");
        Product largeFinal = new SizeLarge(withOat);
        System.out.println("  Product: " + largeFinal.name());
        System.out.println("  Price: EUR " + ((Priced) largeFinal).price());

        System.out.println("\nFINAL RESULT:");
        System.out.println("  Product: " + largeFinal.name());
        System.out.println("  Total Price: EUR " + ((Priced) largeFinal).price());
        System.out.println("  Price Breakdown: 2.50 + 0.80 + 0.50 + 0.70 = 4.50");

        System.out.println("\nKEY FEATURES:");
        System.out.println("  - Customizations can be combined in any order");
        System.out.println("  - Each add-on is tracked separately");
        System.out.println("  - Price updates automatically");
        System.out.println("  - Easy to add or remove options");

        System.out.println("\n" + "=".repeat(60) + "\n");
    }

    private static void showRecipeGuide() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("RECIPE CODE REFERENCE");
        System.out.println("=".repeat(60));
        System.out.println("\nRecipe codes allow you to quickly create customized products");
        System.out.println("using a simple text-based format.\n");

        String[] recipes = {
                "ESP",
                "ESP+SHOT",
                "LAT+OAT+L",
                "CAP+SHOT+SYP+L"
        };

        for (String recipe : recipes) {
            System.out.println("> Processing recipe: \"" + recipe + "\"");
            try {
                Product product = factory.create(recipe);
                System.out.println("  Result: " + product.name());
                if (product instanceof Priced priced) {
                    System.out.println("  Price: EUR " + priced.price());
                }
                System.out.println();
            } catch (IllegalArgumentException e) {
                System.out.println("  Error: " + e.getMessage() + "\n");
            }
        }

        System.out.println("\n" + "=".repeat(60) + "\n");
    }

    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }
}