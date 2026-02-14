package com.logistics.transport;

import com.logistics.domain.Transport;
import lombok.Getter;

/**
 * Класс Helicopter - конкретная реализация транспорта (Вертолет).
 * GRASP: Information Expert - знает свои параметры (стоимость $$200$$ $/км, скорость $$250$$ км/ч, тип $$Air$$) и отвечает за расчет времени доставки.
 * GoF: Polymorphism - реализует общий интерфейс Transport.
 */

@Getter
public class Helicopter implements Transport {

    private static final String NAME = "Вертолет";
    private static final String TYPE = "Air";
    private static final double COST_PER_KM = 200.0;
    private static final double SPEED = 250.0;

    @Override public String getName() { return NAME; }
    @Override public String getType() { return TYPE; }
    @Override public double getCostPerKm() { return COST_PER_KM; }
    @Override public double getSpeed() { return SPEED; }

    @Override
    public double calculateDeliveryTime(double distance) {
        if (distance < 0) throw new IllegalArgumentException("Расстояние не может быть отрицательным");
        return distance / SPEED;
    }

    @Override
    public String toString() {
        return String.format("%s (тип: %s, $%.2f/км, %.0f км/ч)",
                NAME, TYPE, COST_PER_KM, SPEED);
    }
}
