package com.epam.esm.model;

import com.epam.esm.model.audit.PurchaseEventLogger;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@EntityListeners(PurchaseEventLogger.class)
@Table(name = "purchase")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private User user;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Certificate certificate;
    private Integer cost;
    private Date timestamp;

    public Purchase() {
    }

    public Purchase(User user, Certificate certificate) {
        this.user = user;
        this.certificate = certificate;
        this.cost = certificate.getPrice();
        affixTimestamp();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void affixTimestamp() {
        this.timestamp = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Purchase purchase = (Purchase) o;
        return Objects.equals(id, purchase.id) && Objects.equals(user, purchase.user)
                && Objects.equals(certificate, purchase.certificate) && Objects.equals(cost, purchase.cost)
                && Objects.equals(timestamp, purchase.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, certificate, cost, timestamp);
    }

}
