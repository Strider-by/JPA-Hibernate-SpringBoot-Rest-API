package com.epam.esm.repository;

import com.epam.esm.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCertificateRepository extends JpaRepository<Certificate, Long> {

}
