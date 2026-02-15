package com.logistics.io.export;

import com.logistics.io.dto.DeliveryOptionResult;

import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Абстрактный класс ExporterDecorator — база для декораторов экспорта.
 * GoF: Decorator — добавляет дополнительную ответственность (шифрование, сжатие) объекту-экспортеру через композицию.
 */

public abstract class ExporterDecorator implements ResultExporter {

    protected final ResultExporter wrapped;

    protected ExporterDecorator(ResultExporter wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void export(List<DeliveryOptionResult> data, java.io.OutputStream out) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wrapped.export(data, baos);
        byte[] processed = process(baos.toByteArray());
        out.write(processed);
    }

    /**
     * Обработка байтов (шифрование, сжатие и т.д.).
     */
    protected abstract byte[] process(byte[] raw) throws Exception;

    @Override
    public void exportToFile(List<DeliveryOptionResult> data, Path path) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        wrapped.export(data, baos);
        byte[] processed = process(baos.toByteArray());
        Files.write(path, processed);
    }
}
