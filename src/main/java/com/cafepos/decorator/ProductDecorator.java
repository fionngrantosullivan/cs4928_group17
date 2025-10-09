package com.cafepos.decorator;
import com.cafepos.common.Product;
import com.cafepos.common.Money;

public abstract class ProductDecorator implements Product {
    protected final Product base;

    protected ProductDecorator(Product base) {
        if (base == null) throw new
                IllegalArgumentException("base product required");
        this.base = base;
    }

    @Override
    public String id() {
        return base.id();
    } // id may remain the base product id

    @Override
    public Money basePrice() {
        return base.basePrice();
    } // original price (not total)
    
}
