package com.cafepos;

import com.cafepos.common.*;
import com.cafepos.domain.*;
import com.cafepos.payment.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentStrategyTests {

    private Order createSampleOrder() {
        SimpleProduct p1 = new SimpleProduct("A", "A", Money.of(2.50));
        SimpleProduct p2 = new SimpleProduct("B", "B", Money.of(3.50));
        Order order = new Order(100);
        order.addItem(new LineItem(p1, 2));
        order.addItem(new LineItem(p2, 1));
        return order;
    }

    @Test
    void payment_strategy_calls() {
        Order order = createSampleOrder();
        final boolean[] called = {false};
        PaymentStrategy fake = o -> called[0] = true;

        order.pay(fake);

        assertTrue(called[0], "Payment strategy should be called");
    }

    @Test
    void cash_payment_prints_message() {
        Order order = createSampleOrder();
        PaymentStrategy cash = new CashPayment();

        // capture console output & validate it
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        order.pay(cash);

        String output = out.toString().trim();
        assertTrue(output.contains("[Cash] Customer paid 9.35 EUR"),
                "CashPayment should print confirmation message");
    }

    @Test
    void card_payment_masks_card_number() {
        Order order = createSampleOrder();
        PaymentStrategy card = new CardPayment("1234567812341234");

        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        order.pay(card);

        String output = out.toString().trim();

        // validate card number is masked
        assertTrue(output.contains("****1234"), "CardPayment should mask all but last 4 digits");
        assertTrue(output.contains("9.35"), "CardPayment should print correct amount");
    }

    @Test
    void wallet_payment_includes_wallet_id() {
        Order order = createSampleOrder();
        PaymentStrategy wallet = new WalletPayment("my-wallet");

        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        order.pay(wallet);

        String output = out.toString().trim();

        // validate wallet payment information
        assertTrue(output.contains("my-wallet"), "WalletPayment should include wallet ID");
        assertTrue(output.contains("9.35"), "WalletPayment should print correct amount");
    }

    @Test
    void order_pay_with_null_strategy_throws() {
        Order order = createSampleOrder();

        assertThrows(IllegalArgumentException.class, () -> order.pay(null),
                "Order should throw when given a null PaymentStrategy");
    }
}
