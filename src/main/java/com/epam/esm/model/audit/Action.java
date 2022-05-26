package com.epam.esm.model.audit;

public enum Action {

    CREATED,
    UPDATED,
    DELETED;

    private final String name;

    Action() {
        this.name = this.toString();
    }

    public String value() {
        return this.name;
    }

}
