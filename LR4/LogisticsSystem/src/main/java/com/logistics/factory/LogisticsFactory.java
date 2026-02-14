package com.logistics.factory;

import com.logistics.cargo.*;
import com.logistics.transport.*;
import com.logistics.domain.Cargo;
import com.logistics.domain.Transport;
import com.logistics.exception.LogisticsException;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс LogisticsFactory — центральный узел управления порождением объектов логистической системы.
 * GoF: Abstract Factory, GRASP: Creator & Indirection — знает все доступные типы фабрик и отвечает за создание семейств Cargo и Transport, скрывая детали реализации от клиента.
 */

public class LogisticsFactory {

    private static LogisticsFactory instance;
    private final Map<String, CargoFactory> cargoFactories;
    private final Map<String, TransportFactory> transportFactories;

    private LogisticsFactory() {
        this.cargoFactories = new HashMap<>();
        this.transportFactories = new HashMap<>();
        registerFactories();
    }

    /**
     * GoF: Singleton — отвечает за обеспечение существования единственного экземпляра LogisticsFactory в системе.
     */

    public static synchronized LogisticsFactory getInstance() {
        if (instance == null) {
            instance = new LogisticsFactory();
        }
        return instance;
    }

    /**
     * Метод registerFactories — инициализирует внутренние реестры фабрик.
     * знает о соответствии строковых идентификаторов конкретным реализациям CargoFactory и TransportFactory.
     */

    private void registerFactories() {

        cargoFactories.put("electronics", new ElectronicsCargoFactory());
        cargoFactories.put("clothing", new ClothingCargoFactory());
        cargoFactories.put("equipment", new EquipmentCargoFactory());
        cargoFactories.put("perishable", new PerishableCargoFactory());

        transportFactories.put("truck", new TruckFactory());
        transportFactories.put("train", new TrainFactory());
        transportFactories.put("tanker", new TankerFactory());
        transportFactories.put("airplane", new AirplaneFactory());
        transportFactories.put("helicopter", new HelicopterFactory());
    }

    /**
     * GRASP: Creator & Indirection — отвечает за создание объекта Cargo через посредника.
     * Находит нужную фабрику по type и возвращает созданный груз в количестве quantity.
     */

    public Cargo createCargo(String type, int quantity) throws LogisticsException {
        CargoFactory factory = cargoFactories.get(type.toLowerCase());

        if (factory == null) {
            throw new LogisticsException(
                    "Неизвестный тип груза: " + type +
                            ". Доступные: " + String.join(", ", cargoFactories.keySet())
            );
        }

        return factory.createCargo(quantity);
    }

    /**
     * GRASP: Creator & Indirection — отвечает за создание объекта Transport.
     * Изолирует клиента от логики выбора конкретного типа транспорта на основе переданного type.
     */

    public Transport createTransport(String type) throws LogisticsException {
        TransportFactory factory = transportFactories.get(type.toLowerCase());

        if (factory == null) {
            throw new LogisticsException(
                    "Неизвестный тип транспорта: " + type +
                            ". Доступные: " + String.join(", ", transportFactories.keySet())
            );
        }

        return factory.createTransport();
    }

    public String[] getAvailableCargoTypes() {
        return cargoFactories.keySet().toArray(new String[0]);
    }

    public String[] getAvailableTransportTypes() {
        return transportFactories.keySet().toArray(new String[0]);
    }
}

// ===== CARGO FACTORIES =====

class ElectronicsCargoFactory implements CargoFactory {
    @Override
    public Cargo createCargo(int quantity) {
        return new ElectronicsCargo(quantity);
    }
    @Override
    public String getCargoType() { return "electronics"; }
}

class ClothingCargoFactory implements CargoFactory {
    @Override
    public Cargo createCargo(int quantity) {
        return new ClothingCargo(quantity);
    }
    @Override
    public String getCargoType() { return "clothing"; }
}

class EquipmentCargoFactory implements CargoFactory {
    @Override
    public Cargo createCargo(int quantity) {
        return new EquipmentCargo(quantity);
    }
    @Override
    public String getCargoType() { return "equipment"; }
}

class PerishableCargoFactory implements CargoFactory {
    @Override
    public Cargo createCargo(int quantity) {
        return new PerishableCargo(quantity);
    }
    @Override
    public String getCargoType() { return "perishable"; }
}

// ===== TRANSPORT FACTORIES =====

class TruckFactory implements TransportFactory {
    @Override
    public Transport createTransport() { return new Truck(); }
    @Override
    public String getTransportType() { return "truck"; }
}

class TrainFactory implements TransportFactory {
    @Override
    public Transport createTransport() { return new Train(); }
    @Override
    public String getTransportType() { return "train"; }
}

class TankerFactory implements TransportFactory {
    @Override
    public Transport createTransport() { return new Tanker(); }
    @Override
    public String getTransportType() { return "tanker"; }
}

class AirplaneFactory implements TransportFactory {
    @Override
    public Transport createTransport() { return new Airplane(); }
    @Override
    public String getTransportType() { return "airplane"; }
}

class HelicopterFactory implements TransportFactory {
    @Override
    public Transport createTransport() { return new Helicopter(); }
    @Override
    public String getTransportType() { return "helicopter"; }
}
