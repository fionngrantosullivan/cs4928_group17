package com.cafepos.catalog;

import com.cafepos.common.Product;

import java.util.*;

public final class InMemoryCatalog implements Catalog {

    private final Map<String, Product> byId = new HashMap<>();

    @Override public void add(Product p) {
        // overrides the add method from the Catalog interface
        // stores products by ID as the key in the Map
        if (p == null) throw new
                IllegalArgumentException("product required");
        byId.put(p.id(), p);
    }

    @Override public Optional<Product> findById(String id) {
        // returns as Optional as there may or may not be a Product at that ID, i.e. ensures an exception won't be thrown if not
        return Optional.ofNullable(byId.get(id));
    }
}
