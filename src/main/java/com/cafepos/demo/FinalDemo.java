package com.cafepos.demo;

import com.cafepos.app.events.*;
import com.cafepos.command.*;
import com.cafepos.common.Money;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.infra.Wiring;
import com.cafepos.menu.*;
import com.cafepos.payment.CardPayment;
import com.cafepos.printing.*;
import com.cafepos.state.OrderFSM;
import com.cafepos.ui.ConsoleView;
import com.cafepos.ui.OrderController;
import vendor.legacy.LegacyThermalPrinter;

public final class FinalDemo {
    public static void main(String[] args) {
        System.out.println("CAFÉ POS SYSTEM - FINAL INTEGRATION DEMO");
        System.out.println("Demonstrating all required patterns in action\n");

        // 1. COMPOSITE & ITERATOR PATTERNS: Hierarchical Menu
        // composite pattern: Menu and MenuItem both implement MenuComponent, allowing uniform treatment
        // we can call add(), print() on both without knowing if it's a group or single item
        System.out.println("[1] COMPOSITE & ITERATOR: Building hierarchical menu...");
        Menu rootMenu = createMenuHierarchy();
        System.out.println("\nFull Menu Structure:");
        // composite print() recursively traverses the tree, calling print() on each component
        rootMenu.print();
        
        // iterator pattern: CompositeIterator uses depth-first search with a stack
        // filters across entire hierarchy regardless of nesting level (root → drinks → coffee → items)
        System.out.println("\n[ITERATOR] Filtering vegetarian items across entire hierarchy:");
        var vegetarianItems = rootMenu.vegetarianItems();
        for (MenuItem item : vegetarianItems) {
            System.out.println("  - " + item.name() + " (V) = " + item.price());
        }
        System.out.println();
        
        // 2. COMMAND PATTERN: POS Remote with Undo
        // command pattern: encapsulates operations as objects, decoupling invoker (PosRemote) from receiver (OrderService)
        System.out.println("[2] COMMAND PATTERN: Using POS Remote for order entry...");
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);
        PosRemote remote = new PosRemote(5);
        
        // configure command slots - commands store operation and parameters, ready to execute later
        remote.setSlot(0, new AddItemCommand(service, "ESP", 1));
        remote.setSlot(1, new AddItemCommand(service, "LAT+L", 2));
        remote.setSlot(2, new AddItemCommand(service, "ESP+SHOT+OAT", 1));
        
        // PosRemote doesn't know what commands do, just executes them and maintains history for undo
        System.out.println("  Pressing slot 0: Add Espresso...");
        remote.press(0);
        System.out.println("  Pressing slot 1: Add Large Latte x2...");
        remote.press(1);
        System.out.println("  Pressing slot 2: Add Espresso with shot and oat milk...");
        remote.press(2);
        System.out.println("  Order subtotal: " + order.subtotal());
        
        // undo uses command history to reverse last operation
        System.out.println("\n  [UNDO] Undoing last command...");
        remote.undo();
        System.out.println("  Order subtotal after undo: " + order.subtotal() + "\n");
        
        // 3. STATE PATTERN: Order Lifecycle
        // state pattern: OrderFSM delegates behavior to state objects instead of using conditionals
        // each state (NewState, PreparingState, etc.) knows its valid transitions
        System.out.println("[3] STATE PATTERN: Order lifecycle state transitions...");
        OrderFSM fsm = new OrderFSM();
        System.out.println("  Initial state: " + fsm.status());
        
        // state objects enforce business rules - NewState rejects prepare() before payment
        System.out.println("  Attempting prepare (should fail - not paid yet):");
        fsm.prepare();
        
        // state transition: NewState → PreparingState when pay() is called
        System.out.println("  Paying order...");
        fsm.pay();
        System.out.println("  State after payment: " + fsm.status());
        
        // state transitions: PreparingState → ReadyState → DeliveredState
        System.out.println("  Starting preparation...");
        fsm.prepare();
        System.out.println("  Marking as ready...");
        fsm.markReady();
        System.out.println("  State: " + fsm.status());
        
        System.out.println("  Delivering order...");
        fsm.deliver();
        System.out.println("  Final state: " + fsm.status() + "\n");
        
        // 4. MVC & EVENTBUS: Complete Order Processing
        // MVC pattern: separates Controller (handles input), Model (domain logic), View (displays output)
        System.out.println("[4] MVC & EVENTBUS: Complete order processing workflow...");
        
        // set up MVC components - controller coordinates between model and view
        var wiring = Wiring.createDefault();
        OrderController controller = new OrderController(wiring.repo(), wiring.checkout());
        ConsoleView view = new ConsoleView();
        
