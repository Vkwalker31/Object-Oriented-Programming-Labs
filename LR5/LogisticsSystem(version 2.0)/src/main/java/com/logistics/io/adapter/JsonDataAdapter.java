package com.logistics.io.adapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.logistics.io.dto.CargoItemInput;
import com.logistics.io.dto.InputData;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Класс JsonDataAdapter — адаптер для чтения входных данных из JSON.
 * GoF: Adapter — преобразует JSON-поток в единый DTO InputData.
 */

public class JsonDataAdapter implements DataFormatAdapter {

    private static final Gson GSON = new GsonBuilder().setLenient().create();

    @Override
    public boolean supports(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        return name.endsWith(".json");
    }

    @Override
    public InputData parse(InputStream input) throws Exception {
        InputStreamReader reader = new InputStreamReader(input);
        JsonInputStructure raw = GSON.fromJson(reader, JsonInputStructure.class);
        if (raw == null) {
            return InputData.builder().build();
        }
        List<CargoItemInput> cargo = raw.cargo != null
                ? Arrays.asList(raw.cargo)
                : new java.util.ArrayList<>();
        return InputData.builder()
                .cargo(new java.util.ArrayList<>(cargo))
                .distance(raw.distance)
                .transportType(raw.transportType)
                .destination(raw.destination)
                .build();
    }

    @SuppressWarnings("unused")
    private static class JsonInputStructure {
        CargoItemInput[] cargo;
        double distance;
        String transportType;
        String destination;
    }
}
