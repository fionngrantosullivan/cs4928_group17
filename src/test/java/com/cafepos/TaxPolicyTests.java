package com.cafepos;

import com.cafepos.common.Money;
import com.cafepos.pricing.FixedRateTaxPolicy;
import com.cafepos.pricing.TaxPolicy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaxPolicyTests {

    @Test
    void fixedRateTax_10_percent() {
        TaxPolicy policy = new FixedRateTaxPolicy(10);
        assertEquals(Money.of(0.74), policy.taxOn(Money.of(7.41)));
    }

    @Test
    void fixedRateTax_0_percent() {
        TaxPolicy policy = new FixedRateTaxPolicy(0);
        assertEquals(Money.zero(), policy.taxOn(Money.of(100.00)));
    }

    @Test
    void fixedRateTax_20_percent() {
        TaxPolicy policy = new FixedRateTaxPolicy(20);
        assertEquals(Money.of(2.00), policy.taxOn(Money.of(10.00)));
    }

    @Test
    void fixedRateTax_negative_percent_throws() {
        assertThrows(IllegalArgumentException.class, () -> {
            new FixedRateTaxPolicy(-10);
        });
    }
}
