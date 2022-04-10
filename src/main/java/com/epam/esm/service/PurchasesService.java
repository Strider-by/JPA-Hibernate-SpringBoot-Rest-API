package com.epam.esm.service;

import com.epam.esm.model.Purchase;
import org.springframework.stereotype.Component;

import java.util.List;

@Component // todo: move to implementation?
public interface PurchasesService {

    List<Purchase> getUserPurchases(long userId, long limit, long offset);

    List<Purchase> getAllPurchases(long limit, long offset);

    List<String> getPrimaryTags(long limit, long offset);

    List<String> getUserPrimaryTags(long userId);

    Purchase purchaseCertificate(long userId, long certificateId);

    Purchase getPurchaseById(long id);

    boolean deletePurchase(long id);

}
