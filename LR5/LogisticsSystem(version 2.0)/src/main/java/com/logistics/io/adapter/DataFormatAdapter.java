package com.logistics.io.adapter;

import com.logistics.io.dto.InputData;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * Интерфейс DataFormatAdapter — контракт адаптера формата входных данных.
 * GoF: Adapter — конвертирует интерфейс внешнего формата (JSON/XML/CSV) в единый интерфейс InputData, ожидаемый клиентом.
 */

public interface DataFormatAdapter {
    /**
     * Определяет, поддерживает ли адаптер данный файл по расширению/типу.
     */
    boolean supports(Path path);

    /**
     * Парсит поток/файл и возвращает InputData.
     */
    InputData parse(InputStream input) throws Exception;
}
