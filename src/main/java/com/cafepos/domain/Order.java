package com.cafepos.domain;

import com.cafepos.payment.PaymentStrategy;
import com.cafepos.common.Money;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class Order implements OrderPublisher {
    private final long id;
    private final List<LineItem> items = new ArrayList<>();
    private final List<OrderObserver> observers = new ArrayList<>();

    public Order(long id) {
        this.id = id;
    }

    @Override
    public void register(OrderObserver o) {
        if (o == null) {
            throw new IllegalArgumentException("observer required");
        }
        if (!observers.contains(o)) {
            observers.add(o);
        }
    }

    @Override
    public void unregister(OrderObserver o) {
        // remove does nothing if o is null or not present
        observers.remove(o);
    }

    @Override
    public void notifyObservers(Order order, String eventType) {
        // iterate over a copy to avoid ConcurrentModification if observers change during notification
        for (OrderObserver o : List.copyOf(observers)) {
            o.updated(order, eventType);
        }
    }

    public void addItem(LineItem li) {
        if (li == null) {
            throw new IllegalArgumentException("line item required");
        }
        if (li.quantity() <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }
        items.add(li);
        // announce after state change
        notifyObservers(this, "itemAdded");
    }

    public Money subtotal() {
        // for each LineItem in this order, adds the lineTotal value with the next to get total sum of Order (without tax)
        return items.stream()
                .map(LineItem::lineTotal)
                .reduce(Money.zero(), Money::add);
    }

    public Money taxAtPercent(int percent) {
        if (percent < 0) {
            throw new IllegalArgumentException("tax percent must be >= 0");
        }
        Money subtotal = subtotal();
        return Money.of(
                subtotal.getAmount()
                        .multiply(BigDecimal.valueOf(percent))
                        .divide(BigDecimal.valueOf(100))
                        .doubleValue()
        );
    }

    public Money totalWithTax(int percent) {
        return subtotal().add(taxAtPercent(percent));
    }

    public void pay(PaymentStrategy strategy) {
        if (strategy == null) throw new
                IllegalArgumentException("strategy required");
        strategy.pay(this);
        // announce after payment completes
        notifyObservers(this, "paid");
    }

    public void markReady() {
        // announce ready (no additional state in this simple model)
        notifyObservers(this, "ready");
    }

    public long id() {
        return id;
    }

    @Override
    public String toString() {
        return "Order{id=" + id +
                ", items=" + items.size() +
                ", subtotal=" + subtotal() +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        // first check is if they're the same object in memory, then it compares IDs to complete the check
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return id == order.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // return a read-only copy of the typed items list
    public Collection<LineItem> items() {
        return List.copyOf(items);
    }
}

