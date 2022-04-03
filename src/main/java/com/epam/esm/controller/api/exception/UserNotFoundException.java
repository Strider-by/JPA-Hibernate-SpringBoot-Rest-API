package com.epam.esm.controller.api.exception;

public class UserNotFoundException extends RuntimeException {

    private final long userId;

    public UserNotFoundException(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }

}
