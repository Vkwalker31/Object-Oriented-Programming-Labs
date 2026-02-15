package com.logistics.io.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс DeliveryOptionResult — DTO одного варианта доставки (название транспорта, цена, скорость, время).
 * Используется для выдачи списка вариантов при неуказанном транспорте и для экспорта/фильтрации/сортировки.
 * GRASP: Information Expert — хранит поля ответа для фильтрации и сортировки (GoF: Strategy, Iterator).
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryOptionResult {
    private String transportName;
    private String transportType;
    private double totalCost;
    private double deliveryTimeHours;
    private double speed;
}
