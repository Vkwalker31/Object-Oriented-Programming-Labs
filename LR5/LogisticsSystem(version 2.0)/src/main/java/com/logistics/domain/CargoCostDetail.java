package com.logistics.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Класс CargoCostDetail — детализация стоимости по одному грузу в расчёте доставки.
 * GRASP: Information Expert — хранит название груза, массу, тариф и итог по грузу.
 */

@Getter
@AllArgsConstructor
public class CargoCostDetail {
    final String cargoName;
    final double massKg;
    final double costPerKg;
    final double totalCost;
}
