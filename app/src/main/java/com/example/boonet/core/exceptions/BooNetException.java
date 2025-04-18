package com.example.boonet.core.exceptions;

/**
 * Базовое исключение для всех ошибок в приложении
 */
public class BooNetException extends Exception {
    public BooNetException(String message) {
        super(message);
    }

    public BooNetException(String message, Throwable cause) {
        super(message, cause);
    }
} 