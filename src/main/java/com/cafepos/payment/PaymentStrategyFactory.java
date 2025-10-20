package com.cafepos.payment;

public class PaymentStrategyFactory {
    public static PaymentStrategy create(String paymentType) {
        if (paymentType == null) {
            return new UnknownPayment();
        }

        return switch (paymentType.toUpperCase()) {
            case "CASH" -> new CashPayment();
            case "CARD" -> new CardPayment();
            case "WALLET" -> new WalletPayment();
            default -> new UnknownPayment();
        };
    }
}