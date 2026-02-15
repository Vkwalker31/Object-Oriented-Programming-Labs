package com.logistics.cargo;

import com.logistics.domain.Cargo;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс ElectronicsCargo - конкретная реализация груза (Электроника).
 * GRASP: Information Expert - знает свои специфические параметры (масса 1.5 кг, цена 50 $/кг) и отвечает за расчет своей стоимости.
 * GoF: Polymorphism - реализует общий интерфейс Cargo.
 * GoF: Prototype - поддерживает клонирование для создания копий электроники.
 */

@Getter
@Setter
public class ElectronicsCargo implements Cargo {

    private static final String NAME = "Электроника";
    private static final double MASS_PER_UNIT = 1.5;
    private static final double COST_PER_KG = 50.0;

    private int quantity;

    public ElectronicsCargo() {
        this.quantity = 0;
    }

    public ElectronicsCargo(int quantity) {
        setQuantity(quantity);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double getMassPerUnit() {
        return MASS_PER_UNIT;
    }

    @Override
    public double getCostPerKg() {
        return COST_PER_KG;
    }

    @Override
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException(
                    String.format("Количество не может быть отрицательным: %d", quantity)
            );
        }
        this.quantity = quantity;
    }

    @Override
    public double getTotalMass() {
        return MASS_PER_UNIT * quantity;
    }

    @Override
    public double getTotalCost() {
        return COST_PER_KG * getTotalMass();
    }

    @Override
    public Cargo clone() throws CloneNotSupportedException {
        return new ElectronicsCargo(this.quantity);
    }

    @Override
    public String toString() {
        return String.format(
                "%s (кол-во: %d ед., масса: %.2f кг, стоимость: $%.2f)",
                NAME, quantity, getTotalMass(), getTotalCost()
        );
    }
}

