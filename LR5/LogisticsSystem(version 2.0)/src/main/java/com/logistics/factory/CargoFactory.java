package com.logistics.factory;

import com.logistics.domain.Cargo;

/**
 * Интерфейс CargoFactory - определяет стандарт создания объектов семейства грузов.
 * GoF: Abstract Factory & GRASP: Creator - обладает знаниями для создания объектов Cargo в количестве quantity и отвечает за предоставление информации о типе создаваемого груза.
 */

public interface CargoFactory {
    Cargo createCargo(int quantity);
    String getCargoType();
}
