package com.logistics;

import com.logistics.builder.DeliveryOrderBuilder;
import com.logistics.calculator.DeliveryCalculator;
import com.logistics.factory.LogisticsFactory;
import com.logistics.exception.LogisticsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class LogisticsSystemTest {

    private LogisticsFactory factory;

    @BeforeEach
    void setUp() {
        factory = LogisticsFactory.getInstance();
    }

    @Test
    void testCargoCreation() throws LogisticsException {
        var electronics = factory.createCargo("electronics", 5);

        assertEquals("Электроника", electronics.getName());
        assertEquals(1.5, electronics.getMassPerUnit());
        assertEquals(50.0, electronics.getCostPerKg());
        assertEquals(7.5, electronics.getTotalMass());
        assertEquals(375.0, electronics.getTotalCost());
    }

    @Test
    void testTransportCreation() throws LogisticsException {
        var truck = factory.createTransport("truck");

        assertEquals("Грузовик", truck.getName());
        assertEquals("Land", truck.getType());
        assertEquals(15.0, truck.getCostPerKm());
        assertEquals(80.0, truck.getSpeed());
    }

    @Test
    void testDeliveryOrderBuilder() throws LogisticsException {
        var cargo = factory.createCargo("electronics", 5);
        var transport = factory.createTransport("truck");

        var order = new DeliveryOrderBuilder("ORD-001")
                .withCargo(cargo)
                .withTransport(transport)
                .withDistance(500)
                .build();

        assertNotNull(order);
        assertEquals("ORD-001", order.getId());
        assertEquals(1, order.getCargoCount());
        assertEquals(500.0, order.getDistance());
    }

    @Test
    void testCostCalculation() throws LogisticsException {
        var cargo = factory.createCargo("electronics", 5);
        var truck = factory.createTransport("truck");

        var order = new DeliveryOrderBuilder("ORD-002")
                .withCargo(cargo)
                .withTransport(truck)
                .withDistance(500)
                .build();

        var calc = new DeliveryCalculator(order);
        double cost = calc.calculateTotalCost();

        // 375 (груз) + 7500 (транспорт) = 7875
        assertEquals(7875.0, cost, 0.01);
    }

    @Test
    void testTimeCalculation() throws LogisticsException {
        var cargo = factory.createCargo("electronics", 5);
        var truck = factory.createTransport("truck");

        var order = new DeliveryOrderBuilder("ORD-003")
                .withCargo(cargo)
                .withTransport(truck)
                .withDistance(800)
                .build();

        var calc = new DeliveryCalculator(order);
        double time = calc.calculateDeliveryTime();

        assertEquals(10.0, time, 0.01);
    }

    @Test
    void testPrototypePattern() throws LogisticsException, CloneNotSupportedException {
        var original = factory.createCargo("electronics", 5);
        var cloned = original.clone();

        assertNotSame(original, cloned);
        assertEquals(original.getTotalMass(), cloned.getTotalMass());
    }

    @Test
    void testBuilderValidation() throws LogisticsException {
        var cargo = factory.createCargo("electronics", 5);

        assertThrows(LogisticsException.class, () -> {
            new DeliveryOrderBuilder("ORD-004")
                    .withCargo(cargo)
                    .build();
        });
    }

    @Test
    void testInvalidCargoType() {
        assertThrows(LogisticsException.class, () -> {
            factory.createCargo("unknown", 5);
        });
    }

    @Test
    void testMultipleCargoOrder() throws LogisticsException {
        var cargo1 = factory.createCargo("electronics", 5);
        var cargo2 = factory.createCargo("clothing", 10);
        var airplane = factory.createTransport("airplane");

        var order = new DeliveryOrderBuilder("ORD-005")
                .withCargo(cargo1)
                .withCargo(cargo2)
                .withTransport(airplane)
                .withDistance(2000)
                .build();

        assertEquals(2, order.getCargoCount());
        var calc = new DeliveryCalculator(order);
        assertNotNull(calc.getDetailedBreakdown());
    }
}
