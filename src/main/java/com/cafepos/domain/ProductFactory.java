package com.cafepos.domain;

import com.cafepos.common.Money;
import com.cafepos.common.Product;

public class ProductFactory {

    public Product create(String code) {
        switch (code) {
            case "ESP+SHOT+OAT":
                return new Product("Espresso (Extra Shot, Oat Milk)", 3.00 + 0.50 + 0.75) {
                    @Override
                    public String id() {
                        return "";
                    }

                    @Override
                    public String name() {
                        return "";
                    }

                    @Override
                    public Money basePrice() {
                        return null;
                    }
                };
            case "LAT+L":
                return new Product("Large Latte", 3.50 + 0.50); // base + size upcharge
            default:
                throw new IllegalArgumentException("Unknown product code: " + code);
        }
    }
}
