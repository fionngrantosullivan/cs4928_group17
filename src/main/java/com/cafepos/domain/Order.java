package com.cafepos.domain;
import Payment.PaymentStrategy;
import com.cafepos.common.Money;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class Order {
    private final long id;
    private final List<LineItem> items = new ArrayList<>();

    public Order(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public List<LineItem> getItems() {
        return List.copyOf(items); // defensive copy
    }

    // Add item if quantity > 0 (LineItem constructor already enforces this)
    public void addItem(LineItem li) {
        if (li == null) {
            throw new IllegalArgumentException("line item required");
        }
        if (li.quantity() <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }
        items.add(li);
    }

    // Subtotal of all items (already provided in skeleton)
    public Money subtotal() {
        return items.stream()
                .map(LineItem::lineTotal)
                .reduce(Money.zero(), Money::add);
    }

    // Tax calculation
    public Money taxAtPercent(int percent) {
        if (percent < 0) {
            throw new IllegalArgumentException("tax percent must be >= 0");
        }
        Money subtotal = subtotal();
        // percent / 100 → e.g. 10% = 0.10
        return Money.of(
                subtotal.getAmount()
                        .multiply(BigDecimal.valueOf(percent))
                        .divide(BigDecimal.valueOf(100))
                        .doubleValue()
        );
    }

    // Total with tax
    public Money totalWithTax(int percent) {
        return subtotal().add(taxAtPercent(percent));
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
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return id == order.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Collection<Object> items() {
        return List.copyOf(items);
    }

    public void pay(PaymentStrategy strategy) {
        if (strategy == null) throw new
                IllegalArgumentException("strategy required");
        strategy.pay(this);
    }
}

