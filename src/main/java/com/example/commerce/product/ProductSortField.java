package com.example.commerce.product;

public enum ProductSortField {

    PRICE("basePrice"),
    INVENTORY("inventory"),
    NAME("name");

    private final String field;

    ProductSortField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
