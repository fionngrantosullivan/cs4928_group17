package com.cafepos;

import com.cafepos.common.Money;
import com.cafepos.pricing.DiscountPolicy;
import com.cafepos.pricing.FixedCouponDiscount;
import com.cafepos.pricing.LoyaltyPercentDiscount;
import com.cafepos.pricing.NoDiscount;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DiscountPolicyTests {

    @Test
    void noDiscount_returns_zero() {
        DiscountPolicy policy = new NoDiscount();
        assertEquals(Money.zero(), policy.discountOf(Money.of(100.00)));
    }

    @Test
    void loyaltyDiscount_5_percent() {
        DiscountPolicy policy = new LoyaltyPercentDiscount(5);
        assertEquals(Money.of(0.39), policy.discountOf(Money.of(7.80)));
    }

    @Test
    void loyaltyDiscount_10_percent() {
        DiscountPolicy policy = new LoyaltyPercentDiscount(10);
        assertEquals(Money.of(1.00), policy.discountOf(Money.of(10.00)));
    }

    @Test
    void fixedCouponDiscount_normal_case() {
        DiscountPolicy policy = new FixedCouponDiscount(Money.of(1.00));
        assertEquals(Money.of(1.00), policy.discountOf(Money.of(10.00)));
    }

    @Test
    void fixedCouponDiscount_capped_at_subtotal() {
        DiscountPolicy policy = new FixedCouponDiscount(Money.of(10.00));
        // Discount can't exceed subtotal
        assertEquals(Money.of(5.00), policy.discountOf(Money.of(5.00)));
    }

    @Test
    void loyaltyDiscount_negative_percent_throws() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LoyaltyPercentDiscount(-5);
        });
    }
}