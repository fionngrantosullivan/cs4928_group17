package com.cafepos;

import com.cafepos.common.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MoneyTests {

    @Test
    void addition() {
        Money a = Money.of(2.00);
        Money b = Money.of(3.00);
        Money result = a.add(b);
        assertEquals(Money.of(5.00), result);
    }

    @Test
    void multiply() {
        Money price = Money.of(2.50);
        Money result = price.multiply(4);
        assertEquals(Money.of(10.00), result);
    }

    @Test
    void no_negative_allowed() {
        assertThrows(IllegalArgumentException.class, () -> Money.of(-1.00));
        assertThrows(IllegalArgumentException.class, () -> Money.of(1.00).multiply(-2));
    }
}

