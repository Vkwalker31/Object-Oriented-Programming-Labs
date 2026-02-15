package com.logistics.io.export;

import com.logistics.io.dto.DeliveryOptionResult;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Интерфейс ResultExporter — контракт экспорта списка вариантов доставки в поток или файл.
 * GoF: Strategy — конкретные форматы (JSON, CSV) реализуют алгоритм сериализации.
 */

public interface ResultExporter {

    /**
     * Записывает данные в выходной поток.
     */
    void export(List<DeliveryOptionResult> data, OutputStream out) throws Exception;

    /**
     * Записывает данные в файл (по умолчанию — открывает поток и вызывает export).
     */
    default void exportToFile(List<DeliveryOptionResult> data, Path path) throws Exception {
        try (OutputStream os = Files.newOutputStream(path)) {
            export(data, os);
        }
    }
}
