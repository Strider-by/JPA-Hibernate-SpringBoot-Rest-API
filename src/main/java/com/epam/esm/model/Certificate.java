package com.epam.esm.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Certificate {

    private long id;
    private String name;
    private List<Tag> description;
    private Integer price;
    private Integer duration;
    private Date createDate;
    private Date lastUpdateDate;

    public Certificate() {
        this.description = new ArrayList<>();
    }

    public Certificate(
            long id, String name, List<Tag> description, int price, int duration, Date createDate, Date lastUpdateDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Tag> getDescription() {
        return description;
    }

    public void setDescription(List<Tag> description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public String toString() {
        return "Certificate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description=" + description +
                ", price=" + price +
                ", duration=" + duration +
                ", createDate=" + createDate +
                ", lastUpdateDate=" + lastUpdateDate +
                '}';
    }

}
