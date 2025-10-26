package com.cafepos;

import com.cafepos.catalog.*;
import com.cafepos.checkout.CheckoutService;
import com.cafepos.common.*;
import com.cafepos.domain.*;
import com.cafepos.factory.ProductFactory;
import com.cafepos.payment.*;
import com.cafepos.pricing.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MidtermIntegrationTests {

    @Test
    void complete_workflow_all_patterns_integrated() {
        // initialise factory from Week 5
        ProductFactory factory = new ProductFactory();

        // setup for pricing with discount and tax policies
        PricingService pricing = new PricingService(
                new LoyaltyPercentDiscount(5),
                new FixedRateTaxPolicy(10)
        );
        ReceiptPrinter printer = new ReceiptPrinter();
        CheckoutService checkout = new CheckoutService(factory, pricing, printer, 10);

        // create Order for registering Observer classes
        Order order = new Order(OrderIds.next());
        List<String> observerEvents = new ArrayList<>();
        order.register((o, e) -> observerEvents.add(e));
        order.register(new KitchenDisplay());
        order.register(new DeliveryDesk());
        order.register(new CustomerNotifier());

        // create products using Decorator + Factory (Week 5)
        Product espressoWithExtras = factory.create("ESP+SHOT+OAT");
        Product largeLatte = factory.create("LAT+L");

        // verify decorated products have correct names and prices
        assertTrue(espressoWithExtras.name().contains("Espresso"));
        assertTrue(espressoWithExtras.name().contains("Extra Shot"));
        assertTrue(espressoWithExtras.name().contains("Oat Milk"));
        assertEquals(Money.of(3.80), ((Priced) espressoWithExtras).price());

        assertTrue(largeLatte.name().contains("Latte"));
        assertTrue(largeLatte.name().contains("Large"));
        assertEquals(Money.of(3.90), ((Priced) largeLatte).price());

        // add LineItems to order (Week 2)
        order.addItem(new LineItem(espressoWithExtras, 1));
        order.addItem(new LineItem(largeLatte, 2));

        // verify order calculations
        // 3.80 + (3.90 * 2) = 11.60, + (11.60 * 10%) = 12.76
        assertEquals(Money.of(11.60), order.subtotal());
        assertEquals(Money.of(1.16), order.taxAtPercent(10));
        assertEquals(Money.of(12.76), order.totalWithTax(10));

        // verify observers were notified of item additions (Week 4)
        assertTrue(observerEvents.contains("itemAdded"));
        assertEquals(2, observerEvents.stream().filter(e -> e.equals("itemAdded")).count());

        // process payment with PaymentStrategy (Week 3)
        PaymentStrategy cardPayment = new CardPayment("1928739182039123");
        order.pay(cardPayment);

        // verify payment observer notification (Week 4)
        assertTrue(observerEvents.contains("paid"));

        // mark order ready (Week 4)
        order.markReady();

        // verify ready observer notification (Week 4)
        assertTrue(observerEvents.contains("ready"));

        // verify complete event sequence i.e. status updates are in correct order
        assertEquals("itemAdded", observerEvents.get(0));
        assertEquals("itemAdded", observerEvents.get(1));
        assertEquals("paid", observerEvents.get(2));
        assertEquals("ready", observerEvents.get(3));

        // test checkout service with pricing (Week 6)
        String receipt = checkout.checkout("LAT+L", 2);

        // verify receipt format and calculations
        //
        assertTrue(receipt.contains("Order (LAT+L) x2"));
        assertTrue(receipt.contains("Subtotal: 7.80"));
        assertTrue(receipt.contains("Discount: -0.39"));
        assertTrue(receipt.contains("Tax (10%): 0.74"));
        assertTrue(receipt.contains("Total: 8.15"));
    }

    @Test
    void different_payment_strategies_all_work() {
        ProductFactory factory = new ProductFactory();
        Order order1 = new Order(OrderIds.next());
        Order order2 = new Order(OrderIds.next());
        Order order3 = new Order(OrderIds.next());

        Product product = factory.create("ESP");

        order1.addItem(new LineItem(product, 1));
        order2.addItem(new LineItem(product, 1));
        order3.addItem(new LineItem(product, 1));

        // all payment strategies should work without errors for same product
        assertDoesNotThrow(() -> order1.pay(new CashPayment()));
        assertDoesNotThrow(() -> order2.pay(new CardPayment("12938729301239")));
        assertDoesNotThrow(() -> order3.pay(new WalletPayment("fionn-wallet")));
    }

    @Test
    void catalog_integration_with_order_system() {
        // test that Catalog (Week 2) integrates with Order and Observer classes
        Catalog catalog = new InMemoryCatalog();
        catalog.add(new SimpleProduct("P-ESP", "Espresso", Money.of(2.50)));
        catalog.add(new SimpleProduct("P-LAT", "Latte", Money.of(3.20)));

        Order order = new Order(OrderIds.next());
        List<String> events = new ArrayList<>();
        order.register((o, e) -> events.add(e));

        Product espresso = catalog.findById("P-ESP").orElseThrow();
        Product latte = catalog.findById("P-LAT").orElseThrow();

        order.addItem(new LineItem(espresso, 2));
        order.addItem(new LineItem(latte, 1));

        assertEquals(Money.of(8.20), order.subtotal());

        // verify size of events ArrayList (should have 2 "itemAdded" events)
        assertEquals(2, events.size());
    }
}