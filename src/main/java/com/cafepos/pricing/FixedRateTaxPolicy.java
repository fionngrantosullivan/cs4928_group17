package com.cafepos.pricing;

import com.cafepos.common.Money;
import java.math.BigDecimal;

public final class FixedRateTaxPolicy implements TaxPolicy {
    private final int percent;

    public FixedRateTaxPolicy(int percent) {
        if (percent < 0) throw new IllegalArgumentException("percent must be >= 0");
        this.percent = percent;
    }

    @Override
    public Money taxOn(Money amount) {
        BigDecimal tax = amount.asBigDecimal()
                .multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100));
        return Money.of(tax);
    }

    public int getPercent() {
        return percent;
    }
}