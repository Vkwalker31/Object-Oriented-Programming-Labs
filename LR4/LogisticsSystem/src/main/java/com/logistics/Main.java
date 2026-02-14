package com.logistics;

import com.logistics.builder.DeliveryOrderBuilder;
import com.logistics.calculator.DeliveryCalculator;
import com.logistics.domain.DeliveryOrder;
import com.logistics.factory.LogisticsFactory;
import com.logistics.exception.LogisticsException;

/**
 * Main - точка входа в приложение
 * Демонстрирует применение GRASP и GoF паттернов:
 * - GRASP: Information Expert
 * - GRASP: Creator
 * - GoF: Abstract Factory
 * - GoF: Builder
 * - GoF: Prototype
 */

public class Main {

    public static void main(String[] args) {
        printHeader();

        try {
            LogisticsFactory factory = LogisticsFactory.getInstance();

            // ===== TEST 1: Электроника грузовиком =====
            testDelivery1(factory);

            // ===== TEST 2: Смешанный груз самолетом =====
            testDelivery2(factory);

            // ===== TEST 3: Скоропортящиеся товары поездом =====
            testDelivery3(factory);

            // ===== TEST 4: Оборудование танкером =====
            testDelivery4(factory);

            printComparison(factory);
            demonstratePrototype(factory);

            printSummary();

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testDelivery1(LogisticsFactory factory) throws LogisticsException {
        printTestHeader("TEST 1", "Электроника грузовиком");

        var electronics = factory.createCargo("electronics", 5);

        var truck = factory.createTransport("truck");

        var order = new DeliveryOrderBuilder("ORD-001")
                .withCargo(electronics)
                .withTransport(truck)
                .withDistance(500)
                .build();

        var calc = new DeliveryCalculator(order);
        var breakdown = calc.getDetailedBreakdown();

        System.out.println(breakdown);
    }

    private static void testDelivery2(LogisticsFactory factory) throws LogisticsException {
        printTestHeader("TEST 2", "Смешанный груз (Электроника + Одежда) самолетом");

        var electronics = factory.createCargo("electronics", 10);
        var clothing = factory.createCargo("clothing", 20);
        var airplane = factory.createTransport("airplane");

        var order = new DeliveryOrderBuilder("ORD-002")
                .withCargo(electronics)
                .withCargo(clothing)
                .withTransport(airplane)
                .withDistance(2000)
                .build();

        var calc = new DeliveryCalculator(order);
        var breakdown = calc.getDetailedBreakdown();

        System.out.println(breakdown);
    }

    private static void testDelivery3(LogisticsFactory factory) throws LogisticsException {
        printTestHeader("TEST 3", "Скоропортящиеся товары поездом");

        var perishable = factory.createCargo("perishable", 8);
        var train = factory.createTransport("train");

        var order = new DeliveryOrderBuilder("ORD-003")
                .withCargo(perishable)
                .withTransport(train)
                .withDistance(1200)
                .build();

        var calc = new DeliveryCalculator(order);
        var breakdown = calc.getDetailedBreakdown();

        System.out.println(breakdown);
    }

    private static void testDelivery4(LogisticsFactory factory) throws LogisticsException {
        printTestHeader("TEST 4", "Оборудование танкером");

        var equipment = factory.createCargo("equipment", 3);
        var tanker = factory.createTransport("tanker");

        var order = new DeliveryOrderBuilder("ORD-004")
                .withCargo(equipment)
                .withTransport(tanker)
                .withDistance(5000)
                .build();

        var calc = new DeliveryCalculator(order);
        var breakdown = calc.getDetailedBreakdown();

        System.out.println(breakdown);
    }

    private static void printComparison(LogisticsFactory factory) throws LogisticsException {
        System.out.println("\n" + "═".repeat(100));
        System.out.println("ТАБЛИЦА СРАВНЕНИЯ ДОСТАВОК");
        System.out.println("═".repeat(100));

        var orders = new java.util.ArrayList<DeliveryOrder>();
        var costs = new java.util.ArrayList<Double>();
        var times = new java.util.ArrayList<Double>();

        // Test 1
        var order1 = new DeliveryOrderBuilder("ORD-001")
                .withCargo(factory.createCargo("electronics", 5))
                .withTransport(factory.createTransport("truck"))
                .withDistance(500)
                .build();
        orders.add(order1);
        var calc1 = new DeliveryCalculator(order1);
        costs.add(calc1.calculateTotalCost());
        times.add(calc1.calculateDeliveryTime());

        // Test 2
        var order2 = new DeliveryOrderBuilder("ORD-002")
                .withCargo(factory.createCargo("electronics", 10))
                .withCargo(factory.createCargo("clothing", 20))
                .withTransport(factory.createTransport("airplane"))
                .withDistance(2000)
                .build();
        orders.add(order2);
        var calc2 = new DeliveryCalculator(order2);
        costs.add(calc2.calculateTotalCost());
        times.add(calc2.calculateDeliveryTime());

        // Test 3
        var order3 = new DeliveryOrderBuilder("ORD-003")
                .withCargo(factory.createCargo("perishable", 8))
                .withTransport(factory.createTransport("train"))
                .withDistance(1200)
                .build();
        orders.add(order3);
        var calc3 = new DeliveryCalculator(order3);
        costs.add(calc3.calculateTotalCost());
        times.add(calc3.calculateDeliveryTime());

        // Test 4
        var order4 = new DeliveryOrderBuilder("ORD-004")
                .withCargo(factory.createCargo("equipment", 3))
                .withTransport(factory.createTransport("tanker"))
                .withDistance(5000)
                .build();
        orders.add(order4);
        var calc4 = new DeliveryCalculator(order4);
        costs.add(calc4.calculateTotalCost());
        times.add(calc4.calculateDeliveryTime());

        System.out.printf("%-10s | %-30s | %-20s | %-15s | %-15s%n",
                "Заказ", "Грузы", "Транспорт", "Стоимость", "Время (ч)");
        System.out.println("─".repeat(100));

        for (int i = 0; i < orders.size(); i++) {
            var order = orders.get(i);
            var cargoStr = order.getCargoCount() == 1 ?
                    order.getCargoList().get(0).getName() :
                    order.getCargoCount() + " грузов";

            System.out.printf("%-10s | %-30s | %-20s | $%-14.2f | %-14.2f%n",
                    order.getId(),
                    cargoStr,
                    order.getTransport().getName(),
                    costs.get(i),
                    times.get(i));
        }
    }

    private static void demonstratePrototype(LogisticsFactory factory) throws LogisticsException, CloneNotSupportedException {
        System.out.println("\n" + "═".repeat(100));
        System.out.println("ДЕМОНСТРАЦИЯ PROTOTYPE ПАТТЕРНА");
        System.out.println("═".repeat(100) + "\n");

        var original = factory.createCargo("electronics", 5);
        var cloned = original.clone();

        System.out.println("Original: " + original);
        System.out.println("Cloned:   " + cloned);
        System.out.println("\n✓ Оба объекта независимы (глубокое копирование)");
    }

    private static void printHeader() {
        System.out.println("\n╔═══════════════════════════════════════════════╗");
        System.out.println("║            ЛОГИСТИЧЕСКАЯ СИСТЕМА              ║");
        System.out.println("║           GRASP + GoF (Creational)            ║");
        System.out.println("╚═══════════════════════════════════════════════╝\n");
    }

    private static void printTestHeader(String testNum, String description) {
        System.out.println("\n" + "═".repeat(100));
        System.out.println(testNum + ": " + description);
        System.out.println("═".repeat(100));
    }

    private static void printSummary() {
        System.out.println("\n" + "═".repeat(100));
        System.out.println("ПРИМЕНЕННЫЕ ПРИНЦИПЫ И ПАТТЕРНЫ");
        System.out.println("═".repeat(100));

        System.out.println("\n GRASP ПРИНЦИПЫ:");
        System.out.println("  ✓ Information Expert");
        System.out.println("    • Cargo - ЗНАЕТ свою массу и стоимость");
        System.out.println("    • Transport - ЗНАЕТ свою скорость и цену");
        System.out.println("    • DeliveryOrder - ЗНАЕТ состав груза");
        System.out.println("    • DeliveryCalculator - ЗНАЕТ формулы расчета");

        System.out.println("\n  ✓ Creator");
        System.out.println("    • LogisticsFactory - СОЗДАЕТ грузы и транспорт");
        System.out.println("    • CargoFactory, TransportFactory - специализированные создатели");
        System.out.println("    • DeliveryOrderBuilder - СОЗДАЕТ заказы");

        System.out.println("\n  ✓ Polymorphism");
        System.out.println("    • Разные грузы (Electronics, Clothing, Equipment, Perishable)");
        System.out.println("    • Разные транспорты (Truck, Train, Tanker, Airplane, Helicopter)");

        System.out.println("\n  ✓ High Cohesion");
        System.out.println("    • Каждый класс имеет четкую ответственность");

        System.out.println("\n  ✓ Low Coupling");
        System.out.println("    • Слабая связанность между компонентами");

        System.out.println("\n GoF CREATIONAL PATTERNS:");
        System.out.println("  ✓ Abstract Factory - LogisticsFactory");
        System.out.println("  ✓ Factory Method - CargoFactory, TransportFactory");
        System.out.println("  ✓ Builder - DeliveryOrderBuilder");
        System.out.println("  ✓ Prototype - Cargo.clone()");
        System.out.println("  ✓ Singleton - LogisticsFactory.getInstance()");
    }
}
