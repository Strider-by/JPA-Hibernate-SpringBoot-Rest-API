package com.epam.esm.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

//@NamedQuery(name = "test name", query = "SELECT s FROM Student s")
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Purchase> purchases;

    public User() {
        purchases = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public void setPurchases(List<Purchase> purchases) {
        this.purchases = purchases;
    }

}
