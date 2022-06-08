package com.epam.esm.model;

import com.epam.esm.model.audit.CertificateEventLogger;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "certificate")
@EntityListeners(CertificateEventLogger.class)
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    // 1.
    // I had to set Eager fetch since otherwise I got LazyInitialization exception during tests when deleting certificates
    // (the fun part - it is thrown when getting data during Entity event logging, in Certificate::toString method)
    // 2.
    // CascadeType.PERSIST somehow works perfectly in real work, but causes org.hibernate.PersistentObjectException:
    // detached entity passed to persist while creating certificates with tags during tests on inner database. Odd.

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Tag> description;
    private Integer price;
    private Integer duration;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Timestamp createDate;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS")
    private Timestamp lastUpdateDate;

    public Certificate() {
        this.description = new ArrayList<>();
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

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Timestamp lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Certificate that = (Certificate) o;

        if (!Objects.equals(id, that.id)) return false;
        if (!Objects.equals(name, that.name)) return false;
        // broken and fixed: Hibernate uses PersistentBag instead of standard List. PersistentBag does not care about the
        // fact that Lists should be adequately comparable.
        if (!listsAreEqual(description, that.description)) return false;
        if (!Objects.equals(price, that.price)) return false;
        if (!Objects.equals(duration, that.duration)) return false;
        if (!Objects.equals(createDate, that.createDate)) return false;
        return Objects.equals(lastUpdateDate, that.lastUpdateDate);
    }

    private boolean listsAreEqual(List<?> l1, List<?> l2) {
        // this guarantees that both lists aren't null
        if (l1 == l2) return true;
        if (l1 == null || l2 == null) return false;

        Object[] arr1 = l1.stream().toArray();
        Object[] arr2 = l2.stream().toArray();
        return Arrays.equals(arr1, arr2);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        // one more fix to mend usage Bag instead of standard List
        result = 31 * result + (description != null ? description.stream().collect(Collectors.toList()).hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (lastUpdateDate != null ? lastUpdateDate.hashCode() : 0);
        return result;
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
