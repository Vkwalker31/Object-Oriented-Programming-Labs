package com.logistics.io.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс InputData — DTO входных данных из файла (груз, расстояние, опционально тип транспорта и пункт назначения).
 * GRASP: DTO — передача данных между слоями; используется фасадом и адаптерами форматов (GoF: Adapter, Facade).
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InputData {
    @Builder.Default
    private List<CargoItemInput> cargo = new ArrayList<>();
    private double distance;
    /**
     * Если null — система вернёт варианты по всем видам транспорта.
     */
    private String transportType;
    private String destination;

    public List<CargoItemInput> getCargo() {
        return cargo != null ? cargo : new ArrayList<>();
    }
}
