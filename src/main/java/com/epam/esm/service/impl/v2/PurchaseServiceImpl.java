package com.epam.esm.service.impl.v2;

import com.epam.esm.controller.api.exception.CertificateNotFoundException;
import com.epam.esm.controller.api.exception.UserNotFoundException;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Purchase;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.PurchaseRepository;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private UserRepository userRepository;


    @Override
    public Page<Purchase> getUserPurchases(long userId, int pageNumber, int pageSize) {
        return purchaseRepository.getUserPurchases(userId, pageNumber, pageSize);
    }

    @Override
    public Page<Purchase> getAllPurchases(int pageNumber, int pageSize) {
        return purchaseRepository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    @Override
    public Page<Tag> getPrimaryTags(int pageNumber, int pageSize) {
        return purchaseRepository.getPrimaryTags(pageNumber, pageSize);
    }

    @Override
    public Page<Tag> getUserPrimaryTags(long userId, int pageNumber, int pageSize) {
//        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return  purchaseRepository.getUserPrimaryTags(userId, pageNumber, pageSize);
    }

    @Override
    public Purchase purchaseCertificate(long userId, long certificateId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new CertificateNotFoundException(certificateId));

        Purchase purchase = new Purchase(user, certificate);
        return purchaseRepository.save(purchase);
    }

    @Override
    public Purchase getPurchaseById(long id) {
        return null;
    }

    @Override
    public boolean deletePurchase(long id) {
        return false;
    }

}
