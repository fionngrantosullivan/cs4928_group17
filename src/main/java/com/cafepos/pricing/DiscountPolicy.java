package com.cafepos.pricing;

import com.cafepos.common.Money;
import java.math.BigDecimal;

// Strategy pattern using polymorphism: Replace primitive obsession and scattered discount logic
public interface DiscountPolicy {
    Money discountOf(Money subtotal);

    // Helper method for percentage-based discounts
    default Money calculatePercentage(Money amount, int percent) {
        return Money.of(amount.asBigDecimal()
                .multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100)));
    }
}
// Concrete strategy for 5% loyalty discount
class LoyaltyDiscount implements DiscountPolicy {
    private static final int LOYALTY_PERCENT = 5;

    @Override
    public Money discountOf(Money subtotal) {
        return calculatePercentage(subtotal, LOYALTY_PERCENT);
    }

    @Override
    public Money calculatePercentage(Money amount, int percent) {
        return null;
    }
}

// Concrete strategy for fixed $1.00 coupon
class FixedCouponDiscount implements DiscountPolicy {
    private static final double COUPON_VALUE = 1.00;

    @Override
    public Money discountOf(Money subtotal) {
        return Money.of(COUPON_VALUE);
    }
}

// Concrete strategy for no discount
class NoDiscount implements DiscountPolicy {
    @Override
    public Money discountOf(Money subtotal) {
        return Money.zero();
    }
}

