package com.logistics.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

/**
 * Класс CostBreakdown - отвечает за детализацию и итоговое представление структуры цены.
 * GRASP: Information Expert - обладает всей информацией о составляющих затрат и отвечает за подготовку полного расчета и форматирование итогового result.
 */

@Getter
@AllArgsConstructor
@Builder
public class CostBreakdown {

    private final List<CargoCostDetail> cargoDetails;
    private final double totalCargoCost;
    private final double transportCost;
    private final double totalCost;
    private final double deliveryTimeHours;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n╔═════════════════════════════════════════════╗\n");
        sb.append("║          РАСЧЁТ СТОИМОСТИ ДОСТАВКИ          ║\n");
        sb.append("╚═════════════════════════════════════════════╝\n\n");

        sb.append("📦 ГРУЗЫ:\n");
        for (CargoCostDetail detail : cargoDetails) {
            sb.append(String.format("  • %s\n", detail.cargoName));
            sb.append(String.format("    Масса: %.2f кг, Тариф: $%.2f/кг\n", detail.massKg, detail.costPerKg));
            sb.append(String.format("    Стоимость: $%.2f\n\n", detail.totalCost));
        }

        sb.append(String.format("Грузы (Σ c_i × m_i):     $%.2f\n", totalCargoCost));
        sb.append(String.format("Транспорт (r × p):       $%.2f\n", transportCost));
        sb.append("─────────────────────────────────────────────\n");
        sb.append(String.format("ИТОГО ($):                 $%.2f\n\n", totalCost));
        sb.append(String.format("Время доставки:           %.2f часов\n", deliveryTimeHours));

        return sb.toString();
    }
}

