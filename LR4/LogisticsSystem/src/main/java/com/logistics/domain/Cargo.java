package com.logistics.domain;

/**
 * Интерфейс Cargo - контракт для всех типов грузов.
 * GRASP: Information Expert - груз знает свои параметры
 * GoF: Polymorphism - разные грузы имеют одинаковый интерфейс
 * GoF: Prototype - поддерживает клонирование
 */

public interface Cargo extends Cloneable {
    String getName();
    double getMassPerUnit();
    double getCostPerKg();
    int getQuantity();
    void setQuantity(int quantity);
    double getTotalMass();
    double getTotalCost();
    com.logistics.domain.Cargo clone() throws CloneNotSupportedException;
}
