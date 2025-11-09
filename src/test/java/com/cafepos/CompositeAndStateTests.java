package com.cafepos;

import com.cafepos.common.Money;
import com.cafepos.common.Product;
import com.cafepos.domain.LineItem;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.factory.ProductFactory;
import com.cafepos.menu.Menu;
import com.cafepos.menu.MenuComponent;
import com.cafepos.menu.MenuItem;
import com.cafepos.state.OrderFSM;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


class CompositeAndStateTests {

    @Test
    void depth_first_iteration_collects_all_nodes() {
        // build nested menu structure
        Menu root = new Menu("ROOT");
        Menu drinks = new Menu("Drinks");
        Menu food = new Menu("Food");

        root.add(drinks);
        root.add(food);

        drinks.add(new MenuItem("Espresso", Money.of(2.50), false));
        drinks.add(new MenuItem("Latte", Money.of(3.20), false));
        food.add(new MenuItem("Salad", Money.of(5.00), true));
        food.add(new MenuItem("Sandwich", Money.of(4.50), false));

        // get all items via depth-first traversal
        List<String> names = root.allItems().stream()
                .map(MenuComponent::name)
                .collect(Collectors.toList());

        // assert all items were collected (menus and items)
        assertTrue(names.contains("Drinks"), "Should contain Drinks menu");
        assertTrue(names.contains("Food"), "Should contain Food menu");
        assertTrue(names.contains("Espresso"), "Should contain Espresso item");
        assertTrue(names.contains("Latte"), "Should contain Latte item");
        assertTrue(names.contains("Salad"), "Should contain Salad item");
        assertTrue(names.contains("Sandwich"), "Should contain Sandwich item");

        assertEquals(6, names.size(), "Should have 6 total components");
    }

    @Test
    void vegetarianItems_returns_only_veg_items() {
        // build menu with mix of vegetarian and non-vegetarian items
        Menu root = new Menu("Menu");
        Menu drinks = new Menu("Drinks");
        Menu food = new Menu("Food");

        root.add(drinks);
        root.add(food);

        drinks.add(new MenuItem("Coffee", Money.of(2.50), false));
        food.add(new MenuItem("Veggie Burger", Money.of(6.00), true));
        food.add(new MenuItem("Chicken Sandwich", Money.of(7.00), false));
        food.add(new MenuItem("Garden Salad", Money.of(5.50), true));

        // get only vegetarian items
        List<MenuItem> vegItems = root.vegetarianItems();

        // assert only vegetarian items returned
        assertEquals(2, vegItems.size(), "Should have exactly 2 vegetarian items");

        List<String> vegNames = vegItems.stream()
                .map(MenuItem::name)
                .collect(Collectors.toList());

        assertTrue(vegNames.contains("Veggie Burger"));
        assertTrue(vegNames.contains("Garden Salad"));
        assertFalse(vegNames.contains("Coffee"));
        assertFalse(vegNames.contains("Chicken Sandwich"));
    }

    @Test
    void nested_menus_iterate_in_depth_first_order() {
        Menu root = new Menu("Root");
        Menu a = new Menu("A");
        Menu b = new Menu("B");

        root.add(a);
        root.add(b);

        a.add(new MenuItem("A1", Money.of(1.0), true));
        b.add(new MenuItem("B1", Money.of(2.0), false));
        b.add(new MenuItem("B2", Money.of(3.0), true));

        List<String> names = root.allItems().stream()
                .map(MenuComponent::name)
                .collect(Collectors.toList());

        // depth-first: Root, A, A1, B, B1, B2
        assertEquals("A", names.get(0));
        assertEquals("A1", names.get(1));
        assertEquals("B", names.get(2));
        assertEquals("B1", names.get(3));
        assertEquals("B2", names.get(4));
    }

