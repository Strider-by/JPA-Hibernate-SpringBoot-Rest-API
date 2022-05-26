package com.epam.esm.repository;

import com.epam.esm.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

// todo: replace JpaRepository<?,?> with separate repos?
public interface PurchaseRepository extends CustomPurchaseRepository, JpaRepository<Purchase, Long> {

}
