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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
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
    @InjectMocks
    private PurchaseServiceImpl service;
    @Captor
    ArgumentCaptor argCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserPurchases_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(purchaseRepository.getUserPurchases(userId, pageable)).thenReturn(purchasePage);
        Page<Purchase> actual = service.getUserPurchases(userId, pageable);
        assertTrue(purchasePage == actual);
        verify(purchaseRepository).getUserPurchases(userId, pageable);
    }

    @Test
    void getUserPurchases_fail_userNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.getUserPurchases(userId, pageable));
        verify(userRepository).findById(userId);
        verifyNoInteractions(purchaseRepository);
    }

    @Test
    void getAllPurchases() {
        when(purchaseRepository.findAll(pageable)).thenReturn(purchasePage);
        Page<Purchase> actual = service.getAllPurchases(pageable);
        assertTrue(purchasePage == actual);
        verify(purchaseRepository).findAll(pageable);
    }

    @Test
    void getPrimaryTags() {
        when(purchaseRepository.getPrimaryTags(pageable)).thenReturn(tagPage);
        Page<Tag> actual = service.getPrimaryTags(pageable);
        assertTrue(tagPage == actual);
        verify(purchaseRepository).getPrimaryTags(pageable);
    }

    @Test
    // todo: ask. In simple cases (when service does nothing with data)
    // I can skip   when ... thenReturn   part and just use verify
    void getUserPrimaryTags_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(purchaseRepository.getUserPrimaryTags(userId, pageable)).thenReturn(tagPage);
        Page<Tag> actual = service.getUserPrimaryTags(userId, pageable);
        assertTrue(tagPage == actual);
        verify(purchaseRepository).getUserPrimaryTags(userId, pageable);
    }

    @Test
    void getUserPrimaryTags_fail_userNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> service.getUserPrimaryTags(userId, pageable));
        verify(userRepository).findById(userId);
        verifyNoInteractions(purchaseRepository);
    }

    @Test
    void purchaseCertificate_success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));
        when(purchaseRepository.save((Purchase) argCaptor.capture())).thenReturn(purchase);
        Purchase actual = service.purchaseCertificate(userId, certificateId);
        assertTrue(purchase == actual);

        Purchase arg = (Purchase) argCaptor.getValue();
        assertTrue(user == arg.getUser());
        assertTrue(certificate == arg.getCertificate());
        assertNull(arg.getId());
        assertEquals(certificate.getPrice(), arg.getCost());
        assertNotNull(arg.getTimestamp());
    }

    @Test
    void purchaseCertificate_fail_userNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));
        when(purchaseRepository.save(purchase)).thenReturn(purchase);
        assertThrows(UserNotFoundException.class, () -> service.purchaseCertificate(userId, certificateId));
        verify(userRepository).findById(userId);
        verifyNoInteractions(purchaseRepository);
    }

    @Test
    void purchaseCertificate_fail_certificateNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.empty());
        when(purchaseRepository.save(purchase)).thenReturn(purchase);
        assertThrows(CertificateNotFoundException.class, () -> service.purchaseCertificate(userId, certificateId));
        verify(certificateRepository).findById(certificateId);
        verifyNoInteractions(purchaseRepository);
    }

    @Test
    void getPurchaseById_success() {
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(purchase));
        Purchase actual = service.getPurchaseById(purchaseId);
        assertTrue(purchase == actual);
        verify(purchaseRepository).findById(purchaseId);
    }

    @Test
    void getPurchaseById_fail_purchaseNotFound() {
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.empty());
        assertThrows(PurchaseNotFoundException.class, () -> service.getPurchaseById(purchaseId));
    }

    @Test
    void deletePurchase_success() {
        service.deletePurchase(purchaseId);
        verify(purchaseRepository).deletePurchaseById(purchaseId);
    }

    @Test
    void deletePurchase_fail_purchaseNotFound() {
        doThrow(new PurchaseNotFoundException(purchaseId)).when(purchaseRepository).deletePurchaseById(purchaseId);
        assertThrows(PurchaseNotFoundException.class, () -> service.deletePurchase(purchaseId));
        verify(purchaseRepository).deletePurchaseById(purchaseId);
    }

    static class Data {

        static final long userId = 1L;
        static final User user = new User();
        static final long certificateId = 2L;
        static final int certificatePrice = 40;
        static final Certificate certificate = new Certificate();
        static final long purchaseId = 3L;
        static final Purchase purchase = new Purchase(user, certificate);
        static final Purchase savedPurchase = new Purchase(user, certificate);

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

        static {
            certificate.setPrice(certificatePrice);
            savedPurchase.setId(purchaseId);
        }
    }

}