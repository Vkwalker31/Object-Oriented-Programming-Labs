package com.logistics.io.export;

import com.logistics.io.dto.DeliveryOptionResult;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Класс CsvResultExporter — экспорт вариантов доставки в формате CSV.
 * GoF: Strategy — конкретная стратегия сериализации результата.
 */

public class CsvResultExporter implements ResultExporter {

    @Override
    public void export(List<DeliveryOptionResult> data, OutputStream out) throws Exception {
        OutputStreamWriter w = new OutputStreamWriter(out, StandardCharsets.UTF_8);
        w.write("transportName,transportType,totalCost,deliveryTimeHours,speed\n");
        for (DeliveryOptionResult r : data) {
            w.write(String.format("%s,%s,%.2f,%.2f,%.2f\n",
                    escape(r.getTransportName()),
                    escape(r.getTransportType()),
                    r.getTotalCost(),
                    r.getDeliveryTimeHours(),
                    r.getSpeed()));
        }
        w.flush();
    }

    private static String escape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"")) return "\"" + s.replace("\"", "\"\"") + "\"";
        return s;
    }
}
