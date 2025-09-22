package com.cafepos;

import com.cafepos.common.*;
import com.cafepos.domain.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MoneyTests {

    @Test
    void testAddition() {
        Money a = Money.of(2.00);
        Money b = Money.of(3.00);
        Money result = a.add(b);
        assertEquals(Money.of(5.00), result);
    }

    @Test
    void testMultiply() {
        Money price = Money.of(2.50);
        Money result = price.multiply(4);
        assertEquals(Money.of(10.00), result);
    }

    @Test
    void testNoNegativeAllowed() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(-1.00));
        assertThrows(IllegalArgumentException.class, () -> Money.of(1.00).multiply(-2));
    }

    @Test void testOrderTotals() {
        var p1 = new SimpleProduct("A", "A", Money.of(2.50));
        var p2 = new SimpleProduct("B", "B", Money.of(3.50));
        var o = new Order(1);
        o.addItem(new LineItem(p1, 2));
        o.addItem(new LineItem(p2, 1));
        assertEquals(Money.of(8.50), o.subtotal());
        assertEquals(Money.of(0.85), o.taxAtPercent(10));
        assertEquals(Money.of(9.35), o.totalWithTax(10));
    }
}

