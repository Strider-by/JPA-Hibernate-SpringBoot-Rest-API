package com.epam.esm.model.dto;

import java.util.Objects;

public class TagDownstreamDto {

    private String name;

    public TagDownstreamDto(String name) {
        this.name = name;
    }

    public TagDownstreamDto() {
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
        TagDownstreamDto that = (TagDownstreamDto) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
