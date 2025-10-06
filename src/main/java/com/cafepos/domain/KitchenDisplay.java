package com.cafepos.domain;

public final class KitchenDisplay implements OrderObserver {
    @Override
    public void updated(Order order, String eventType) {
        if (eventType.equals("itemAdded")) {
            System.out.println("[Kitchen] Order #" + order.id() + ": Item added.");
        } else if (eventType.equals("paid")) {
            System.out.println("[Kitchen] Order #" + order.id() + ": Payment received.");
        }
    }
}
