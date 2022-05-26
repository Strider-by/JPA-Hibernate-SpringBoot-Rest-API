package com.epam.esm.service;

import com.epam.esm.model.Purchase;
import com.epam.esm.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

public interface PurchaseService {

    Page<Purchase> getUserPurchases(long userId, Pageable pageable);

    Page<Purchase> getAllPurchases(Pageable pageable);

    Page<Tag> getPrimaryTags(Pageable pageable);

    Page<Tag> getUserPrimaryTags(long userId, Pageable pageable);

    Purchase purchaseCertificate(long userId, long certificateId);

    Purchase getPurchaseById(long id);

    void deletePurchase(long id);

}
