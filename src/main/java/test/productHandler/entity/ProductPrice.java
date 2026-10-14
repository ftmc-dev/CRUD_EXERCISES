package test.productHandler.entity;

import java.math.BigDecimal;

public record ProductPrice(BigDecimal amount, PriceCurrency currency){
    public static ProductPrice createProductPrice(BigDecimal amount, PriceCurrency currency){
        return new ProductPrice(amount, currency);
    }

    public static ProductPrice updateProductPrice(BigDecimal amount, PriceCurrency currency){
        return new ProductPrice(amount, currency);
    }
}