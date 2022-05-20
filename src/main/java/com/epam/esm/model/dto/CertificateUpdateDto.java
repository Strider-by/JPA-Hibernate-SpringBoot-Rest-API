//package com.epam.esm.model.dto;
//
//import java.util.Date;
//import java.util.List;
//
//public class CertificateUpdateDto {
//
//    private long id;
//    private String name;
//    private List<String> description;
//    private Integer price;
//    private Integer duration;
//    private Date lastUpdate;
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public List<String> getDescription() {
//        return description;
//    }
//
//    public void setDescription(List<String> description) {
//        this.description = description;
//    }
//
//    public Integer getPrice() {
//        return price;
//    }
//
//    public void setPrice(int price) {
//        this.price = price;
//    }
//
//    public Integer getDuration() {
//        return duration;
//    }
//
//    public void setDuration(int duration) {
//        this.duration = duration;
//    }
//
//    public Date getLastUpdate() {
//        return lastUpdate;
//    }
//
//    public void setLastUpdate(Date lastUpdate) {
//        this.lastUpdate = lastUpdate;
//    }
//
//    public void affixUpdateTimestamp() {
//        this.lastUpdate = new Date();
//    }
//
//    @Override
//    public String toString() {
//        return "CertificateDto{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", description=" + description +
//                ", price=" + price +
//                ", duration=" + duration +
//                ", lastUpdate='" + lastUpdate + '\'' +
//                '}';
//    }
//
//}
