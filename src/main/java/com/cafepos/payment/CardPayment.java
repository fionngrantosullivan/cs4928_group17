package com.cafepos.payment;

import com.cafepos.domain.Order;

public final class CardPayment implements PaymentStrategy {
    private final String cardNumber;

    public CardPayment(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            throw new IllegalArgumentException("Card number must have at least 4 digits");
        }
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay(Order order) {
        String masked = "****" + cardNumber.substring(cardNumber.length() - 4);
        System.out.println("[Card] Customer paid " + order.totalWithTax(10) + " EUR with card " + masked);
    }
}
