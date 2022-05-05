package com.epam.esm.repository;

import com.epam.esm.model.Purchase;
import com.epam.esm.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

public interface CustomPurchaseRepository {

    Page<Purchase> getUserPurchases(long userId, Pageable pageable);

    Page<Tag> getPrimaryTags(Pageable pageable);

    Page<Tag> getUserPrimaryTags(long userId, Pageable pageable);

    void deletePurchaseById(long id);

}
