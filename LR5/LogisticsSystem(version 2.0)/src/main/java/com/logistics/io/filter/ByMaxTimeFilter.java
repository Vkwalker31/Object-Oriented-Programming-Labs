package com.logistics.io.filter;

import com.logistics.io.dto.DeliveryOptionResult;

/**
 * Класс ByMaxTimeFilter — фильтр по максимальному времени доставки (часы).
 * GoF: Strategy — конкретная стратегия фильтрации.
 */

public class ByMaxTimeFilter implements FilterStrategy {

    private final double maxHours;

    public ByMaxTimeFilter(double maxHours) {
        this.maxHours = maxHours;
    }

    @Override
    public boolean test(DeliveryOptionResult option) {
        return option != null && option.getDeliveryTimeHours() <= maxHours;
    }
}
