package com.logistics.exception;

/**
 * LogisticsException - кастомное исключение для логистической системы
 */

public class LogisticsException extends Exception {

    public LogisticsException(String message) {
        super(message);
    }

    public LogisticsException(String message, Throwable cause) {
        super(message, cause);
    }
}
