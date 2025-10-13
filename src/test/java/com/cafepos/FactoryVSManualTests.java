package com.cafepos;

import com.cafepos.common.Money;
import com.cafepos.common.Product;
import com.cafepos.common.SimpleProduct;
import com.cafepos.common.Priced;
import com.cafepos.decorator.*;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.factory.ProductFactory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FactoryVSManualTests {

    @Test
    void factory_and_manual_produce_same_product() {
        // build via factory
        ProductFactory factory = new ProductFactory();
        Product viaFactory = factory.create("ESP+SHOT+OAT+L");

        // build via manual wrapping
        Product viaManual = new SizeLarge(
                new OatMilk(
                        new ExtraShot(
                                new SimpleProduct("P-ESP", "Espresso", Money.of(2.50))
                        )
                )
        );

        // assert names are equal
        assertEquals(viaManual.name(), viaFactory.name(),
                "Factory and manual construction should produce the same product name");

        // assert prices are equal (using Priced interface)
        assertTrue(viaFactory instanceof Priced, "Factory product should implement Priced");
        assertTrue(viaManual instanceof Priced, "Manual product should implement Priced");

        Money factoryPrice = ((Priced) viaFactory).price();
        Money manualPrice = ((Priced) viaManual).price();

        assertEquals(manualPrice, factoryPrice,
                "Factory and manual construction should produce the same price");
    }

    @Test
    void factory_and_manual_produce_same_order_totals() {
        // build products
        ProductFactory factory = new ProductFactory();
        Product viaFactory = factory.create("ESP+SHOT+OAT+L");

        Product viaManual = new SizeLarge(
                new OatMilk(
                        new ExtraShot(
                                new SimpleProduct("P-ESP", "Espresso", Money.of(2.50))
                        )
                )
        );

        // create two separate orders
        Order orderFactory = new Order(OrderIds.next());
        Order orderManual = new Order(OrderIds.next());

        // add each product with quantity one
        orderFactory.addItem(new LineItem(viaFactory, 1));
        orderManual.addItem(new LineItem(viaManual, 1));

        // assert subtotals are equal
        assertEquals(orderManual.subtotal(), orderFactory.subtotal(),
                "Orders with factory and manual products should have the same subtotal");

        // assert totals with tax are equal
        assertEquals(orderManual.totalWithTax(10), orderFactory.totalWithTax(10),
                "Orders with factory and manual products should have the same total with tax");
    }

    @Test
    void factory_and_manual_produce_correct_price_breakdown() {
        ProductFactory factory = new ProductFactory();
        Product product = factory.create("ESP+SHOT+OAT+L");

        // verify the price breakdown: 2.50 + 0.80 + 0.50 + 0.70 = 4.50
        Money expectedPrice = Money.of(4.50);
        Money actualPrice = ((Priced) product).price();

        assertEquals(expectedPrice, actualPrice,
                "ESP+SHOT+OAT+L should cost EUR 4.50");
    }

    @Test
    void different_recipes_produce_different_products() {
        ProductFactory factory = new ProductFactory();

        Product espresso = factory.create("ESP");
        Product espressoWithShot = factory.create("ESP+SHOT");
        Product latteLarge = factory.create("LAT+L");

        // names should be different
        assertNotEquals(espresso.name(), espressoWithShot.name());
        assertNotEquals(espresso.name(), latteLarge.name());
        assertNotEquals(espressoWithShot.name(), latteLarge.name());

        // prices should be different
        assertNotEquals(((Priced) espresso).price(), ((Priced) espressoWithShot).price());
        assertNotEquals(((Priced) espresso).price(), ((Priced) latteLarge).price());
    }
}