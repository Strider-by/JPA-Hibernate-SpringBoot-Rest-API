package com.epam.esm.model;

import com.epam.esm.model.audit.UserEventLogger;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@EntityListeners(UserEventLogger.class)
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
        this.purchases = new ArrayList(purchases);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!Objects.equals(id, user.id)) return false;
        return Objects.equals(purchases, user.purchases);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (purchases != null ? purchases.hashCode() : 0);
        return result;
    }

    @Override
    // fixme: somehow @PostRemove in Entity event logger causes NPE in User::toString (it cannot get list of purchases
    //  AFTER user was deleted? With @PreRemove everything works just fine.)
    public String toString() {
        return "User{" +
                "id=" + id +
                '}';
    }

}
