package com.cafepos.demo;

import com.cafepos.checkout.CheckoutService;
import com.cafepos.domain.*;
import com.cafepos.factory.ProductFactory;
import com.cafepos.payment.CardPayment;
import com.cafepos.payment.CashPayment;
import com.cafepos.payment.WalletPayment;
import com.cafepos.pricing.*;
import com.cafepos.common.Product;

public final class Week6Demo {
    public static void main(String[] args) {
        System.out.println("=== OVERALL CAFE POS SYSTEM ===");
        System.out.println("-------------------------------\n");

        // PART 1: comparing OldManagerGod vs CheckoutService (after Week 6 refactoring)
        System.out.println("--- PART 1: Refactoring Validation ---");
        System.out.println("OldManagerGod vs CheckoutService class outputs\n");

        // old behaviour from God class
        String oldReceipt = com.cafepos.smells.OrderManagerGod.process(
                "LAT+L", 2, "CARD", "LOYAL5", false
        );

        // new behavior (clean, refactored design)
        var pricing = new PricingService(
                new LoyaltyPercentDiscount(5),
                new FixedRateTaxPolicy(10)
        );
        var printer = new ReceiptPrinter();
        var checkout = new CheckoutService(new ProductFactory(), pricing, printer, 10);
        String newReceipt = checkout.checkout("LAT+L", 2);

        System.out.println("Old Receipt:\n" + oldReceipt);
        System.out.println("\nNew Receipt:\n" + newReceipt);
        System.out.println("\nMatch: " + oldReceipt.equals(newReceipt));

        // PART 2: complete order flow with all patterns
        System.out.println("\n--- PART 2: Complete Order Flow ---");
        System.out.println("Demonstrating all patterns working together\n");

        // initialize factory for creating products (Week 5)
        ProductFactory factory = new ProductFactory();

        // create Order (Week 2)
        Order order = new Order(OrderIds.next());
        System.out.println("Created Order #" + order.id());

        // register Observers (Week 4) - Kitchen, Delivery, Customer
        order.register(new KitchenDisplay());
        order.register(new DeliveryDesk());
        order.register(new CustomerNotifier());
        System.out.println("Registered observers: Kitchen, Delivery, Customer\n");

        // create decorated products using Factory (Week 5)
        System.out.println("Creating products using Factory pattern with Decorator:");
        Product product1 = factory.create("ESP+SHOT+OAT");
        System.out.println("  - " + product1.name() + " (Recipe: ESP+SHOT+OAT)");

        Product product2 = factory.create("LAT+L");
        System.out.println("  - " + product2.name() + " (Recipe: LAT+L)\n");

        // add items to order (Week 2) - triggers Observer notifications
        System.out.println("Adding items to order (observers will be notified):");
        order.addItem(new LineItem(product1, 1));
        order.addItem(new LineItem(product2, 2));
        System.out.println();

        // display order details (Week 2)
        System.out.println("Order Details:");
        System.out.println("  Order #" + order.id());
        for (LineItem li : order.items()) {
            System.out.println("    - " + li.product().name() +
                    " x" + li.quantity() + " = €" + li.lineTotal());
        }
        System.out.println("  Subtotal: €" + order.subtotal());
        System.out.println("  Tax (10%): €" + order.taxAtPercent(10));
        System.out.println("  Total: €" + order.totalWithTax(10) + "\n");

        // process payment using Strategy pattern (Week 3) - triggers Observer notification
        System.out.println("Processing payment with Card Strategy:");
        order.pay(new CardPayment("1234567890123456"));
        System.out.println();

        // mark order ready (Week 4) - triggers Observer notification
        System.out.println("Marking order as ready for delivery:");
        order.markReady();
        System.out.println();

        // PART 3: demonstrating different payment strategies (Week 3)
        System.out.println("\n--- PART 3: Payment Strategy Examples ---");

        // example with CashPayment
        Order cashOrder = new Order(OrderIds.next());
        cashOrder.addItem(new LineItem(factory.create("ESP"), 1));
        System.out.println("Order #" + cashOrder.id() + " Total: €" + cashOrder.totalWithTax(10));
        cashOrder.pay(new CashPayment());

        // example with WalletPayment
        Order walletOrder = new Order(OrderIds.next());
        walletOrder.addItem(new LineItem(factory.create("CAP+SYP"), 1));
        System.out.println("Order #" + walletOrder.id() + " Total: €" + walletOrder.totalWithTax(10));
        walletOrder.pay(new WalletPayment("fionn-wallet"));

        // PART 4: demonstrating different discount policies (Week 6)
        System.out.println("\n--- PART 4: Discount Policy Examples ---");

        // no discount
        var checkoutNoDiscount = new CheckoutService(
                new ProductFactory(),
                new PricingService(new NoDiscount(), new FixedRateTaxPolicy(10)),
                new ReceiptPrinter(),
                10
        );
        System.out.println("Receipt with NO discount:");
        System.out.println(checkoutNoDiscount.checkout("ESP+SHOT", 2));

        // fixed coupon discount
        System.out.println("\nReceipt with €1.00 COUPON:");
        var checkoutCoupon = new CheckoutService(
                new ProductFactory(),
                new PricingService(new FixedCouponDiscount(com.cafepos.common.Money.of(1.00)),
                        new FixedRateTaxPolicy(10)),
                new ReceiptPrinter(),
                10
        );
        System.out.println(checkoutCoupon.checkout("ESP+SHOT", 2));

        // loyalty percentage discount
        System.out.println("\nReceipt with 10% LOYALTY discount:");
        var checkoutLoyalty = new CheckoutService(
                new ProductFactory(),
                new PricingService(new LoyaltyPercentDiscount(10),
                        new FixedRateTaxPolicy(10)),
                new ReceiptPrinter(),
                10
        );
        System.out.println(checkoutLoyalty.checkout("LAT+L", 2));
    }
}