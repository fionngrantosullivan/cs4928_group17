package com.cafepos.payment;

import com.cafepos.common.Money;
import com.cafepos.domain.Order;

    public interface PaymentStrategy {
        void processPayment(Money amount);

        void pay(Order order);
    }
