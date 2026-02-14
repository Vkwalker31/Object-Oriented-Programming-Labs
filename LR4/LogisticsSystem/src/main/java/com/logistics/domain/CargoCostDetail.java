package com.logistics.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CargoCostDetail {
    final String cargoName;
    final double massKg;
    final double costPerKg;
    final double totalCost;
}
