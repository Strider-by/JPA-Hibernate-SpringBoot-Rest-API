package com.epam.esm.model.dto;

import java.util.List;
import java.util.Objects;

public class CertificateCreateDto {

    private String name;
    private List<TagDownstreamDto> description;
    private int price;
    private int duration;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TagDownstreamDto> getDescription() {
        return description;
    }

    public void setDescription(List<TagDownstreamDto> description) {
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
