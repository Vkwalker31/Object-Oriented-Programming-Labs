package com.logistics.io.facade;

import com.logistics.builder.DeliveryOrderBuilder;
import com.logistics.calculator.DeliveryCalculator;
import com.logistics.domain.CostBreakdown;
import com.logistics.domain.DeliveryOrder;
import com.logistics.exception.LogisticsException;
import com.logistics.factory.LogisticsFactory;
import com.logistics.io.adapter.CsvDataAdapter;
import com.logistics.io.adapter.DataFormatAdapter;
import com.logistics.io.adapter.JsonDataAdapter;
import com.logistics.io.adapter.XmlDataAdapter;
import com.logistics.io.dto.DeliveryOptionResult;
import com.logistics.io.dto.InputData;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Класс DataModuleFacade — единая точка входа для работы с внешними данными.
 * GoF: Facade — предоставляет унифицированный интерфейс для загрузки файлов (JSON/XML/CSV), расчёта вариантов доставки и экспорта.
 * Скрывает от клиента набор адаптеров и логику выбора формата.
 */

public class DataModuleFacade {

    private static final List<DataFormatAdapter> ADAPTERS = List.of(
            new JsonDataAdapter(),
            new XmlDataAdapter(),
            new CsvDataAdapter()
    );

    private final LogisticsFactory factory = LogisticsFactory.getInstance();

    /**
     * Загружает входные данные из файла по пути. Формат определяется по расширению (.json, .xml, .csv).
     */
    public InputData loadFromFile(Path path) throws Exception {
        DataFormatAdapter adapter = null;
        for (DataFormatAdapter a : ADAPTERS) {
            if (a.supports(path)) {
                adapter = a;
                break;
            }
        }
        if (adapter == null) {
            throw new IllegalArgumentException("Неподдерживаемый формат файла. Ожидается .json, .xml или .csv");
        }
        try (InputStream is = Files.newInputStream(path)) {
            return adapter.parse(is);
        }
    }

    /**
     * Строит заказ по InputData. Если transportType задан — возвращается один заказ с этим транспортом.
     * Иначе транспорт в заказе не устанавливается (для последующего расчёта всех вариантов).
     */
    public DeliveryOrder buildOrder(InputData data) throws LogisticsException {
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        DeliveryOrderBuilder builder = new DeliveryOrderBuilder(orderId);
        for (var item : data.getCargo()) {
            builder.withCargo(factory.createCargo(item.getType(), item.getQuantity()));
        }
        builder.withDistance(data.getDistance());
        if (data.getTransportType() != null && !data.getTransportType().isEmpty()) {
            builder.withTransport(factory.createTransport(data.getTransportType()));
        }
        return builder.build();
    }

    /**
     * Если в data не задан тип транспорта — возвращает список всех возможных вариантов доставки (по каждому виду транспорта).
     * Иначе — список из одного варианта.
     */
    public List<DeliveryOptionResult> computeDeliveryOptions(InputData data) throws LogisticsException {
        List<DeliveryOptionResult> results = new ArrayList<>();
        String[] transportTypes = factory.getAvailableTransportTypes();

        if (data.getTransportType() != null && !data.getTransportType().isEmpty()) {
            DeliveryOrder order = buildOrder(data);
            addOption(order, results);
            return results;
        }

        for (String transportType : transportTypes) {
            InputData withTransport = InputData.builder()
                    .cargo(data.getCargo())
                    .distance(data.getDistance())
                    .transportType(transportType)
                    .destination(data.getDestination())
                    .build();
            try {
                DeliveryOrder order = buildOrder(withTransport);
                addOption(order, results);
            } catch (LogisticsException e) {
                // skip invalid combination
            }
        }
        return results;
    }

    private void addOption(DeliveryOrder order, List<DeliveryOptionResult> results) {
        DeliveryCalculator calc = new DeliveryCalculator(order);
        CostBreakdown breakdown = calc.getDetailedBreakdown();
        results.add(DeliveryOptionResult.builder()
                .transportName(order.getTransport().getName())
                .transportType(order.getTransport().getType())
                .totalCost(breakdown.getTotalCost())
                .deliveryTimeHours(breakdown.getDeliveryTimeHours())
                .speed(order.getTransport().getSpeed())
                .build());
    }
}
