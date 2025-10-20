package com.cafepos.payment;

import com.cafepos.common.Money;
import com.cafepos.domain.Order;

class UnknownPayment implements PaymentStrategy {
    @Override
    public void processPayment(Money amount) {
        System.out.println("[UnknownPayment] " + amount);
    }

    @Override
    public void pay(Order order) {

    }
}