package com.example.boonet.core.exceptions;

/**
 * Исключения, связанные с платежами и подпиской
 */
public class PaymentException extends BooNetException {
    private final PaymentErrorType errorType;

    public enum PaymentErrorType {
        NO_CARD_LINKED("Карта не привязана"),
        INVALID_CARD("Недействительная карта"),
        SUBSCRIPTION_ALREADY_ACTIVE("Подписка уже активна"),
        PAYMENT_FAILED("Ошибка оплаты");

        private final String message;

        PaymentErrorType(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public PaymentException(PaymentErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public PaymentErrorType getErrorType() {
        return errorType;
    }
} 