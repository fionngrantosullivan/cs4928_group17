package com.cafepos.pricing;

import com.cafepos.common.Money;
import java.math.BigDecimal;

public interface DiscountPolicy {
    Money discountOf(Money subtotal);

    // Helper method for percentage-based discounts
    default Money calculatePercentage(Money amount, int percent) {
        return Money.of(amount.asBigDecimal()
                .multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100)));
    }
}