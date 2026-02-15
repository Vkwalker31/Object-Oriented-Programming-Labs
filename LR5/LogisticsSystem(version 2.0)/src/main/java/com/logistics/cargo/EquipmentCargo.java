package com.logistics.cargo;

import com.logistics.domain.Cargo;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс EquipmentCargo - конкретная реализация груза (Оборудование).
 * GRASP: Information Expert - знает свои специфические параметры (масса 120 кг, цена 15 $/кг) и отвечает за расчет своей стоимости.
 * GoF: Polymorphism - реализует общий интерфейс Cargo.
 * GoF: Prototype - поддерживает клонирование для создания копий оборудования.
 */

@Getter
@Setter
public class EquipmentCargo implements Cargo {

    private static final String NAME = "Оборудование";
    private static final double MASS_PER_UNIT = 120.0;
    private static final double COST_PER_KG = 15.0;

    private int quantity;

    public EquipmentCargo() { this.quantity = 0; }
    public EquipmentCargo(int quantity) { setQuantity(quantity); }

    @Override public String getName() { return NAME; }
    @Override public double getMassPerUnit() { return MASS_PER_UNIT; }
    @Override public double getCostPerKg() { return COST_PER_KG; }

    @Override
    public void setQuantity(int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Количество не может быть отрицательным");
        this.quantity = quantity;
    }

    @Override public double getTotalMass() { return MASS_PER_UNIT * quantity; }
    @Override public double getTotalCost() { return COST_PER_KG * getTotalMass(); }

    @Override
    public Cargo clone() throws CloneNotSupportedException {
        return new EquipmentCargo(this.quantity);
    }

    @Override
    public String toString() {
        return String.format("%s (кол-во: %d ед., масса: %.2f кг, стоимость: $%.2f)",
                NAME, quantity, getTotalMass(), getTotalCost());
    }
}
