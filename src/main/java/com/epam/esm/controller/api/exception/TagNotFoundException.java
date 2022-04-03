package com.epam.esm.controller.api.exception;

public class TagNotFoundException extends RuntimeException {

    private final String tagName;

    public TagNotFoundException(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

}
