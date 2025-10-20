package com.cafepos.checkout;

import com.cafepos.common.Money;
import com.cafepos.common.Priced;
import com.cafepos.common.Product;
import com.cafepos.factory.ProductFactory;
import com.cafepos.pricing.PricingService;
import com.cafepos.pricing.ReceiptPrinter;

import java.math.BigDecimal;

public final class CheckoutService {
    private final ProductFactory factory;
    private final PricingService pricing;
    private final ReceiptPrinter printer;
    private final int taxPercent;

    public CheckoutService(ProductFactory factory, PricingService pricing,
                           ReceiptPrinter printer, int taxPercent) {
        if (factory == null) throw new IllegalArgumentException("factory required");
        if (pricing == null) throw new IllegalArgumentException("pricing required");
        if (printer == null) throw new IllegalArgumentException("printer required");
        if (taxPercent < 0) throw new IllegalArgumentException("taxPercent must be >= 0");

        this.factory = factory;
        this.pricing = pricing;
        this.printer = printer;
        this.taxPercent = taxPercent;
    }

    public String checkout(String recipe, int qty) {
        // Create product using factory
        Product product = factory.create(recipe);

        // Clamp quantity to minimum 1
        if (qty <= 0) qty = 1;

        // Get unit price (decorated or base)
        Money unit = (product instanceof Priced p) ? p.price() : product.basePrice();

        // Calculate subtotal
        Money subtotal = unit.multiply(BigDecimal.valueOf(qty));

        // Apply discount and tax via pricing service
        var result = pricing.price(subtotal);

        // Format and return receipt
        return printer.format(recipe, qty, result, taxPercent);
    }
}