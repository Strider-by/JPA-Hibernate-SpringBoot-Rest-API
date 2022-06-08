package com.epam.esm.service.impl;

import com.epam.esm.controller.api.exception.CertificateNotFoundException;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.model.dto.TagCreateDto;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.service.CertificateService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.epam.esm.service.impl.CertificateServiceImpl_UsingInnerDB_Test.Data.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class CertificateServiceImpl_UsingInnerDB_Test {

    @Autowired
    CertificateService service;
    @Autowired
    CertificateRepository certificateRepository;
    @Autowired
    TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        setInitialDBRecords();
        Data.init();
    }

    @AfterEach
    void cleanUp() {
        clearRepositories();
    }

    @Test
    void getAllCertificates() {
        Page<Certificate> certificates = service.getAllCertificates(pageable);
        assertEquals(certificates.getTotalElements(), initialCertificatesQuantity);
    }

    @Test
    void createCertificate() {
        Certificate createdCertificate = service.createCertificate(certificateCreateDto);
        assertNotNull(createdCertificate.getId());
        Page<Certificate> certificates = service.getAllCertificates(pageable);
        assertEquals(certificates.getTotalElements(), initialCertificatesQuantity + 1);
        Certificate certificateGottenFromDb = certificates.getContent().stream()
                .filter(cert -> cert.getName().equals(certificateName)).findAny().get();
        assertEquals(createdCertificate, certificateGottenFromDb);

        Set<String> tagsFromCreateDto = certificateCreateDto.getDescription().stream()
                .map(TagCreateDto::getName)
                .collect(Collectors.toSet());

        Set<String>  certificateTagsAfterItHasBeenGottenFromDb = certificateGottenFromDb.getDescription().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        assertTrue(tagsFromCreateDto.equals(certificateTagsAfterItHasBeenGottenFromDb));
        assertEquals(certificateGottenFromDb.getDuration(), certificateDuration);
        assertEquals(certificateGottenFromDb.getPrice(), certificatePrice);
        assertEquals(certificateGottenFromDb.getName(), certificateName);
        assertNotNull(certificateGottenFromDb.getCreateDate());
        assertEquals(certificateGottenFromDb.getCreateDate(), certificateGottenFromDb.getLastUpdateDate());
    }

    @Test
    void getCertificate_certificateExists() {
        long id = getCertificateIdThatIsPresentInDB();
        Certificate certificate = service.getCertificate(id);
        assertEquals(certificate.getId(), id);
    }

    @Test
    void getCertificate_certificateNotExists() {
        long id = getCertificateIdThatIsNotPresentInDB();
        assertThrows(CertificateNotFoundException.class, () -> service.getCertificate(id));
    }

    @Test
    void updateCertificate_certificateExists() {
        long id = getCertificateIdThatIsPresentInDB();
        Certificate updatedCertificate = service.updateCertificate(id, updateParams);
        assertEquals(updatedCertificate.getName(), newName);
        assertEquals(updatedCertificate.getPrice(), Integer.parseInt(newPrice));
        assertEquals(updatedCertificate.getDuration(), Integer.parseInt(newDuration));
        List<String> updatedCertificateDescription = updatedCertificate.getDescription().stream()
                .map(Tag::getName)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        assertTrue(updatedCertificateDescription.containsAll(newDescription));
        assertEquals(updatedCertificateDescription.size(), newDescription.size());
        assertNotEquals(updatedCertificate.getCreateDate(), updatedCertificate.getLastUpdateDate());
    }

    @Test
    void updateCertificate_certificateNotExists() {
        long id = getCertificateIdThatIsNotPresentInDB();
        assertThrows(CertificateNotFoundException.class, () -> service.updateCertificate(id, updateParams));
    }

    @Test
    void deleteCertificate_certificateExists() {
        long id = getCertificateIdThatIsPresentInDB();
        service.deleteCertificate(id);
        assertEquals(initialCertificatesQuantity - 1, certificateRepository.count());
    }

    @Test
    void deleteCertificate_certificateNotExists() {
        long id = getCertificateIdThatIsNotPresentInDB();
        assertThrows(CertificateNotFoundException.class, () -> service.deleteCertificate(id));
        assertEquals(initialCertificatesQuantity, certificateRepository.count());
    }

    @Test
    void searchCertificates_case1() {
        Page<Certificate> foundCertificatesPage = service.searchCertificates(searchParams1, pageable);
        Certificate[] actualFoundCertificates = foundCertificatesPage.getContent().stream().toArray(Certificate[]::new);
        assertTrue(Arrays.equals(actualFoundCertificates, expectedFoundCertificates1));
    }

    @Test
    void searchCertificates_case2() {
        Page<Certificate> foundCertificatesPage = service.searchCertificates(searchParams2, pageable);
        Certificate[] actualFoundCertificates = foundCertificatesPage.getContent().stream().toArray(Certificate[]::new);
        assertTrue(Arrays.equals(actualFoundCertificates, expectedFoundCertificates2));
    }

    @Test
    void searchCertificates_case3() {
        Page<Certificate> foundCertificatesPage = service.searchCertificates(searchParams3, pageable);
        Certificate[] actualFoundCertificates = foundCertificatesPage.getContent().stream().toArray(Certificate[]::new);
        assertTrue(Arrays.equals(actualFoundCertificates, expectedFoundCertificates3));
    }

    @Test
    void searchCertificates_case4() {
        Page<Certificate> foundCertificatesPage = service.searchCertificates(searchParams4, pageable);
        Certificate[] actualFoundCertificates = foundCertificatesPage.getContent().stream().toArray(Certificate[]::new);
        assertTrue(Arrays.equals(actualFoundCertificates, expectedFoundCertificates4));
    }

    @Test
    void searchCertificates_case5() {
        Page<Certificate> foundCertificatesPage = service.searchCertificates(searchParams5, pageable);
        Certificate[] actualFoundCertificates = foundCertificatesPage.getContent().stream().toArray(Certificate[]::new);
        assertTrue(Arrays.equals(actualFoundCertificates, expectedFoundCertificates5));
    }

    @Test
    void searchCertificatesByTagNames_case1() {
        Page<Certificate> foundCertificatesPage = service.searchCertificatesByTagNames(tagNamesToSearch1, pageable);
        Set<Certificate> actualFoundCertificates = foundCertificatesPage.getContent().stream().collect(Collectors.toSet());
        assertEquals(expectedFoundByTagNamesCertificates1, actualFoundCertificates);
    }

    @Test
    void searchCertificatesByTagNames_case2() {
        Page<Certificate> foundCertificatesPage = service.searchCertificatesByTagNames(tagNamesToSearch2, pageable);
        Set<Certificate> actualFoundCertificates = foundCertificatesPage.getContent().stream().collect(Collectors.toSet());
        assertEquals(expectedFoundByTagNamesCertificates2, actualFoundCertificates);
    }

    @Test
    void searchCertificatesByTagNames_case3() {
        Page<Certificate> foundCertificatesPage = service.searchCertificatesByTagNames(tagNamesToSearch3, pageable);
        Set<Certificate> actualFoundCertificates = foundCertificatesPage.getContent().stream().collect(Collectors.toSet());
        assertEquals(expectedFoundByTagNamesCertificates3, actualFoundCertificates);
    }

    @Test
    void searchCertificatesByTagNames_case4() {
        Page<Certificate> foundCertificatesPage = service.searchCertificatesByTagNames(tagNamesToSearch4, pageable);
        Set<Certificate> actualFoundCertificates = foundCertificatesPage.getContent().stream().collect(Collectors.toSet());
        assertEquals(expectedFoundByTagNamesCertificates4, actualFoundCertificates);
    }

    long getCertificateIdThatIsPresentInDB() {
        return certificateRepository.findAll().stream().map(Certificate::getId).findAny().get();
    }

    long getCertificateIdThatIsNotPresentInDB() {
        return -1;
    }


    void clearRepositories() {
        certificateRepository.deleteAll();
        tagRepository.deleteAll();

        long certificatesCount = certificateRepository.count();
        long tagsCount = tagRepository.count();
        assertEquals(certificatesCount, 0);
        assertEquals(tagsCount, 0);
    }

    void setInitialDBRecords() {
        // tags
        setInitialTagDBRecordsData();

        // certificates
        setInitialCertificateDBRecordsData();
        for (Certificate certificate : initialCertificates) {
            Certificate savedCertificate = certificateRepository.createCertificate(certificate);
            assertNotNull(savedCertificate.getId());
        }

        long certificatesAfterInsertion = certificateRepository.count();
        assertEquals(certificatesAfterInsertion, initialCertificatesQuantity);
    }

    static class Data {
        static Pageable pageable;

        // initial DB records data
        static Certificate certificate1;
        static Certificate certificate2;
        static Certificate certificate3;

        static Certificate[] initialCertificates;
        static int initialCertificatesQuantity;

        static Tag tag1;
        static Tag tag2;
        static Tag tag3;

        static Tag[] initialTags;
        static int initialTagsQuantity;

        // create certificate data
        static CertificateCreateDto certificateCreateDto;
        static String certificateName;
        static int certificateDuration;
        static int certificatePrice;
        static List<TagCreateDto> description;

        // update certificate data
        static String newName;
        static MultiValueMap<String, String> updateParams;
        static String newPrice;
        static String newDuration;
        static List<String> newDescription;

        // search certificates data
        static Map<String, String> searchParams1;
        static Certificate[] expectedFoundCertificates1;

        static Map<String, String> searchParams2;
        static Certificate[] expectedFoundCertificates2;

        static Map<String, String> searchParams3;
        static Certificate[] expectedFoundCertificates3;

        static Map<String, String> searchParams4;
        static Certificate[] expectedFoundCertificates4;

        static Map<String, String> searchParams5;
        static Certificate[] expectedFoundCertificates5;

        // search certificates by tag names data
        static List<String> tagNamesToSearch1;
        static Set<Certificate> expectedFoundByTagNamesCertificates1;
        static List<String> tagNamesToSearch2;
        static Set<Certificate> expectedFoundByTagNamesCertificates2;
        static List<String> tagNamesToSearch3;
        static Set<Certificate> expectedFoundByTagNamesCertificates3;
        static List<String> tagNamesToSearch4;
        static Set<Certificate> expectedFoundByTagNamesCertificates4;

        static void init() {
            pageable = PageRequest.of(0, 10);

            setCreateParamsValues();
            setUpdateParamsValues();
            setSearchParamsValues();
            setSearchByTagNamesParamsValues();
        }

        static void setCreateParamsValues() {
            certificateCreateDto = new CertificateCreateDto();
            certificateName = "C4";
            certificateDuration = 10;
            certificatePrice = 144;
            String tagName1 = "tag_for_create_cert_test_1";
            String tagName2 = "tag_for_create_cert_test_2";
            String tagName3 = "tag1";
            description = Arrays.asList(
                    new TagCreateDto(tagName1),
                    new TagCreateDto(tagName2),
                    new TagCreateDto(tagName3)
            );

            certificateCreateDto.setName(certificateName);
            certificateCreateDto.setDuration(certificateDuration);
            certificateCreateDto.setPrice(certificatePrice);
            certificateCreateDto.setDescription(description);
        }

        static void setInitialTagDBRecordsData() {
            tag1 = new Tag("tag1");
            tag2 = new Tag("tag2");
            tag3 = new Tag("tag3");

            initialTags = new Tag[] {tag1, tag2, tag3};
            initialTagsQuantity = initialTags.length;
        }

        static void setInitialCertificateDBRecordsData() {
            certificate1 = new Certificate();
            certificate1.setName("A");
            certificate1.setDescription(Arrays.asList(initialTags[0]));
            certificate2 = new Certificate();
            certificate2.setName("B2");
            certificate2.setDescription(Arrays.asList(initialTags[0], initialTags[2]));
            certificate3 = new Certificate();
            certificate3.setName("B1");
            certificate3.setDescription(Collections.emptyList());

            initialCertificates = new Certificate[] {certificate1, certificate2, certificate3};
            initialCertificatesQuantity = initialCertificates.length;
        }

        static void setUpdateParamsValues() {
            newName = "new name";
            newPrice = "184000";
            newDuration = "14";
            newDescription = Arrays.asList("new tag 1", "new tag 2", "new tag 3");
            updateParams = new LinkedMultiValueMap<>();
            updateParams.add("name", newName);
            updateParams.add("price", newPrice);
            updateParams.add("duration", newDuration);
            updateParams.put("description", newDescription);
        }

        static void setSearchParamsValues() {
            String tagKey = "tag";
            String containsKey = "contains";
            String sortByKey = "sort_by";
            String orderKey = "order";

            searchParams1 = new HashMap<>();
            searchParams1.put(containsKey, certificate2.getName());
            expectedFoundCertificates1 = new Certificate[] {certificate2};

            searchParams2 = new HashMap<>();
            searchParams2.put(tagKey, initialTags[1].getName());
            expectedFoundCertificates2 = new Certificate[] {};

            searchParams3 = new HashMap<>();
            searchParams3.put(orderKey, "desc");
            searchParams3.put(sortByKey, "name");
            expectedFoundCertificates3 = new Certificate[] {certificate2, certificate3, certificate1};

            searchParams4 = new HashMap<>();
            searchParams4.put(sortByKey, "id");
            searchParams4.put(tagKey, initialTags[0].getName());
            expectedFoundCertificates4 = new Certificate[] {certificate1, certificate2};

            searchParams5 = new HashMap<>();
            searchParams5.put(tagKey, initialTags[2].getName());
            expectedFoundCertificates5 = new Certificate[] {certificate2};
        }

        static void setSearchByTagNamesParamsValues() {
            tagNamesToSearch1 = Arrays.asList(initialTags[0].getName());
            expectedFoundByTagNamesCertificates1 = Stream.of(certificate1, certificate2).collect(Collectors.toSet());
            tagNamesToSearch2 = Arrays.asList(initialTags[2].getName());
            expectedFoundByTagNamesCertificates2 = Stream.of(certificate2).collect(Collectors.toSet());
            tagNamesToSearch3 = Arrays.asList(initialTags[1].getName());
            expectedFoundByTagNamesCertificates3 = Collections.emptySet();
            tagNamesToSearch4 = Arrays.asList(initialTags[0].getName(), initialTags[1].getName());
            expectedFoundByTagNamesCertificates4 = Collections.emptySet();
        }
    }


}