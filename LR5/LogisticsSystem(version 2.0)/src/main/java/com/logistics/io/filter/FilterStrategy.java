package com.logistics.io.filter;

import com.logistics.io.dto.DeliveryOptionResult;

/**
 * Интерфейс FilterStrategy — стратегия фильтрации вариантов доставки.
 * GoF: Strategy — инкапсулирует критерий отбора (по полям ответа).
 */

@FunctionalInterface
public interface FilterStrategy {
    boolean test(DeliveryOptionResult option);
}
