package com.logistics.io.sort;

import com.logistics.io.dto.DeliveryOptionResult;

/**
 * Класс BySpeedSortStrategy — сортировка по скорости транспорта.
 * GoF: Strategy — конкретная стратегия сортировки.
 */

public class BySpeedSortStrategy implements SortStrategy {

    private final boolean ascending;

    public BySpeedSortStrategy() {
        this(true);
    }

    public BySpeedSortStrategy(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public int compare(DeliveryOptionResult a, DeliveryOptionResult b) {
        int c = Double.compare(a.getSpeed(), b.getSpeed());
        return ascending ? c : -c;
    }
}
