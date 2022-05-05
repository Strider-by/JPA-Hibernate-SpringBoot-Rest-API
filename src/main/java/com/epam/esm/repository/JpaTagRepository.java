package com.epam.esm.repository;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTagRepository extends JpaRepository<Tag, Long> {

}
