package com.epam.esm.service.impl;

import com.epam.esm.controller.api.exception.PurchaseNotFoundException;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Purchase;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.PurchaseRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.service.PurchaseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class PurchaseServiceImpl_UsingInnerDB_Test {

    @Autowired
    PurchaseService service;
    @Autowired
    CertificateRepository certificateRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PurchaseRepository purchaseRepository;
    @Autowired
    TagRepository tagRepository;

    private Data data;

    @BeforeEach
    void setUp() {
        data = new Data();
        setInitialDbRecords();
    }

    @AfterEach
    void cleanUp() {
        clearRepositories();
    }


    @Test
    public void getPurchaseById_success() {
        long id = getPurchaseIdThatIsPresentInDB();
        Purchase purchase = service.getPurchaseById(id);
        assertEquals(purchase.getId(), id);
    }

    @Test
    public void getPurchaseById_fail_purchaseNotExists() {
        long id = getPurchaseIdThatIsNotPresentInDB();
        assertThrows(PurchaseNotFoundException.class, () -> service.getPurchaseById(id));
    }

    @Test
    public void getAllPurchases() {
        Pageable pageable = data.initialDbRecords.pageable;
        Page<Purchase> purchasePage = service.getAllPurchases(pageable);
        assertEquals(purchasePage.getTotalElements(), data.initialDbRecords.initialPurchases.length);
        assertEquals(purchasePage.getContent().size(), data.initialDbRecords.initialPurchases.length);
    }

    @ParameterizedTest
    @MethodSource("getRegisteredUsersIds")
    public void getUserPurchases_success_case1(long userId) {
        Pageable pageable = data.initialDbRecords.pageable;
        Page<Purchase> purchasePage = service.getUserPurchases(userId, pageable);
        Set<Long> purchaseIdsFromDb = purchasePage.getContent().stream().map(Purchase::getId).collect(Collectors.toSet());
        Set<Long> purchaseIdsThatBelongToCurrentUser = Arrays.stream(data.initialDbRecords.initialPurchases)
                .filter(purchase -> purchase.getUser().getId() == userId)
                .map(Purchase::getId)
                .collect(Collectors.toSet());
        assertTrue(purchaseIdsFromDb.equals(purchaseIdsThatBelongToCurrentUser));
    }

    @Test
    public void getUserPrimaryTags() {

    }

    @Test
    public void getPrimaryTags() {

    }

    @Test
    public void purchaseCertificate() {

    }

    @Test
    public void deletePurchase() {

    }

    @Test
    public void updatePurchase() {

    }

    Stream<Arguments> getRegisteredUsersIds() {
        return Arrays.stream(data.initialDbRecords.initialUsers).map(User::getId).map(Arguments::of);
    }

    long getPurchaseIdThatIsPresentInDB() {
        return data.initialDbRecords.initialPurchases[0].getId();
    }

    long getPurchaseIdThatIsNotPresentInDB() {
        return -1;
    }


    void setInitialDbRecords() {
        Certificate[] certificates = data.initialDbRecords.initialCertificates;
        for (int i = 0; i < certificates.length; i++) {
            Certificate createdCertificate = certificateRepository.createCertificate(certificates[i]);
            assertNotNull(createdCertificate.getId());
            certificates[i] = createdCertificate;
        }

        User[] users = data.initialDbRecords.initialUsers;
        for (int i = 0; i < users.length; i++) {
            User createdUser = userRepository.save(users[i]);
            assertNotNull(createdUser.getId());
            users[i] = createdUser;
        }

        data.initialDbRecords.initPurchaseData();
        Purchase[] purchases = data.initialDbRecords.initialPurchases;
        for (int i = 0; i < purchases.length; i++) {
            Purchase createdPurchase = purchaseRepository.save(purchases[i]);
            assertNotNull(createdPurchase.getId());
            purchases[i] = createdPurchase;
        }

        assertEquals(certificateRepository.count(), data.initialDbRecords.initialCertificates.length);
        assertEquals(tagRepository.count(), data.initialDbRecords.initialTags.length);
        assertEquals(userRepository.count(), data.initialDbRecords.initialUsers.length);
        assertEquals(purchaseRepository.count(), data.initialDbRecords.initialPurchases.length);
    }

    void clearRepositories() {
        purchaseRepository.deleteAll();
        userRepository.deleteAll();
        certificateRepository.deleteAll();
        tagRepository.deleteAll();

        assertEquals(purchaseRepository.count(), 0);
        assertEquals(userRepository.count(), 0);
        assertEquals(certificateRepository.count(), 0);
        assertEquals(tagRepository.count(), 0);
    }

    class Data {
        public InitialDbRecords initialDbRecords = new InitialDbRecords();

        class InitialDbRecords {
            Pageable pageable = PageRequest.of(0, 10);

            Tag tag1 = new Tag("tag1");
            Tag tag2 = new Tag("tag2");
            Tag tag3 = new Tag("tag3");
            Tag[] initialTags = {tag1, tag2, tag3};

            Certificate certificate1 = new Certificate();
            Certificate certificate2 = new Certificate();
            Certificate certificate3 = new Certificate();
            Certificate[] initialCertificates = {certificate1, certificate2, certificate3};

            {
                certificate1.setName("certificate1");
                certificate1.setDescription(Arrays.asList(tag1, tag3));
                certificate2.setName("certificate2");
                certificate2.setDescription(Arrays.asList(tag2, tag3));
                certificate3.setName("certificate3");
                certificate3.setDescription(Arrays.asList(tag1, tag2));
            }

            User user1 = new User();
            User user2 = new User();
            User user3 = new User();
            User[] initialUsers = {user1, user2, user3};

            Purchase purchase1 = new Purchase();
            Purchase purchase2 = new Purchase();
            Purchase purchase3 = new Purchase();
            Purchase purchase4 = new Purchase();
            Purchase purchase5 = new Purchase();
            Purchase purchase6 = new Purchase();
            Purchase purchase7 = new Purchase();
            Purchase[] initialPurchases = {purchase1, purchase2, purchase3, purchase4, purchase5, purchase6, purchase7};

            void initPurchaseData() {
                purchase1.setUser(initialUsers[0]);
                purchase1.setCertificate(initialCertificates[0]);
                purchase1.setCost(initialCertificates[0].getPrice());
                purchase1.setTimestamp(new Timestamp( new Date().getTime()) );
                purchase2.setUser(initialUsers[0]);
                purchase2.setCertificate(initialCertificates[1]);
                purchase2.setCost(initialCertificates[1].getPrice());
                purchase2.setTimestamp(new Timestamp( new Date().getTime()) );
                purchase3.setUser(initialUsers[1]);
                purchase3.setCertificate(initialCertificates[1]);
                purchase3.setCost(initialCertificates[1].getPrice());
                purchase3.setTimestamp(new Timestamp( new Date().getTime()) );
                purchase4.setUser(initialUsers[1]);
                purchase4.setCertificate(initialCertificates[0]);
                purchase4.setCost(initialCertificates[0].getPrice());
                purchase4.setTimestamp(new Timestamp( new Date().getTime()) );
                purchase5.setUser(initialUsers[1]);
                purchase5.setCertificate(initialCertificates[2]);
                purchase5.setCost(initialCertificates[2].getPrice());
                purchase5.setTimestamp(new Timestamp( new Date().getTime()) );
                purchase6.setUser(initialUsers[1]);
                purchase6.setCertificate(initialCertificates[2]);
                purchase6.setCost(initialCertificates[2].getPrice());
                purchase6.setTimestamp(new Timestamp( new Date().getTime()) );
                purchase7.setUser(initialUsers[1]);
                purchase7.setCertificate(initialCertificates[2]);
                purchase7.setCost(initialCertificates[2].getPrice());
                purchase7.setTimestamp(new Timestamp( new Date().getTime()) );

            }
        }


        void init() {
            initialDbRecords = new InitialDbRecords();
        }
    }


}