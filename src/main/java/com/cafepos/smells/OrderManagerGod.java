package com.cafepos.smells;

import com.cafepos.common.Money;
import com.cafepos.factory.ProductFactory;
import com.cafepos.common.Product;
import com.cafepos.payment.PaymentStrategy;
import com.cafepos.payment.PaymentStrategyFactory;

import java.math.BigDecimal;

public class OrderManagerGod {
    // Global/Static State: Mutable static variable accessible from anywhere
    public static int TAX_PERCENT = 10;
    // Global/Static State: Another mutable static variable for side effects
    public static String LAST_DISCOUNT_CODE = null;

    // God Class & Long Method: Single method doing too many responsibilities
    public static String process(String recipe, int qty, String
            paymentType, String discountCode, boolean printReceipt) {

        // God Class & Long Method: Responsibility #1 - Product creation
        ProductFactory factory = new ProductFactory();
        Product product = factory.create(recipe);

        // God Class & Long Method: Responsibility #2 - Pricing logic
        Money unitPrice;
        try {
            var priced = product instanceof com.cafepos.common.Priced
                    p ? p.price() : product.basePrice();
            unitPrice = priced;
        } catch (Exception e) {
            unitPrice = product.basePrice();
        }

        if (qty <= 0) qty = 1;

        // God Class & Long Method: Responsibility #3 - Subtotal calculation
        Money subtotal = unitPrice.multiply(BigDecimal.valueOf(qty));

        // God Class & Long Method: Responsibility #4 - Discount application
        Money discount = Money.zero();
        if (discountCode != null) {
            // Primitive Obsession: Using raw strings to represent discount types
            if (discountCode.equalsIgnoreCase("LOYAL5")) {
                // Duplicated Logic & Shotgun Surgery: Inline Money/BigDecimal math for discount
                discount = Money.of(subtotal.asBigDecimal()
                        .multiply(java.math.BigDecimal.valueOf(5))
                        .divide(java.math.BigDecimal.valueOf(100)));
            } else if (discountCode.equalsIgnoreCase("COUPON1")) {
                // Primitive Obsession: Magic number 1.00 hardcoded for coupon value
                discount = Money.of(1.00);
            } else if (discountCode.equalsIgnoreCase("NONE")) {
                discount = Money.zero();
            } else {
                discount = Money.zero();
            }
            // Global/Static State: Side effect of setting static variable
            LAST_DISCOUNT_CODE = discountCode;
        }

        // God Class & Long Method: Responsibility #5 - Applying discount
        // Duplicated Logic: Inline Money/BigDecimal arithmetic scattered throughout
        Money discounted =
                Money.of(subtotal.asBigDecimal().subtract(discount.asBigDecimal()));
        if (discounted.asBigDecimal().signum() < 0) discounted =
                Money.zero();

        // God Class & Long Method: Responsibility #6 - Tax calculation
        // Shotgun Surgery: Tax rate embedded inline; change requires modifying this method
        // Primitive Obsession: TAX_PERCENT as static primitive; magic numbers in calculation
        var tax = Money.of(discounted.asBigDecimal()
                .multiply(java.math.BigDecimal.valueOf(TAX_PERCENT))
                .divide(java.math.BigDecimal.valueOf(100)));

        var total = discounted.add(tax);

        // God Class & Long Method: Responsibility #7 - Payment I/O & side effects
        // Feature Envy/Shotgun Surgery: Payment method logic embedded inline
        // Primitive Obsession: Using raw strings for payment types
        PaymentStrategy paymentStrategy = PaymentStrategyFactory.create(paymentType);
        paymentStrategy.processPayment(total);

        // God Class & Long Method: Responsibility #8 - Receipt formatting
        StringBuilder receipt = new StringBuilder();
        receipt.append("Order (").append(recipe).append(") x").append(qty).append("\n");
        receipt.append("Subtotal: ").append(subtotal).append("\n");
        if (discount.asBigDecimal().signum() > 0) {
            receipt.append("Discount: -").append(discount).append("\n");
        }
        // Shotgun Surgery: Tax display format coupled to this method; change requires editing here
        receipt.append("Tax (").append(TAX_PERCENT).append("%): ").append(tax).append("\n");
        receipt.append("Total: ").append(total);

        String out = receipt.toString();

        // God Class & Long Method: Responsibility #9 - Conditional printing (I/O side effect)
        if (printReceipt) {
            System.out.println(out);
        }

        return out;
    }
}