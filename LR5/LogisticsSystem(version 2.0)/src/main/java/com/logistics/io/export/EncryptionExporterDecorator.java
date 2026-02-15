package com.logistics.io.export;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Класс EncryptionExporterDecorator — декоратор экспорта с шифрованием данных.
 * GoF: Decorator — добавляет ответственность шифрования к базовому экспортеру.
 */

public class EncryptionExporterDecorator extends ExporterDecorator {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private final byte[] keyBytes;

    public EncryptionExporterDecorator(ResultExporter wrapped, String secretKey) throws Exception {
        super(wrapped);
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        this.keyBytes = Arrays.copyOf(sha.digest(secretKey.getBytes(StandardCharsets.UTF_8)), 16);
    }

    @Override
    protected byte[] process(byte[] raw) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(raw);
    }
}
