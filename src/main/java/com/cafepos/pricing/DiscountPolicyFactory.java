package com.cafepos.pricing;

import com.cafepos.common.Money;

public class DiscountPolicyFactory {
    public static DiscountPolicy create(String discountCode) {
        if (discountCode == null || discountCode.equalsIgnoreCase("NONE")) {
            return new NoDiscount();
        } else if (discountCode.equalsIgnoreCase("LOYAL5")) {
            return new LoyaltyDiscount(5);
        } else if (discountCode.equalsIgnoreCase("COUPON1")) {
            return new FixedCouponDiscount(Money.of(1.00));
        } else {
            return new NoDiscount();
        }
    }
}