package com.epam.esm.model.dto;

import java.util.List;
import java.util.Objects;

public class CertificateUpstreamDto {

    private Long id;
    private String name;
    private List<String> description;
    private Integer price;
    private Integer duration;
    private String created;
    private String lastUpdate;

    public CertificateUpstreamDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


    @Override
    public String toString() {
        return "CertificateResponseDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description=" + description +
                ", price=" + price +
                ", duration=" + duration +
                ", created='" + created + '\'' +
                ", lastUpdate='" + lastUpdate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CertificateUpstreamDto that = (CertificateUpstreamDto) o;
        return price == that.price && duration == that.duration && Objects.equals(id, that.id)
                && Objects.equals(name, that.name) && Objects.equals(description, that.description)
                && Objects.equals(created, that.created) && Objects.equals(lastUpdate, that.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, price, duration, created, lastUpdate);
    }

}

