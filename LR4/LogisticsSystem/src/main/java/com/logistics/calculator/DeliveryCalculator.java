package com.logistics.calculator;

import com.logistics.domain.*;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс DeliveryCalculator — отвечает за проведение комплексных расчетов стоимости доставки.
 * GRASP: Information Expert & Pure Fabrication — знает алгоритмы ценообразования и отвечает за вычисление итоговой суммы на основе параметров заказа, являясь искусственным сервисным классом для обеспечения высокой связности.
 * Расчет выполняется по формуле: P = Σ (c_i × m_i) + r × p_{delivery}
 * где:
 *   c_i — стоимость перевозки за единицу массы для конкретного типа груза i;
 *   m_i — общая масса груза i;
 *   r — общее расстояние маршрута;
 *   p_{delivery} — тарифная ставка выбранного транспорта за единицу расстояния.
 */

@RequiredArgsConstructor
public class DeliveryCalculator {

    private final DeliveryOrder order;

    public double calculateTotalCost() {
        double cargoCost = order.getTotalCargoCost();
        double transportCost = order.getDistance() * order.getTransport().getCostPerKm();
        return cargoCost + transportCost;
    }

    public double calculateDeliveryTime() {
        return order.getTransport().calculateDeliveryTime(order.getDistance());
    }

    public CostBreakdown getDetailedBreakdown() {
        List<CargoCostDetail> cargoDetails = new ArrayList<>();
        double totalCargoCost = 0;

        for (Cargo cargo : order.getCargoList()) {
            CargoCostDetail detail = new CargoCostDetail(
                    cargo.getName(),
                    cargo.getTotalMass(),
                    cargo.getCostPerKg(),
                    cargo.getTotalCost()
            );
            cargoDetails.add(detail);
            totalCargoCost += detail.getTotalCost();
        }

        // Транспортные расходы
        // GRASP: Information Expert - транспорт сам знает свою цену
        double transportCost = order.getDistance() * order.getTransport().getCostPerKm();
        double totalCost = totalCargoCost + transportCost;
        double deliveryTime = calculateDeliveryTime();

        return CostBreakdown.builder()
                .cargoDetails(cargoDetails)
                .totalCargoCost(totalCargoCost)
                .transportCost(transportCost)
                .totalCost(totalCost)
                .deliveryTimeHours(deliveryTime)
                .build();
    }
}
