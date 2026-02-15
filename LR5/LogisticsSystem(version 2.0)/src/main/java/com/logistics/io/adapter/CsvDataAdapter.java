package com.logistics.io.adapter;

import com.logistics.io.dto.CargoItemInput;
import com.logistics.io.dto.InputData;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс CsvDataAdapter — адаптер для чтения входных данных из CSV.
 * GoF: Adapter — преобразует CSV-поток в единый DTO InputData.
 * Формат: первая строка — distance,transportType,destination; далее строки — type,quantity для каждого груза.
 */

public class CsvDataAdapter implements DataFormatAdapter {

    @Override
    public boolean supports(Path path) {
        String name = path.getFileName().toString().toLowerCase();
        return name.endsWith(".csv");
    }

    @Override
    public InputData parse(InputStream input) throws Exception {
        List<String> lines = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) lines.add(line);
            }
        }

        double distance = 0;
        String transportType = null;
        String destination = null;
        List<CargoItemInput> cargo = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",", -1);
            for (int j = 0; j < parts.length; j++) parts[j] = parts[j].trim();
            if (i == 0 && parts.length >= 1 && isNumeric(parts[0])) {
                distance = Double.parseDouble(parts[0]);
                transportType = parts.length >= 2 && !parts[1].isEmpty() ? parts[1] : null;
                destination = parts.length >= 3 && !parts[2].isEmpty() ? parts[2] : null;
            } else if (parts.length >= 2) {
                try {
                    int qty = Integer.parseInt(parts[1].isEmpty() ? "0" : parts[1]);
                    cargo.add(new CargoItemInput(parts[0], qty));
                } catch (NumberFormatException ignored) { }
            }
        }

        return InputData.builder()
                .cargo(cargo)
                .distance(distance)
                .transportType(transportType)
                .destination(destination)
                .build();
    }

    private static boolean isNumeric(String s) {
        if (s == null || s.isEmpty()) return false;
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
