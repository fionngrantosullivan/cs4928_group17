package com.cafepos;

import com.cafepos.catalog.*;
import com.cafepos.common.*;
import com.cafepos.payment.*;
import com.cafepos.domain.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObserverPatternTests {

    @Test
    void observers_notified_on_item_add() {
        var p = new SimpleProduct("A", "A", Money.of(2));
        var o = new Order(1);
        o.addItem(new LineItem(p, 1)); // baseline

        List<String> events = new ArrayList<>();
        o.register((order, evt) -> events.add(evt));

        o.addItem(new LineItem(p, 1));

        assertTrue(events.contains("itemAdded"));
    }

    @Test
    void multiple_observers_both_receive_ready_event() {
        var o = new Order(1);

        List<String> events1 = new ArrayList<>();
        List<String> events2 = new ArrayList<>();

        o.register((order, evt) -> events1.add(evt));
        o.register((order, evt) -> events2.add(evt));

        o.markReady();

        assertTrue(events1.contains("ready"));
        assertTrue(events2.contains("ready"));
    }

    @Test
    void all_event_types_are_propagated() {
        var p = new SimpleProduct("A", "A", Money.of(3));
        var o = new Order(1);

        List<String> events = new ArrayList<>();
        o.register((order, evt) -> events.add(evt));

        o.addItem(new LineItem(p, 1));
        o.pay(new CashPayment());
        o.markReady();

        assertEquals(3, events.size());
        assertEquals("itemAdded", events.get(0));
        assertEquals("paid", events.get(1));
        assertEquals("ready", events.get(2));
    }

    @Test
    void unregister_stops_notifications() {
        var o = new Order(1);
        List<String> events = new ArrayList<>();
        OrderObserver observer = (order, evt) -> events.add(evt);

        o.register(observer);
        o.markReady(); // Receives this

        o.unregister(observer);
        o.markReady(); // Doesn't receive this

        assertEquals(1, events.size());
    }

    @Test
    void null_observer_rejected() {
        var o = new Order(1);
        assertThrows(IllegalArgumentException.class, () -> o.register(null));
    }

    @Test
    void duplicate_observer_only_notified_once() {
        var o = new Order(1);
        List<String> events = new ArrayList<>();
        OrderObserver observer = (order, evt) -> events.add(evt);

        o.register(observer);
        o.register(observer); // Duplicate

        o.markReady();

        assertEquals(1, events.size()); // Only notified once
    }

    @Test
    void concrete_observers_work_together_without_errors() {
        var p = new SimpleProduct("A", "A", Money.of(2));
        var o = new Order(1);

        assertDoesNotThrow(() -> {
            o.register(new KitchenDisplay());
            o.register(new DeliveryDesk());
            o.register(new CustomerNotifier());

            o.addItem(new LineItem(p, 1));
            o.pay(new CashPayment());
            o.markReady();
        });
    }

    @Test
    void complete_workflow_with_catalog_and_observers() {
        Catalog catalog = new InMemoryCatalog();
        catalog.add(new SimpleProduct("P-ESP", "Espresso", Money.of(2.50)));

        Order order = new Order(OrderIds.next());
        List<String> recordedEvents = new ArrayList<>();

        order.register((o, evt) -> recordedEvents.add(evt));

        order.addItem(new LineItem(catalog.findById("P-ESP").orElseThrow(), 2));
        order.pay(new CashPayment());
        order.markReady();

        assertEquals(3, recordedEvents.size());
        assertTrue(recordedEvents.contains("itemAdded"));
        assertTrue(recordedEvents.contains("paid"));
        assertTrue(recordedEvents.contains("ready"));
    }
}