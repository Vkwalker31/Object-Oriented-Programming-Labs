package com.logistics.io.sort;

import com.logistics.io.dto.DeliveryOptionResult;

/**
 * Класс ByPriceSortStrategy — сортировка по цене (totalCost).
 * GoF: Strategy — конкретная стратегия сортировки.
 */

public class ByPriceSortStrategy implements SortStrategy {

    private final boolean ascending;

    public ByPriceSortStrategy() {
        this(true);
    }

    public ByPriceSortStrategy(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public int compare(DeliveryOptionResult a, DeliveryOptionResult b) {
        int c = Double.compare(a.getTotalCost(), b.getTotalCost());
        return ascending ? c : -c;
    }
}
