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
        // creates Money object from double value - BigDecimal ensures precision when rounding
        return new Money(BigDecimal.valueOf(value));
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    private Money(BigDecimal a) {
        if (a == null) throw new IllegalArgumentException("amount required");

        // enforce 2 decimal places when rounding
        BigDecimal scaled = a.setScale(2, RoundingMode.HALF_UP);

        // no negative values allowed
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

    public Money multiply(BigDecimal qty) {
        // can't multiply by negative number (since negative Money values aren't allowed)
        if (qty == null) {
            throw new IllegalArgumentException("quantity cannot be negative");
        }
        return new Money(this.amount.multiply(qty));
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
        return amount.toString();
    }

    public BigDecimal asBigDecimal() {
        return amount;
    }

    public double divide(BigDecimal bigDecimal) {
        return 0;
    }
    public static Money of(BigDecimal value) {
        if (value == null) throw new IllegalArgumentException("value required");
        return new Money(value);
    }


    public int signum() {
        return 0;
    }

    public double subtract(Money bigDecimal) {
        return 0;
    }
}
