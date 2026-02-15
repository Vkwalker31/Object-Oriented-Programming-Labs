package com.logistics.transport;

import com.logistics.domain.Transport;
import lombok.Getter;

/**
 * Класс Train - конкретная реализация транспорта (Поезд).
 * GRASP: Information Expert - знает свои параметры (стоимость 5 $/км, скорость 60 км/ч, тип Land) и отвечает за расчет времени доставки.
 * GoF: Polymorphism - реализует общий интерфейс Transport.
 */

@Getter
public class Train implements Transport {

    private static final String NAME = "Поезд";
    private static final String TYPE = "Land";
    private static final double COST_PER_KM = 5.0;
    private static final double SPEED = 60.0;

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
