package com.epam.esm.repository;

import com.epam.esm.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPurchaseRepository extends JpaRepository<Purchase, Long> {

}
