package com.cafepos.pricing;

// Factory to replace string-based discount code branching
public class DiscountPolicyFactory {
    public static DiscountPolicy create(String discountCode) {
        if (discountCode == null || discountCode.equalsIgnoreCase("NONE")) {
            return new NoDiscount();
        } else if (discountCode.equalsIgnoreCase("LOYAL5")) {
            return new LoyaltyDiscount();
        } else if (discountCode.equalsIgnoreCase("COUPON1")) {
            return new FixedCouponDiscount();
        } else {
            return new NoDiscount();
        }
    }
}
