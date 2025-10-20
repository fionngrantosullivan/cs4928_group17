package com.cafepos.pricing;

import com.cafepos.common.Money;

public final class LoyaltyDiscount implements DiscountPolicy {
    private final int percent;

    public LoyaltyDiscount(int percent) {
        if (percent < 0) throw new IllegalArgumentException("percent must be >= 0");
        this.percent = percent;
    }

    @Override
    public Money discountOf(Money subtotal) {
        return calculatePercentage(subtotal, percent);
    }
}