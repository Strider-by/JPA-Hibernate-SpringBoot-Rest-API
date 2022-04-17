package com.epam.esm.repository;

import com.epam.esm.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface JpaCertificateRepository extends JpaRepository<Certificate, Long> {

}
