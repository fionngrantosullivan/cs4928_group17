package com.cafepos.domain;

public final class DeliveryDesk implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        if (eventType.equals("ready")) {
            System.out.println("[Delivery] Order #" + order.id() + " is ready for delivery.");
        }
    }
}
