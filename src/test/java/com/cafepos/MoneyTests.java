package com.cafepos;

import com.cafepos.common.Money;
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
}

