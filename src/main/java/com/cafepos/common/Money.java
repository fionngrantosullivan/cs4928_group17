package com.cafepos.common;
import java.math.BigDecimal;
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
        BigDecimal scaled = a.setScale(2, java.math.RoundingMode.HALF_UP);

        if (scaled.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount cannot be negative");
        }
        this.amount = scaled;
    }

    public Money add(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("other required");
        }
        return new Money(this.amount.add(other.amount));
    }

    public Money multiply(int qty) {
        if (qty < 0) {
            throw new IllegalArgumentException("quantity cannot be negative");
        }
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
