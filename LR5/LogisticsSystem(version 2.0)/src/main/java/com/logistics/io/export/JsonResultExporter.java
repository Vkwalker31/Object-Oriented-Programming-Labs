package com.logistics.io.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.logistics.io.dto.DeliveryOptionResult;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Класс JsonResultExporter — экспорт вариантов доставки в формате JSON.
 * GoF: Strategy — конкретная стратегия сериализации результата.
 */

public class JsonResultExporter implements ResultExporter {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public void export(List<DeliveryOptionResult> data, OutputStream out) throws Exception {
        try (OutputStreamWriter w = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
            GSON.toJson(data, w);
        }
    }
}
