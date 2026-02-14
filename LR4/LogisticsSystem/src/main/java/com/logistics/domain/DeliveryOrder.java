package com.logistics.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс DeliveryOrder - представляет сущность заказа на доставку.
 * GRASP: Information Expert - знает состав грузов, выбранный транспорт и расстояние (distance).
 * Отвечает за расчет агрегированных показателей: общая масса и итоговая стоимость заказа.
 * GRASP: Entity - является ключевой бизнес-сущностью, представляющей реальный процесс доставки.
 */

@Getter
@AllArgsConstructor
public class DeliveryOrder {

    private final String id;
    private final List<Cargo> cargo;
    private final Transport transport;
    private final double distance;

    public double getTotalCargoMass() {
        return cargo.stream()
                .mapToDouble(Cargo::getTotalMass)
                .sum();
    }

    public double getTotalCargoCost() {
        return cargo.stream()
                .mapToDouble(Cargo::getTotalCost)
                .sum();
    }

    public List<Cargo> getCargoList() {
        return Collections.unmodifiableList(cargo);
    }

    public int getCargoCount() {
        return cargo.size();
    }

    public String getCargoSummary() {
        return cargo.stream()
                .map(Cargo::getName)
                .collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return String.format(
                "Order %s: %s -> %s (расстояние: %.2f км, масса: %.2f кг)",
                id, transport.getName(), getCargoSummary(), distance, getTotalCargoMass()
        );
    }
}
