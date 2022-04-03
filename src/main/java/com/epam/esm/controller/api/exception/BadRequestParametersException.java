package com.epam.esm.controller.api.exception;

public class BadRequestParametersException extends RuntimeException {

    private final String message;

    public BadRequestParametersException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
