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
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.service.PurchaseService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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


    @BeforeEach
    void setUp() {
        setInitialDbRecords();
    }

    @AfterEach
    void cleanUp() {
        clearRepositories();
    }


    @Test
    public void getPurchaseById_success() {
        List<Long> purchasesIds = getPurchasesIdsThatIsPresentInDB();
        for (long id : purchasesIds) {
            Purchase purchase = service.getPurchaseById(id);
            assertEquals(purchase.getId(), id);
        }
    }

    @Test
    public void getPurchaseById_fail_purchaseNotExists() {
        long id = getPurchaseIdThatIsNotPresentInDB();
        assertThrows(PurchaseNotFoundException.class, () -> service.getPurchaseById(id));
    }

    @Test
    public void getAllPurchases() {
        Pageable pageable = Data.pageable;
        Page<Purchase> purchasePage = service.getAllPurchases(pageable);
        assertEquals(purchasePage.getTotalElements(), Data.InitialDbRecords.initialPurchases.length);
        assertEquals(purchasePage.getContent().size(), Data.InitialDbRecords.initialPurchases.length);
    }

    @Test
    // I can't use @ParameterizedTest here using @MethodSource:
    // somehow test framework fetch data I need before it has been saved at DB and has gotten an id set
    // so instead of separate test fot each user I have to iterate through all users in a single test
    public void getUserPurchases_success() {
        Pageable pageable = Data.pageable;
        List<Long> registeredUsersIds = getRegisteredUsersIds();
        for (long userId : registeredUsersIds) {
            Page<Purchase> purchasePage = service.getUserPurchases(userId, pageable);
            Set<Long> purchaseIdsFromDb = purchasePage.getContent().stream().map(Purchase::getId).collect(Collectors.toSet());
            Set<Long> purchaseIdsThatBelongToCurrentUser = Arrays.stream(Data.InitialDbRecords.initialPurchases)
                    .filter(purchase -> purchase.getUser().getId() == userId)
                    .map(Purchase::getId)
                    .collect(Collectors.toSet());
            assertEquals(purchaseIdsFromDb, purchaseIdsThatBelongToCurrentUser);
        }
    }

    @Test
    public void getUserPurchases_fail_userNotExists() {
        long userId = getUserIdThatIsNotPresentInDB();
        Pageable pageable = Data.pageable;
        assertThrows(UserNotFoundException.class, () -> service.getUserPurchases(userId, pageable));
    }

    @Test
    // Warning: since pagination involved, make sure the returned page can fit all the tags, else the test can fail.
    public void getUserPrimaryTags_success() {
        List<Long> registeredUsersIds = getRegisteredUsersIds();
        for (Long registeredUsersId : registeredUsersIds) {
            Set<Tag> expectedUserPrimaryTags = getUserPrimaryTags(registeredUsersId);
            Page<Tag> actualUserPrimaryTagsPage = service.getUserPrimaryTags(registeredUsersId, Data.pageable);
            Set<Tag> actualUserPrimaryTags = new HashSet<>(actualUserPrimaryTagsPage.getContent());
            assertEquals(expectedUserPrimaryTags, actualUserPrimaryTags);
        }
    }

    @Test
    public void getUserPrimaryTags_fail_userNotExists() {
        long userId = getUserIdThatIsNotPresentInDB();
        Pageable pageable = Data.pageable;
        assertThrows(UserNotFoundException.class, () -> service.getUserPrimaryTags(userId, pageable));
    }

    @Test
    // Warning: since pagination involved, make sure the returned page can fit all the tags, else the test can fail.
    public void getPrimaryTagsTest() {
        Set<Tag> expectedPrimaryTags = getPrimaryTags();
        Page<Tag> actualPrimaryTagsPage = service.getPrimaryTags(Data.pageable);
        Set<Tag> actualPrimaryTags = new HashSet<>(actualPrimaryTagsPage.getContent());
        assertEquals(expectedPrimaryTags, actualPrimaryTags);
    }

    @Test
    public void purchaseCertificate_success() {
        User user = Data.InitialDbRecords.initialUsers[0];
        long userId = user.getId();
        Certificate certificate = Data.InitialDbRecords.initialCertificates[0];
        long certificateId = certificate.getId();

        Purchase createdPurchase = service.purchaseCertificate(userId, certificateId);
        assertNotNull(createdPurchase.getId());
        assertEquals(createdPurchase.getUser().getId(), user.getId());
        assertEquals(createdPurchase.getCertificate(), certificate);
        assertEquals(createdPurchase.getCost(), certificate.getPrice());

        assertEquals(Data.InitialDbRecords.initialPurchases.length + 1, purchaseRepository.count());
    }

    @Test
    public void purchaseCertificate_fail_userNotExists() {
        long userId = getUserIdThatIsNotPresentInDB();
        long certificateId = Data.InitialDbRecords.initialCertificates[0].getId();
        assertThrows(UserNotFoundException.class, () -> service.purchaseCertificate(userId, certificateId));
    }

    @Test
    public void purchaseCertificate_fail_certificateNotExists() {
        long userId = Data.InitialDbRecords.initialUsers[0].getId();
        long certificateId = getCertificateIdThatIsNotPresentInDB();
        assertThrows(CertificateNotFoundException.class, () -> service.purchaseCertificate(userId, certificateId));
    }

    @Test
    public void deletePurchase_success() {
        long purchaseId = Data.InitialDbRecords.initialPurchases[0].getId();
        assertNotNull(service.getPurchaseById(purchaseId));
        service.deletePurchase(purchaseId);
        assertEquals(Data.InitialDbRecords.initialPurchases.length - 1, purchaseRepository.count());
        assertThrows(PurchaseNotFoundException.class, () -> service.deletePurchase(purchaseId));
    }

    @Test
    public void deletePurchase_fail_purchaseNotExists() {
        long purchaseId = getPurchaseIdThatIsNotPresentInDB();
        assertThrows(PurchaseNotFoundException.class, () -> service.deletePurchase(purchaseId));
    }


    private Set<Tag> getPrimaryTags() {
        return getPrimaryTags(OptionalLong.empty());
    }

    private Set<Tag> getUserPrimaryTags(long userId) {
        return getPrimaryTags(OptionalLong.of(userId));
    }

    private Set<Tag> getPrimaryTags(OptionalLong userId) {
        Predicate<Purchase> purchasePredicate = purchase ->
                userId.isPresent()
                        ? purchase.getUser().getId() == userId.getAsLong()
                        : true;

        Map<Tag, Long> tagNamesToCountMap = Arrays.stream(Data.InitialDbRecords.initialPurchases)
                .filter(purchasePredicate)
                .map(Purchase::getCertificate)
                .map(Certificate::getDescription)
                .flatMap(List::stream)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<Long, Set<Tag>> countToTagNamesMap = tagNamesToCountMap.entrySet().stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toSet())));

        Comparator<Map.Entry<Long, Set<Tag>>> comparator = Comparator.comparingLong(Map.Entry::getKey);
        Comparator<Map.Entry<Long, Set<Tag>>> reversedComparator = comparator.reversed();
        Set<Tag> tags = countToTagNamesMap.entrySet().stream()
                .sorted(reversedComparator)
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(Collections.emptySet());

        return tags;
    }


    static List<Long> getRegisteredUsersIds() {
        return Arrays.stream(Data.InitialDbRecords.initialUsers).map(User::getId).collect(Collectors.toList());
    }

    static List<Long> getPurchasesIdsThatIsPresentInDB() {
        return Arrays.stream(Data.InitialDbRecords.initialPurchases).map(Purchase::getId).collect(Collectors.toList());
    }

    long getPurchaseIdThatIsNotPresentInDB() {
        return -1;
    }

    long getUserIdThatIsNotPresentInDB() {
        return -1;
    }

    long getCertificateIdThatIsNotPresentInDB() {
        return -1;
    }

    void setInitialDbRecords() {
        Data.InitialDbRecords.initTagsData();
        Data.InitialDbRecords.initCertificatesData();
        Certificate[] certificates = Data.InitialDbRecords.initialCertificates;
        for (int i = 0; i < certificates.length; i++) {
            Certificate createdCertificate = certificateRepository.createCertificate(certificates[i]);
            assertNotNull(createdCertificate.getId());
            certificates[i] = createdCertificate;
        }

        User[] users = Data.InitialDbRecords.initialUsers;
        for (int i = 0; i < users.length; i++) {
            User createdUser = userRepository.save(users[i]);
            assertNotNull(createdUser.getId());
            users[i] = createdUser;
        }

        Data.InitialDbRecords.initPurchaseData();
        Purchase[] purchases = Data.InitialDbRecords.initialPurchases;
        for (int i = 0; i < purchases.length; i++) {
            Purchase createdPurchase = purchaseRepository.save(purchases[i]);
            assertNotNull(createdPurchase.getId());
            purchases[i] = createdPurchase;
        }

        assertEquals(certificateRepository.count(), Data.InitialDbRecords.initialCertificates.length);
        assertEquals(tagRepository.count(), Data.InitialDbRecords.initialTags.length);
        assertEquals(userRepository.count(), Data.InitialDbRecords.initialUsers.length);
        assertEquals(purchaseRepository.count(), Data.InitialDbRecords.initialPurchases.length);
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

    static class Data {

        public static Pageable pageable = PageRequest.of(0, 10);

        static class InitialDbRecords {

            static Tag tag1;
            static Tag tag2;
            static Tag tag3;
            public static Tag[] initialTags;

            static void initTagsData() {
                tag1 = new Tag("tag1");
                tag2 = new Tag("tag2");
                tag3 = new Tag("tag3");
                initialTags = new Tag[] {tag1, tag2, tag3};
            }

            static Certificate certificate1;
            static Certificate certificate2;
            static Certificate certificate3;
            public static Certificate[] initialCertificates;

            static void initCertificatesData() {
                certificate1 = new Certificate();
                certificate2 = new Certificate();
                certificate3 = new Certificate();
                initialCertificates = new Certificate[] {certificate1, certificate2, certificate3};
                certificate1.setName("certificate1");
                certificate1.setDescription(Arrays.asList(tag1, tag3));
                certificate2.setName("certificate2");
                certificate2.setDescription(Arrays.asList(tag2, tag3));
                certificate3.setName("certificate3");
                certificate3.setDescription(Arrays.asList(tag1, tag2));
            }

            static User user1 = new User();
            static User user2 = new User();
            static User user3 = new User();
            public static User[] initialUsers = {user1, user2, user3};

            static Purchase purchase1;
            static Purchase purchase2;
            static Purchase purchase3;
            static Purchase purchase4;
            static Purchase purchase5;
            static Purchase purchase6;
            static Purchase purchase7;
            public static Purchase[] initialPurchases;

            static void initPurchaseData() {
                purchase1 = new Purchase();
                purchase2 = new Purchase();
                purchase3 = new Purchase();
                purchase4 = new Purchase();
                purchase5 = new Purchase();
                purchase6 = new Purchase();
                purchase7 = new Purchase();

                initialPurchases = new Purchase[] {purchase1, purchase2, purchase3, purchase4, purchase5, purchase6, purchase7};

                purchase1.setUser(initialUsers[0]);
                purchase1.setCertificate(initialCertificates[0]);
                purchase1.setCost(initialCertificates[0].getPrice());
                purchase1.setTimestamp(new Timestamp(new Date().getTime()));
                purchase2.setUser(initialUsers[0]);
                purchase2.setCertificate(initialCertificates[1]);
                purchase2.setCost(initialCertificates[1].getPrice());
                purchase2.setTimestamp(new Timestamp(new Date().getTime()));
                purchase3.setUser(initialUsers[1]);
                purchase3.setCertificate(initialCertificates[1]);
                purchase3.setCost(initialCertificates[1].getPrice());
                purchase3.setTimestamp(new Timestamp(new Date().getTime()));
                purchase4.setUser(initialUsers[1]);
                purchase4.setCertificate(initialCertificates[0]);
                purchase4.setCost(initialCertificates[0].getPrice());
                purchase4.setTimestamp(new Timestamp(new Date().getTime()));
                purchase5.setUser(initialUsers[1]);
                purchase5.setCertificate(initialCertificates[2]);
                purchase5.setCost(initialCertificates[2].getPrice());
                purchase5.setTimestamp(new Timestamp(new Date().getTime()));
                purchase6.setUser(initialUsers[1]);
                purchase6.setCertificate(initialCertificates[2]);
                purchase6.setCost(initialCertificates[2].getPrice());
                purchase6.setTimestamp(new Timestamp(new Date().getTime()));
                purchase7.setUser(initialUsers[1]);
                purchase7.setCertificate(initialCertificates[2]);
                purchase7.setCost(initialCertificates[2].getPrice());
                purchase7.setTimestamp(new Timestamp(new Date().getTime()));

            }

        }

    }

}