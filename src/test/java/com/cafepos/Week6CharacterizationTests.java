package com.cafepos;

import com.cafepos.smells.OrderManagerGod;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Week6CharacterizationTests {

    /**
     * Tests a simple order with no discounts paid in cash.
     * Espresso + Shot + Oat (2.50 + 0.80 + 0.50 = 3.80)
     * No discount applied
     * Tax: 10% of 3.80 = 0.38
     * Total: 4.18
     */
    @Test
    void no_discount_cash_payment() {
        // Arrange
        String items = "ESP+SHOT+OAT";
        int quantity = 1;
        String paymentMethod = "CASH";
        String discount = "NONE";
        boolean isVip = false;

        // Act
        String receipt = OrderManagerGod.process(items, quantity, paymentMethod, discount, isVip);

        // Assert
        assertTrue(receipt.startsWith("Order (ESP+SHOT+OAT) x1"),
                "Receipt should start with order description");
        assertTrue(receipt.contains("Subtotal: 3.80"),
                "Subtotal should be 3.80");
        assertTrue(receipt.contains("Tax (10%): 0.38"),
                "Tax should be 0.38 (10% of 3.80)");
        assertTrue(receipt.contains("Total: 4.18"),
                "Total should be 4.18");
    }

    /**
     * Tests loyalty discount applied to card payment.
     * Latte (Large) = 3.20 + 0.70 = 3.90, quantity 2 = 7.80
     * 5% loyalty discount = -0.39
     * Subtotal after discount: 7.41
     * Tax: 10% of 7.41 = 0.74
     * Total: 8.15
     */
    @Test
    void loyalty_discount_card_payment() {
        // Arrange
        String items = "LAT+L";
        int quantity = 2;
        String paymentMethod = "CARD";
        String discount = "LOYAL5";
        boolean isVip = false;

        // Act
        String receipt = OrderManagerGod.process(items, quantity, paymentMethod, discount, isVip);

        // Assert
        assertTrue(receipt.contains("Subtotal: 7.80"),
                "Subtotal should be 7.80 (3.90 x 2)");
        assertTrue(receipt.contains("Discount: -0.39"),
                "Discount should be -0.39 (5% of 7.80)");
        assertTrue(receipt.contains("Tax (10%): 0.74"),
                "Tax should be 0.74 (10% of 7.41)");
        assertTrue(receipt.contains("Total: 8.15"),
                "Total should be 8.15");
    }

    /**
     * Tests coupon with fixed amount discount and quantity clamping.
     * Quantity 0 is clamped to 1 (minimum order quantity)
     * Espresso + Shot = 2.50 + 0.80 = 3.30
     * Coupon1 fixed discount = -1.00
     * Subtotal after discount: 2.30
     * Tax: 10% of 2.30 = 0.23
     * Total: 2.53
     */
    @Test
    void coupon_fixed_amount_and_qty_clamp() {
        // Arrange
        String items = "ESP+SHOT";
        int quantity = 0; // Will be clamped to 1
        String paymentMethod = "WALLET";
        String discount = "COUPON1";
        boolean isVip = false;

        // Act
        String receipt = OrderManagerGod.process(items, quantity, paymentMethod, discount, isVip);

        // Assert
        assertTrue(receipt.contains("Order (ESP+SHOT) x1"),
                "Quantity should be clamped to 1 (from 0)");
        assertTrue(receipt.contains("Subtotal: 3.30"),
                "Subtotal should be 3.30");
        assertTrue(receipt.contains("Discount: -1.00"),
                "Fixed coupon discount should be -1.00");
        assertTrue(receipt.contains("Tax (10%): 0.23"),
                "Tax should be 0.23 (10% of 2.30)");
        assertTrue(receipt.contains("Total: 2.53"),
                "Total should be 2.53");
    }
}