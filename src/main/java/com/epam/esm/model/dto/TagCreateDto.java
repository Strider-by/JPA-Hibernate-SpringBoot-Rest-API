package com.epam.esm.model.dto;

import java.util.Objects;

public class TagCreateDto {

    private String name;

    public TagCreateDto(String name) {
        this.name = name;
    }

    public TagCreateDto() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagCreateDto that = (TagCreateDto) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