        // EventBus pattern: publish-subscribe decouples components - publishers don't know subscribers
        EventBus bus = new EventBus();
        // subscribe to events - handlers are notified when events are emitted
        bus.on(OrderCreated.class, e -> 
            System.out.println("  [EVENT] Order #" + e.orderId() + " created!"));
        bus.on(OrderPaid.class, e -> 
            System.out.println("  [EVENT] Order #" + e.orderId() + " paid! Notifying kitchen..."));
        
        // controller manipulates model (Order via repository) without knowing about view
        long orderId = 5001L;
        System.out.println("  [CONTROLLER] Creating order #" + orderId + "...");
        controller.createOrder(orderId);
        // emit event - all subscribers are notified automatically, no direct dependencies
        bus.emit(new OrderCreated(orderId));
        
        // controller coordinates business operations using application services
        System.out.println("  [CONTROLLER] Adding items...");
        controller.addItem(orderId, "LAT+L", 1);
        controller.addItem(orderId, "ESP+SHOT", 2);
        
        // controller gets data from model, returns it to view
        System.out.println("  [CONTROLLER] Processing checkout...");
        String receipt = controller.checkout(orderId, 10);
        
        // view displays data - could be swapped for GUI/web view without changing controller or model
        System.out.println("  [VIEW] Receipt:\n");
        view.print(receipt);
        
        // emit events decouple layers - application doesn't depend on presentation
        bus.emit(new OrderPaid(orderId));
        System.out.println();
        
        // 5. ADAPTER PATTERN: Legacy Printer Integration
        // adapter pattern: wraps incompatible interface (LegacyThermalPrinter.legacyPrint(byte[]))
        // and adapts it to modern interface (Printer.print(String))
        System.out.println("[5] ADAPTER PATTERN: Printing receipt via legacy printer...");
        // LegacyPrinterAdapter translates String → byte[] internally, legacy code stays untouched
        Printer printer = new LegacyPrinterAdapter(new LegacyThermalPrinter());
        System.out.println("  Using LegacyPrinterAdapter to wrap LegacyThermalPrinter...");
        // client code uses modern Printer interface, adapter handles conversion
        printer.print(receipt);
        System.out.println("  [ADAPTER] Receipt sent to legacy thermal printer successfully!\n");
        
        // 6. COMMAND PATTERN: Macro Command
        // macro command: composite of commands - combines multiple operations into one executable unit
        System.out.println("[6] COMMAND PATTERN: Macro Command - Batch operations...");
        Order anotherOrder = new Order(OrderIds.next());
        OrderService anotherService = new OrderService(anotherOrder);
        
        // MacroCommand contains multiple commands, executes them sequentially as one operation
        // undo would reverse all commands in reverse order
        Command macro = new MacroCommand(
            new AddItemCommand(anotherService, "ESP", 1),
            new AddItemCommand(anotherService, "LAT", 1),
            new PayOrderCommand(anotherService, new CardPayment("1234567890123456"), 10)
        );
        
        System.out.println("  Executing macro command (add 2 items + pay)...");
        macro.execute();
        System.out.println("  Macro completed. Order subtotal: " + anotherOrder.subtotal());
    }
    
    // composite pattern: builds tree structure where Menu (composite) and MenuItem (leaf) both extend MenuComponent
    // can add MenuItems to Menus, or Menus to other Menus - all treated uniformly
    private static Menu createMenuHierarchy() {
        Menu root = new Menu("CAFÉ MENU");
        
        // drinks section
        Menu drinks = new Menu("  Drinks");
        Menu coffee = new Menu("    Coffee");
        Menu tea = new Menu("    Tea");
        
        // MenuItems are leaf nodes - can't contain children
        coffee.add(new MenuItem("Espresso", Money.of(2.50), true));
        coffee.add(new MenuItem("Latte (Large)", Money.of(3.90), true));
        coffee.add(new MenuItem("Cappuccino", Money.of(3.20), true));
        
        tea.add(new MenuItem("Green Tea", Money.of(2.00), true));
        tea.add(new MenuItem("Chai Latte", Money.of(3.50), true));
        
        // Menus are composites - can contain other Menus or MenuItems
        drinks.add(coffee);
        drinks.add(tea);
        
        // food section
        Menu food = new Menu("  Food");
        Menu sandwiches = new Menu("    Sandwiches");
        
        sandwiches.add(new MenuItem("Veggie Wrap", Money.of(5.50), true));
        sandwiches.add(new MenuItem("Chicken Sandwich", Money.of(6.50), false));
        sandwiches.add(new MenuItem("Tofu Sandwich", Money.of(5.00), true));
        
        food.add(sandwiches);
        food.add(new MenuItem("Garden Salad", Money.of(4.50), true));
        
        root.add(drinks);
        root.add(food);
        
        return root;
    }
}

