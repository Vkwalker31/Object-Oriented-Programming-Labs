package com.logistics.io.sort;

import com.logistics.io.dto.DeliveryOptionResult;

import java.util.Comparator;

/**
 * Класс ByNameSortStrategy — сортировка по названию транспорта.
 * GoF: Strategy — конкретная стратегия сортировки.
 */

public class ByNameSortStrategy implements SortStrategy {

    private final boolean ascending;

    public ByNameSortStrategy() {
        this(true);
    }

    public ByNameSortStrategy(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public int compare(DeliveryOptionResult a, DeliveryOptionResult b) {
        String na = a.getTransportName() != null ? a.getTransportName() : "";
        String nb = b.getTransportName() != null ? b.getTransportName() : "";
        int c = na.compareToIgnoreCase(nb);
        return ascending ? c : -c;
    }
}
