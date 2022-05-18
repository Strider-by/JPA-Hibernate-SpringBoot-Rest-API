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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.epam.esm.service.impl.PurchaseServiceImplTest.Data.*;

class PurchaseServiceImplTest {

    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private CertificateRepository certificateRepository;
    @Mock
    private UserRepository userRepository;
    private PurchaseService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new PurchaseServiceImpl(purchaseRepository, certificateRepository, userRepository);
    }

    @Test
    void getUserPurchases_success() {
        when(purchaseRepository.getUserPurchases(userId, pageable)).thenReturn(purchasePage);
        Page<Purchase> actual = service.getUserPurchases(userId, pageable);
        assertEquals(purchases, actual);
    }

    @Test
    void getUserPurchases_fail_userNotFound() {
        when(purchaseRepository.getUserPurchases(userId, pageable)).thenThrow(new UserNotFoundException(userId));
        assertThrows(UserNotFoundException.class, () -> service.getUserPurchases(userId, pageable));
    }

    @Test
    void getAllPurchases() {
        when(purchaseRepository.findAll(pageable)).thenReturn(purchasePage);
        Page<Purchase> actual = service.getAllPurchases(pageable);
        assertEquals(purchasePage, actual);
    }

    @Test
    void getPrimaryTags() {
        when(purchaseRepository.getPrimaryTags(pageable)).thenReturn(tagPage);
        Page<Tag> actual = service.getPrimaryTags(pageable);
        assertEquals(tagPage, actual);
    }

    @Test
    void getUserPrimaryTags_success() {
        when(purchaseRepository.getUserPrimaryTags(userId, pageable)).thenReturn(tagPage);
        Page<Tag> actual = service.getUserPrimaryTags(userId, pageable);
        assertEquals(tagPage, actual);
    }

    @Test
    void purchaseCertificate_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));
        when(purchaseRepository.save(any())).thenReturn(purchase);
        Purchase actual = service.purchaseCertificate(userId, certificateId);
        assertEquals(purchase, actual);
    }

    @Test
    void purchaseCertificate_fail_userNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));
        when(purchaseRepository.save(purchase)).thenReturn(purchase);
        assertThrows(UserNotFoundException.class, () -> service.purchaseCertificate(userId, certificateId));
    }

    @Test
    // todo: use argument captor; check that methods were actually called
    void purchaseCertificate_fail_certificateNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.empty());
        when(purchaseRepository.save(purchase)).thenReturn(purchase);
        assertThrows(CertificateNotFoundException.class, () -> service.purchaseCertificate(userId, certificateId));
    }

    @Test
    void getPurchaseById_success() {
        when(purchaseRepository.getById(purchaseId)).thenReturn(purchase);
        Purchase actual = service.getPurchaseById(purchaseId);
        assertEquals(purchase, actual);
    }

    @Test
    void getPurchaseById_fail_purchaseNotFound() {
        when(purchaseRepository.getById(purchaseId)).thenThrow(new PurchaseNotFoundException(purchaseId));
        assertThrows(PurchaseNotFoundException.class, () -> service.getPurchaseById(purchaseId));
    }

    @Test
    // todo: check that repo method called + for other delete methods tests
    void deletePurchase_success() {
        service.deletePurchase(purchaseId);
    }

    static class Data {

        static final long userId = 1L;
        static final User user = new User();
        static final long certificateId = 2L;
        static final Certificate certificate = new Certificate();
        static final long purchaseId = 3L;
        static final Purchase purchase = new Purchase(user, certificate);

        static final int pageNumber = 0;
        static final int pageSize = 10;
        static final Pageable pageable = PageRequest.of(pageNumber, pageSize);
        static final long elementsTotal = 25;

        static final List<Purchase> purchases = Collections.unmodifiableList(
                IntStream.range(0, pageSize).mapToObj(i -> new Purchase()).collect(Collectors.toList()));
        static final Page<Purchase> purchasePage = new PageImpl<>(purchases, pageable, elementsTotal);

        static final List<Tag> tags = Collections.unmodifiableList(
                IntStream.range(0, pageSize).mapToObj(i -> new Tag()).collect(Collectors.toList()));
        static final Page<Tag> tagPage = new PageImpl<>(tags, pageable, elementsTotal);
    }

}