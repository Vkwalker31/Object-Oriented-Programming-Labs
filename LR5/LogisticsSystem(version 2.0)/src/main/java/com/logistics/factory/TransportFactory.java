package com.logistics.factory;

import com.logistics.domain.Transport;

/**
 * Интерфейс TransportFactory - определяет стандарт создания объектов транспортных средств.
 * GoF: Abstract Factory & GRASP: Creator - отвечает за порождение объектов Transport и обладает информацией о типе создаваемого транспорта.
 */

public interface TransportFactory {
    Transport createTransport();
    String getTransportType();
}
