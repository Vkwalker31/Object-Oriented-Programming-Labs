package com.logistics;

import com.logistics.builder.DeliveryOrderBuilder;
import com.logistics.calculator.DeliveryCalculator;
import com.logistics.factory.LogisticsFactory;
import com.logistics.exception.LogisticsException;
import com.logistics.io.dto.DeliveryOptionResult;
import com.logistics.io.dto.InputData;
import com.logistics.io.export.*;
import com.logistics.io.facade.DataModuleFacade;
import com.logistics.io.filter.ByMaxPriceFilter;
import com.logistics.io.filter.ByTransportNameFilter;
import com.logistics.io.iterator.DeliveryOptionsResult;
import com.logistics.io.sort.ByNameSortStrategy;
import com.logistics.io.sort.ByPriceSortStrategy;
import com.logistics.io.sort.BySpeedSortStrategy;
import com.logistics.io.sort.SortStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

    // ========== Модуль данных: Adapter, Facade, Decorator, Strategy, Iterator ==========

    @Test
    void testDataModuleLoadJsonAndComputeAllOptions(@TempDir Path tempDir) throws Exception {
        Path json = tempDir.resolve("input.json");
        Files.writeString(json, """
            {"cargo":[{"type":"electronics","quantity":5}],"distance":500,"transportType":null,"destination":"Minsk"}
            """);
        DataModuleFacade facade = new DataModuleFacade();
        InputData data = facade.loadFromFile(json);
        assertEquals(1, data.getCargo().size());
        assertEquals(5, data.getCargo().get(0).getQuantity());
        assertEquals(500, data.getDistance(), 0.01);
        assertTrue(data.getTransportType() == null || data.getTransportType().isEmpty());

        List<DeliveryOptionResult> options = facade.computeDeliveryOptions(data);
        assertTrue(options.size() >= 5);
        assertTrue(options.stream().anyMatch(o -> "Грузовик".equals(o.getTransportName())));
        assertTrue(options.stream().anyMatch(o -> o.getTotalCost() > 0));
    }

    @Test
    void testDataModuleWithTransportTypeReturnsOneOption() throws Exception {
        InputData data = InputData.builder()
                .cargo(List.of(new com.logistics.io.dto.CargoItemInput("electronics", 5)))
                .distance(500)
                .transportType("truck")
                .build();
        DataModuleFacade facade = new DataModuleFacade();
        List<DeliveryOptionResult> options = facade.computeDeliveryOptions(data);
        assertEquals(1, options.size());
        assertEquals("Грузовик", options.get(0).getTransportName());
    }

    @Test
    void testFilterStrategy() {
        List<DeliveryOptionResult> list = new ArrayList<>();
        list.add(DeliveryOptionResult.builder().transportName("A").totalCost(100).build());
        list.add(DeliveryOptionResult.builder().transportName("B").totalCost(200).build());
        list.add(DeliveryOptionResult.builder().transportName("C").totalCost(300).build());
        var filter = new ByMaxPriceFilter(150);
        var filtered = list.stream().filter(filter::test).toList();
        assertEquals(2, filtered.size());
        assertTrue(new ByTransportNameFilter("A").test(list.get(0)));
        assertFalse(new ByTransportNameFilter("X").test(list.get(0)));
    }

    @Test
    void testSortStrategyCombinable() {
        List<DeliveryOptionResult> list = new ArrayList<>();
        list.add(DeliveryOptionResult.builder().transportName("C").totalCost(100).speed(50).build());
        list.add(DeliveryOptionResult.builder().transportName("A").totalCost(100).speed(80).build());
        list.add(DeliveryOptionResult.builder().transportName("B").totalCost(50).speed(60).build());
        SortStrategy sort = new ByPriceSortStrategy(true).then(new ByNameSortStrategy(true));
        sort.apply(list);
        assertEquals(50, list.get(0).getTotalCost(), 0.01);
        assertEquals("B", list.get(0).getTransportName());
        assertEquals(100, list.get(1).getTotalCost(), 0.01);
        assertEquals("A", list.get(1).getTransportName());
        assertEquals("C", list.get(2).getTransportName());
    }

    @Test
    void testIteratorOverDeliveryOptions() {
        List<DeliveryOptionResult> options = List.of(
                DeliveryOptionResult.builder().transportName("Truck").build(),
                DeliveryOptionResult.builder().transportName("Train").build()
        );
        DeliveryOptionsResult result = new DeliveryOptionsResult(options);
        int count = 0;
        for (DeliveryOptionResult r : result) {
            assertNotNull(r.getTransportName());
            count++;
        }
        assertEquals(2, count);
        assertEquals(2, result.size());
    }

    @Test
    void testExportJsonAndCsv(@TempDir Path tempDir) throws Exception {
        List<DeliveryOptionResult> data = List.of(
                DeliveryOptionResult.builder().transportName("Truck").transportType("Land").totalCost(100).deliveryTimeHours(5).speed(80).build()
        );
        Path jsonPath = tempDir.resolve("out.json");
        Path csvPath = tempDir.resolve("out.csv");
        new JsonResultExporter().exportToFile(data, jsonPath);
        new CsvResultExporter().exportToFile(data, csvPath);
        assertTrue(Files.size(jsonPath) > 0);
        String csv = Files.readString(csvPath);
        assertTrue(csv.contains("Truck") && csv.contains("100"));
    }

    @Test
    void testExportWithCompressionDecorator(@TempDir Path tempDir) throws Exception {
        List<DeliveryOptionResult> data = List.of(
                DeliveryOptionResult.builder().transportName("T").totalCost(1).deliveryTimeHours(1).speed(1).build()
        );
        Path zipPath = tempDir.resolve("result.zip");
        ResultExporter compressed = new CompressionExporterDecorator(new JsonResultExporter());
        compressed.exportToFile(data, zipPath);
        assertTrue(Files.exists(zipPath));
        assertTrue(Files.size(zipPath) > 0);
    }

    @Test
    void testExportWithEncryptionDecorator(@TempDir Path tempDir) throws Exception {
        List<DeliveryOptionResult> data = List.of(
                DeliveryOptionResult.builder().transportName("T").totalCost(1).deliveryTimeHours(1).speed(1).build()
        );
        Path encPath = tempDir.resolve("enc.bin");
        ResultExporter encrypted = new EncryptionExporterDecorator(new JsonResultExporter(), "test-key");
        encrypted.exportToFile(data, encPath);
        assertTrue(Files.size(encPath) > 0);
    }
}
