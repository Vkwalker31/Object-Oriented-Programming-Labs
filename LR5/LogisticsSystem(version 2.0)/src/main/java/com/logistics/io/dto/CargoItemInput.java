package com.logistics.io.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Класс CargoItemInput — DTO для одного пункта груза при загрузке из файла.
 * Содержит тип груза и количество единиц.
 * Используется модулем работы с внешними данными (Facade, Adapter).
 * GRASP: DTO — передача данных между слоями.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CargoItemInput {
    private String type;
    private int quantity;
}
