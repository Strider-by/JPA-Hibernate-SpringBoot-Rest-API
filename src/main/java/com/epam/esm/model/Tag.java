package com.epam.esm.model;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Tag {

    @Id
//    @TableGenerator(
//            name = "tag_table_generator",
//            table = "tag_table",
//            pkColumnValue = "tag_seq")
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
            /*generator = "tag_table_generator"*/)
//    @SequenceGenerator(
//            name = "tag_seq",
//            sequenceName = "tag_seq")
    private Long id;
    @Column(unique = true)
    private String name;
//    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
//    private List<Certificate> certificates;

    public Tag() {
    }

    public Tag(Long id, String name) {
        this.id = id;
        this.name = name;
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

//    public List<Certificate> getCertificates() {
//        return certificates;
//    }
//
//    public void setCertificates(List<Certificate> certificates) {
//        this.certificates = certificates;
//    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id) && Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

}
