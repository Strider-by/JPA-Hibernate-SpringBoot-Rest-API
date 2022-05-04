package com.epam.esm.service;

import com.epam.esm.model.Purchase;
import com.epam.esm.model.Tag;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component // todo: move to implementation?
public interface PurchaseService {

    Page<Purchase> getUserPurchases(long userId, int pageNumber, int pageSize);

    Page<Purchase> getAllPurchases(int pageNumber, int pageSize);

    Page<Tag> getPrimaryTags(int pageNumber, int pageSize);

    Page<Tag> getUserPrimaryTags(long userId, int pageNumber, int pageSize);

    Purchase purchaseCertificate(long userId, long certificateId);

    Purchase getPurchaseById(long id);

    boolean deletePurchase(long id);

}
