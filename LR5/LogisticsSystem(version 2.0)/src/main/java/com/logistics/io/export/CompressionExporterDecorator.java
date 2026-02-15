package com.logistics.io.export;

import com.logistics.io.dto.DeliveryOptionResult;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Класс CompressionExporterDecorator — декоратор экспорта со сжатием в .zip архив.
 * GoF: Decorator — добавляет ответственность сжатия к базовому экспортеру.
 */

public class CompressionExporterDecorator extends ExporterDecorator {

    private static final String ZIP_ENTRY_NAME = "result";

    public CompressionExporterDecorator(ResultExporter wrapped) {
        super(wrapped);
    }

    @Override
    protected byte[] process(byte[] raw) throws Exception {
        ByteArrayOutputStream zipOut = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(zipOut)) {
            zos.putNextEntry(new ZipEntry(ZIP_ENTRY_NAME));
            zos.write(raw);
            zos.closeEntry();
        }
        return zipOut.toByteArray();
    }

    @Override
    public void exportToFile(List<DeliveryOptionResult> data, Path path) throws Exception {
        Path targetPath = path.getFileName().toString().toLowerCase().endsWith(".zip")
                ? path
                : path.getParent().resolve(path.getFileName().toString() + ".zip");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wrapped.export(data, baos);
        byte[] zipped = process(baos.toByteArray());
        Files.write(targetPath, zipped);
    }
}
