package com.epam.esm.repository;

import com.epam.esm.model.Purchase;
import com.epam.esm.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomPurchaseRepository {

    Page<Purchase> getUserPurchases(long userId, int pageNumber, int pageSize);

    Page<Tag> getPrimaryTags(int pageNumber, int pageSize);

    Page<Tag> getUserPrimaryTags(long userId, int pageNumber, int pageSize);

}
