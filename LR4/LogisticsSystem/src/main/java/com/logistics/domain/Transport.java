package com.logistics.domain;

/**
 * Интерфейс Transport - контракт для всех видов транспорта.
 * GRASP: Information Expert - транспорт знает свои параметры
 * GoF: Polymorphism - разные транспорты имеют одинаковый интерфейс
 */

public interface Transport {
    String getName();
    String getType();
    double getCostPerKm();
    double getSpeed();
    double calculateDeliveryTime(double distance);
}
