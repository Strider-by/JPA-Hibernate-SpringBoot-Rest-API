package com.epam.esm.controller.util;

import org.springframework.http.HttpStatus;

public class Message {

    private final int code;
    private final String text;

    public Message(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public Message(HttpStatus httpStatus, String text) {
        this.code = httpStatus.value();
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

}
