package com.logistics.io.sort;

import com.logistics.io.dto.DeliveryOptionResult;

import java.util.Comparator;
import java.util.List;

/**
 * Интерфейс SortStrategy — стратегия сортировки вариантов доставки.
 * GoF: Strategy — инкапсулирует алгоритм сравнения; сортировки можно комбинировать через thenComparing.
 */

public interface SortStrategy extends Comparator<DeliveryOptionResult> {

    /**
     * Применить сортировку к списку (изменяет список).
     */
    default void apply(List<DeliveryOptionResult> list) {
        list.sort(this);
    }

    /**
     * Комбинировать с другой стратегией: сначала this, затем other.
     */
    default SortStrategy then(SortStrategy other) {
        return (a, b) -> {
            int c = this.compare(a, b);
            return c != 0 ? c : other.compare(a, b);
        };
    }
}
