package com.epam.esm.controller.api.exception;

public class BadRequestParametersException extends RuntimeException {

    private String message;

    public BadRequestParametersException(String message) {
        this.message = message;
    }

    public BadRequestParametersException(String message, String message1) {
        super(message);
        this.message = message1;
    }

    public BadRequestParametersException(String message, Throwable cause, String message1) {
        super(message, cause);
        this.message = message1;
    }

    public BadRequestParametersException(Throwable cause, String message) {
        super(cause);
        this.message = message;
    }

    public BadRequestParametersException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String message1) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.message = message1;
    }

    public BadRequestParametersException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return message;
    }

}
