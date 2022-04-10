package com.epam.esm.repository;

import com.epam.esm.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTagRepository extends JpaRepository<Certificate, Long> {

}
