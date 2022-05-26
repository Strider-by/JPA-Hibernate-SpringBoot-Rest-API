package com.epam.esm.service.impl;

import com.epam.esm.controller.api.exception.CertificateNotFoundException;
import com.epam.esm.controller.api.exception.PurchaseNotFoundException;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private UserRepository userRepository;

    public PurchaseServiceImpl() {
    }

    public PurchaseServiceImpl(PurchaseRepository purchaseRepository, CertificateRepository certificateRepository, UserRepository userRepository) {
        this.purchaseRepository = purchaseRepository;
        this.certificateRepository = certificateRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<Purchase> getUserPurchases(long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return purchaseRepository.getUserPurchases(userId, pageable);
    }

    @Override
    public Page<Purchase> getAllPurchases(Pageable pageable) {
        return purchaseRepository.findAll(pageable);
    }

    @Override
    public Page<Tag> getPrimaryTags(Pageable pageable) {
        return purchaseRepository.getPrimaryTags(pageable);
    }

    @Override
    public Page<Tag> getUserPrimaryTags(long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return  purchaseRepository.getUserPrimaryTags(userId, pageable);
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
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new PurchaseNotFoundException(id));
    }

    @Override
    public void deletePurchase(long id) {
        purchaseRepository.deletePurchaseById(id);
    }

}
