package com.logistics.io.filter;

import com.logistics.io.dto.DeliveryOptionResult;

/**
 * Класс ByMaxPriceFilter — фильтр по максимальной допустимой цене.
 * GoF: Strategy — конкретная стратегия фильтрации.
 */

public class ByMaxPriceFilter implements FilterStrategy {

    private final double maxPrice;

    public ByMaxPriceFilter(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    @Override
    public boolean test(DeliveryOptionResult option) {
        return option != null && option.getTotalCost() <= maxPrice;
    }
}
