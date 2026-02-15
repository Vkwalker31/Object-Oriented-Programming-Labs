package com.logistics.builder;

import com.logistics.domain.Cargo;
import com.logistics.domain.DeliveryOrder;
import com.logistics.domain.Transport;
import com.logistics.exception.LogisticsException;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс DeliveryOrderBuilder — отвечает за пошаговое конструирование сложного объекта заказа.
 * GoF: Builder & GRASP: Creator — знает структуру DeliveryOrder и отвечает за инициализацию всех его компонентов, обеспечивая разделение процесса конструирования и итогового представления.
 */

public class DeliveryOrderBuilder {

    private final String orderId;
    private final List<Cargo> cargo;
    private Transport transport;
    private double distance;
    private Exception error;

    /**
     * Конструктор DeliveryOrderBuilder — инициализирует процесс сборки, устанавливая уникальный идентификатор orderId.
     */
    
    public DeliveryOrderBuilder(String orderId) {
        this.orderId = orderId;
        this.cargo = new ArrayList<>();
        this.distance = 0;
    }

    /**
     * Метод withCargo — добавляет груз в список компонентов заказа.
     * GRASP: High Cohesion — отвечает только за добавление данных. 
     * GoF: Prototype — отвечает за клонирование объекта cargo для обеспечения независимости состояния заказа от внешних изменений.
     */
    
    public DeliveryOrderBuilder withCargo(Cargo cargo) {
        if (error != null) return this;

        if (cargo == null) {
            error = new IllegalArgumentException("Груз не может быть null");
            return this;
        }

        try {
            this.cargo.add(cargo.clone());
        } catch (CloneNotSupportedException e) {
            error = new RuntimeException("Ошибка при клонировании груза", e);
        }

        return this;
    }

    /**
     * Метод withTransport — устанавливает транспортное средство для заказа.
     * GRASP: High Cohesion — отвечает за ассоциацию конкретного объекта transport с текущим билдером.
     */
    
    public DeliveryOrderBuilder withTransport(Transport transport) {
        if (error != null) return this;

        if (transport == null) {
            error = new IllegalArgumentException("Транспорт не может быть null");
            return this;
        }

        this.transport = transport;
        return this;
    }

    /**
     * Метод withDistance — устанавливает протяженность маршрута.
     * GRASP: High Cohesion — отвечает за фиксацию параметра distance, необходимого для логистических расчетов.
     */
    
    public DeliveryOrderBuilder withDistance(double distance) {
        if (error != null) return this;

        if (distance <= 0) {
            error = new IllegalArgumentException(
                    String.format("Расстояние должно быть > 0, получено: %.2f", distance)
            );
            return this;
        }

        this.distance = distance;
        return this;
    }

    /**
     * Метод validate — выполняет финальную проверку согласованности данных.
     * знает правила валидности заказа и отвечает за предотвращение создания некорректного объекта DeliveryOrder.
     */
    
    private void validate() throws LogisticsException {
        if (error != null) {
            throw new LogisticsException("Ошибка построения заказа: " + error.getMessage(), error);
        }

        if (transport == null) {
            throw new LogisticsException("Транспорт не установлен");
        }

        if (cargo.isEmpty()) {
            throw new LogisticsException("Хотя бы один груз должен быть добавлен");
        }

        if (distance == 0) {
            throw new LogisticsException("Расстояние не установлено");
        }
    }

    /**
     * Метод build — завершает процесс конструирования.
     * GRASP: Creator — отвечает за инстанцирование итогового объекта DeliveryOrder на основе накопленных данных.
     */
    
    public DeliveryOrder build() throws LogisticsException {
        validate();
        return new DeliveryOrder(orderId, new ArrayList<>(cargo), transport, distance);
    }
}
