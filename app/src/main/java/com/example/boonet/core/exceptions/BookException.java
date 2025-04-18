package com.example.boonet.core.exceptions;

/**
 * Исключения, связанные с книгами
 */
public class BookException extends BooNetException {
    private final BookErrorType errorType;

    public enum BookErrorType {
        BOOK_NOT_FOUND("Книга не найдена"),
        ACCESS_DENIED("Нет доступа к книге"),
        INVALID_BOOK_DATA("Некорректные данные книги"),
        SUBSCRIPTION_REQUIRED("Требуется подписка");

        private final String message;

        BookErrorType(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public BookException(BookErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public BookErrorType getErrorType() {
        return errorType;
    }
} 