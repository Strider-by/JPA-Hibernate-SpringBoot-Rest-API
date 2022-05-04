package com.epam.esm.repository;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

// todo: didn't I create separate repo instead of JpaRepository<?,?>?
public interface PurchaseRepository extends CustomPurchaseRepository, JpaRepository<Purchase, Long> {

}
