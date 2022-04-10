package com.epam.esm.model;

import javax.persistence.NamedQuery;

@NamedQuery(name = "test name", query = "SELECT s FROM Student s")
public class User {
    
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
