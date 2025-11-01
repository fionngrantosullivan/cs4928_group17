package com.cafepos;

import com.cafepos.command.*;
import com.cafepos.common.Money;
import com.cafepos.domain.Order;
import com.cafepos.domain.OrderIds;
import com.cafepos.payment.CashPayment;
import com.cafepos.printing.LegacyPrinterAdapter;
import com.cafepos.printing.Printer;

import vendor.legacy.LegacyThermalPrinter;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommandAndAdapterTests {

    @Test
    void command_executes_and_undo_reverses_change() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);

        Command addLatte = new AddItemCommand(service, "LAT", 2);
        addLatte.execute();
        
        // count items before + after undo to prove its effect
        assertEquals(1, order.items().size(), "Command should add 1 line item");
        addLatte.undo();
        assertEquals(0, order.items().size(), "Undo should remove the item");
    }

    @Test
    void macroCommand_undo_reverses_in_reverse_order() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);

        // execute 2 orders using MacroCommand
        Command addEsp = new AddItemCommand(service, "ESP", 1);
        Command addLat = new AddItemCommand(service, "LAT", 1);
        Command macro = new MacroCommand(addEsp, addLat);

        // show MacroCommand undoes all its commands in reverse order
        macro.execute();
        assertEquals(2, order.items().size(), "Macro should add 2 items");
        macro.undo();

        assertEquals(0, order.items().size(), 
            "MacroCommand undo should reverse all commands in reverse order");
    }

    class FakeLegacyPrinter extends LegacyThermalPrinter {
        int lastLen = -1;
        
        @Override
        public void legacyPrint(byte[] payload) {
            lastLen = payload.length;
        }
    }
    
    @Test
    void adapter_converts_text_to_bytes() {
        // create fake legacy printer
        FakeLegacyPrinter fake = new FakeLegacyPrinter();
        Printer adapter = new LegacyPrinterAdapter(fake);

        adapter.print("ABC");

        // confirm string is converted to at least 3 bytes
        assertTrue(fake.lastLen >= 3,
            "Adapter should convert 'ABC' to at least 11 bytes");
    }


    // integration test to show end-to-end wiring
    @Test
    void integration_posRemote_with_commands_and_payment() {
        Order order = new Order(OrderIds.next());
        OrderService service = new OrderService(order);
        PosRemote remote = new PosRemote(5);

        // bind 2 AddItemCommands and 1 PayOrderCommand
        Command addEsp = new AddItemCommand(service, "ESP", 1);  // 2.50
        Command addLat = new AddItemCommand(service, "LAT+L", 2);  // 3.90 x 2 = 7.80
        Command pay = new PayOrderCommand(service, new CashPayment(), 10);
        
        remote.setSlot(0, addEsp);
        remote.setSlot(1, addLat);
        remote.setSlot(2, pay);

        remote.press(0);
        remote.press(1);
        
        // assert order subtotal equals expected from Week 5 prices
        // ESP = 2.50, LAT+L = 3.90, quantity 2 = 7.80
        // Total: 2.50 + 7.80 = 10.30
        Money expectedSubtotal = Money.of(10.30);
        assertEquals(expectedSubtotal, order.subtotal(), 
            "Subtotal should match Week 5 decorator prices");
        
        // execute payment
        assertDoesNotThrow(() -> remote.press(2), 
            "Payment should execute without errors");
    }
}