    @Test
    void order_fsm_happy_path_transitions() {
        // create FSM in NEW state
        OrderFSM fsm = new OrderFSM();
        assertEquals("NEW", fsm.status(), "Should start in NEW state");

        // pay: NEW → PREPARING
        fsm.pay();
        assertEquals("PREPARING", fsm.status(), "Should transition to PREPARING after pay");

        // mark ready: PREPARING → READY
        fsm.markReady();
        assertEquals("READY", fsm.status(), "Should transition to READY after markReady");

        // deliver: READY → DELIVERED
        fsm.deliver();
        assertEquals("DELIVERED", fsm.status(), "Should transition to DELIVERED after deliver");
    }

    @Test
    void order_fsm_illegal_transitions_stay_in_same_state() {
        OrderFSM fsm = new OrderFSM();

        // try to prepare before paying (illegal)
        assertEquals("NEW", fsm.status());
        fsm.prepare();
        assertEquals("NEW", fsm.status(), "Should stay in NEW after illegal prepare");

        // try to deliver before paying (illegal)
        fsm.deliver();
        assertEquals("NEW", fsm.status(), "Should stay in NEW after illegal deliver");

        // try to mark ready before paying (illegal)
        fsm.markReady();
        assertEquals("NEW", fsm.status(), "Should stay in NEW after illegal markReady");
    }

    @Test
    void order_fsm_cancel_from_new_state() {
        OrderFSM fsm = new OrderFSM();
        assertEquals("NEW", fsm.status());

        // cancel from NEW → CANCELLED
        fsm.cancel();
        assertEquals("CANCELLED", fsm.status(), "Should transition to CANCELLED");

        // once cancelled, all operations should be rejected
        fsm.pay();
        assertEquals("CANCELLED", fsm.status(), "Should stay CANCELLED after pay attempt");

        fsm.prepare();
        assertEquals("CANCELLED", fsm.status(), "Should stay CANCELLED after prepare attempt");
    }

    @Test
    void order_fsm_cancel_during_preparing() {
        OrderFSM fsm = new OrderFSM();

        // transition to PREPARING
        fsm.pay();
        assertEquals("PREPARING", fsm.status());

        // CANCEL while preparing
        fsm.cancel();
        assertEquals("CANCELLED", fsm.status(), "Should be CANCELLED after cancel during prep");
    }

    @Test
    void order_fsm_cannot_cancel_after_ready() {
        OrderFSM fsm = new OrderFSM();

        // transition to READY
        fsm.pay();
        fsm.markReady();
        assertEquals("READY", fsm.status());

        // try to CANCEL (should be rejected)
        fsm.cancel();
        assertEquals("READY", fsm.status(), "Should stay READY - cannot cancel after ready");
    }

    @Test
    void integration_menu_item_to_product_to_order() {
        // build menu
        Menu menu = new Menu("Café Menu");
        menu.add(new MenuItem("Espresso", Money.of(2.50), false));
        menu.add(new MenuItem("Latte", Money.of(3.20), false));
        menu.add(new MenuItem("Cappuccino", Money.of(3.00), false));

        // find menu item by name (simulate customer selection)
        MenuItem selectedItem = menu.allItems().stream()
                .filter(mc -> mc instanceof MenuItem)
                .map(mc -> (MenuItem) mc)
                .filter(mi -> mi.name().equals("Latte"))
                .findFirst()
                .orElseThrow();

        assertEquals("Latte", selectedItem.name());
        assertEquals(Money.of(3.20), selectedItem.price());

        // create product via factory (using matching recipe)
        ProductFactory factory = new ProductFactory();
        Product product = factory.create("LAT");

        // add to order
        Order order = new Order(OrderIds.next());
        order.addItem(new LineItem(product, 2));

        // assert totals match Money logic
        // 2 lattes at 3.20 each = 6.40
        Money expectedSubtotal = Money.of(6.40);
        assertEquals(expectedSubtotal, order.subtotal(),
                "Order subtotal should match menu price × quantity");

        // with 10% tax: 6.40 + 0.64 = 7.04
        Money expectedTotal = Money.of(7.04);
        assertEquals(expectedTotal, order.totalWithTax(10),
                "Total with tax should match expected calculation");
    }
}