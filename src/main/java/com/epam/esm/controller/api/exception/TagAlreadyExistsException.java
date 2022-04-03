package com.epam.esm.controller.api.exception;

public class TagAlreadyExistsException extends RuntimeException {

    private final String tagName;

    public TagAlreadyExistsException(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

}
