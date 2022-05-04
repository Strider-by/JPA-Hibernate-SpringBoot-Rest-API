//package com.epam.esm.service.impl.v1;
//
//import com.epam.esm.controller.api.exception.PurchaseNotFoundException;
//import com.epam.esm.controller.api.exception.UserNotFoundException;
//import com.epam.esm.dao.PurchasesDao;
//import com.epam.esm.model.Purchase;
//import com.epam.esm.service.PurchasesService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//import java.util.List;
//
//@Component
//public class PurchasesServiceImpl implements PurchasesService {
//
//    @Autowired
//    private PurchasesDao dao;
//
//    @Override
//    public List<Purchase> getUserPurchases(long userId, long limit, long offset) {
//        List<Purchase> userPurchases = dao.getUserPurchases(userId, limit, offset);
//        if (userPurchases == null) { //todo: return null if user doesn't exist
//            throw new UserNotFoundException(userId);
//        }
//        return userPurchases;
//    }
//
//    @Override
//    public Page<Purchase> getAllPurchases(int pageNumber, int pageSize) {
//        return dao.getAllPurchases(pageNumber, pageSize);
//    }
//
//    @Override
//    public List<String> getUserPrimaryTags(long userId) {
//        return dao.getUserPrimaryTags(userId);
//    }
//
//    @Override
//    public Purchase purchaseCertificate(long userId, long certificateId) {
//        Date timestamp = new Date();
//        Purchase purchase = dao.purchaseCertificate(userId, certificateId, timestamp);
//        if (purchase == null) { // todo: switch to 2 custom exceptions on dao layer; add exception processor
//            throw new UserNotFoundException(userId);
//        }
//        return purchase;
//    }
//
//    @Override
//    public Purchase getPurchaseById(long id) {
//        Purchase purchase = dao.getPurchaseById(id);
//        if (purchase == null) {
//            throw new PurchaseNotFoundException(id);
//        }
//        return purchase;
//    }
//
//    @Override
//    public boolean deletePurchase(long id) {
//        return dao.deletePurchase(id);
//    }
//
//    @Override
//    public List<String> getPrimaryTags(long limit, long offset) {
//        return dao.getPrimaryTags(limit, offset);
//    }
//
//}
