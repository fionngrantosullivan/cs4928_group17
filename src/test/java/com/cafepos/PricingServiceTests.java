package com.cafepos;

import com.cafepos.common.Money;
import com.cafepos.pricing.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PricingServiceTests {

    @Test
    void pricing_with_loyalty_discount_and_tax() {
        PricingService pricing = new PricingService(
                new LoyaltyPercentDiscount(5),
                new FixedRateTaxPolicy(10)
        );

        PricingService.PricingResult result = pricing.price(Money.of(7.80));

        assertEquals(Money.of(7.80), result.subtotal());
        assertEquals(Money.of(0.39), result.discount());
        assertEquals(Money.of(0.74), result.tax());
        assertEquals(Money.of(8.15), result.total());
    }

    @Test
    void pricing_with_no_discount() {
        PricingService pricing = new PricingService(
                new NoDiscount(),
                new FixedRateTaxPolicy(10)
        );

        PricingService.PricingResult result = pricing.price(Money.of(10.00));

        assertEquals(Money.of(10.00), result.subtotal());
        assertEquals(Money.zero(), result.discount());
        assertEquals(Money.of(1.00), result.tax());
        assertEquals(Money.of(11.00), result.total());
    }

    @Test
    void pricing_with_coupon_discount() {
        PricingService pricing = new PricingService(
                new FixedCouponDiscount(Money.of(1.00)),
                new FixedRateTaxPolicy(10)
        );

        PricingService.PricingResult result = pricing.price(Money.of(3.30));

        assertEquals(Money.of(3.30), result.subtotal());
        assertEquals(Money.of(1.00), result.discount());
        assertEquals(Money.of(0.23), result.tax());
        assertEquals(Money.of(2.53), result.total());
    }

    @Test
    void pricing_handles_discount_larger_than_subtotal() {
        PricingService pricing = new PricingService(
                new FixedCouponDiscount(Money.of(100.00)),
                new FixedRateTaxPolicy(10)
        );

        PricingService.PricingResult result = pricing.price(Money.of(5.00));

        // Discount capped at subtotal, resulting in 0 after discount
        assertEquals(Money.zero(), result.tax());
        assertEquals(Money.zero(), result.total());
    }
}