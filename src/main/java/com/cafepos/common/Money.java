package com.cafepos.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money implements Comparable<Money> {

    public BigDecimal getAmount() {
        return amount;
    }

    private final BigDecimal amount;

    public static Money of(double value) {
        return new Money(BigDecimal.valueOf(value));
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    private Money(BigDecimal a) {
        if (a == null) throw new IllegalArgumentException("amount required");
        this.amount = a.setScale(2, RoundingMode.HALF_UP);
    }

    public Money add(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("other required");
        }
        return new Money(this.amount.add(other.amount));
    }

    public Money multiply(int qty) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(qty)));
    }

    @Override
    public int compareTo(Money other) {
        if (other == null) {
            throw new NullPointerException("Cannot compare to null");
        }
        return this.amount.compareTo(other.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return "€" + amount.toString();
    }
}
