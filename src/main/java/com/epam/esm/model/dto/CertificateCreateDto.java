package com.epam.esm.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CertificateCreateDto {

    private String name;
    private List<TagCreateDto> description;
    private int price;
    private int duration;

    // todo: this is for testing only. rework somehow?
    public CertificateCreateDto() {
        description = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TagCreateDto> getDescription() {
        return description;
    }

    public void setDescription(List<TagCreateDto> description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CertificateCreateDto dto = (CertificateCreateDto) o;
        return price == dto.price && duration == dto.duration && Objects.equals(name, dto.name)
                && Objects.equals(description, dto.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, price, duration);
    }

    @Override
    public String toString() {
        return "CertificateDownstreamDto{" +
                "name='" + name + '\'' +
                ", description=" + description +
                ", price=" + price +
                ", duration=" + duration +
                '}';
    }

}
