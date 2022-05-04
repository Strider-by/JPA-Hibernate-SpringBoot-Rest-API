//package com.epam.esm.dao;
//
//import com.epam.esm.model.Purchase;
//import org.springframework.data.domain.Page;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//import java.util.List;
//
//@Component // todo: move to impl?
//public interface PurchasesDao {
//
//    List<Purchase> getUserPurchases(long userId, long limit, long offset);
//
//    Page<Purchase> getAllPurchases(int pageNumber, int pageSize);
//
//    List<String> getUserPrimaryTags(long userId);
//
//    List<String> getPrimaryTags(long limit, long offset);
//
//    Purchase purchaseCertificate(long userId, long certificateId, Date timestamp);
//
//    Purchase getPurchaseById(long id);
//
//    boolean deletePurchase(long id);
//
//}
