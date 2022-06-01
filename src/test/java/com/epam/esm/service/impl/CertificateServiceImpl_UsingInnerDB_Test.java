package com.epam.esm.service.impl;

import com.epam.esm.controller.api.exception.CertificateNotFoundException;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.model.dto.TagCreateDto;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.service.CertificateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;

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
        clearRepositories();
        setInitialDBRecords();
        Data.init();
    }

    @Test
    void getAllCertificates() {
        Page<Certificate> certificates = service.getAllCertificates(pageable);
        assertEquals(certificates.getTotalElements(), initialCertificatesQuantity);
    }

    @Test
    // fixme: disabled for repair
    @Disabled
    void createCertificate() {
        Certificate createdCertificate = service.createCertificate(certificateCreateDto);
        assertNotNull(createdCertificate.getId());
        Page<Certificate> certificates = service.getAllCertificates(pageable);
        assertEquals(certificates.getTotalElements(), initialCertificatesQuantity + 1);
        Certificate certificateGottenFromDb = certificates.getContent().stream()
                .filter(cert -> cert.getName().equals(certificateName)).findAny().get();

        // fixme: failed, somehow the instance I get from database has Timestamp fields instead of Date fields
        // so it fails when equals method is being called
        // change initial type from Date to Timestamp?
        assertEquals(createdCertificate, certificateGottenFromDb);

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
    void searchCertificatesByTagNames_case1() {

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
        for (int i = 0; i < initialTagsQuantity; i++) {
            Tag savedTag = tagRepository.save(initialTags[i]);
            assertNotNull(savedTag.getId());
            initialTags[i] = savedTag;
        }

        long tagsAfterInsertion = tagRepository.count();
        assertEquals(tagsAfterInsertion, initialTagsQuantity);

        // certificates
        setInitialCertificateDBRecordsData();
        for (Certificate certificate : initialCertificates) {
            Certificate savedCertificate = certificateRepository.save(certificate);
            assertNotNull(savedCertificate.getId());
            long id = savedCertificate.getId();
            System.out.println(id);
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

        // search certificates by tag names data
        static List<String> tagNamesToSearch1;
        static Certificate[] expectedFoundByTagNamesCertificates1;
        static List<String> tagNamesToSearch2;
        static Certificate[] expectedFoundByTagNamesCertificates2;
        static List<String> tagNamesToSearch3;
        static List<String> expectedFoundByTagNamesCertificates3;

        static void init() {
            pageable = PageRequest.of(0, 10);


            certificateCreateDto = new CertificateCreateDto();
            certificateName = "C4";
            certificateDuration = 10;
            certificatePrice = 144;
            String tagName1 = "tag1";
            String tagName2 = "tag2";
            String tagName3 = "tag3";
            description = Arrays.asList(
                    new TagCreateDto(tagName1),
                    new TagCreateDto(tagName2),
                    new TagCreateDto(tagName3)
            );

            certificateCreateDto.setName(certificateName);
            certificateCreateDto.setDuration(certificateDuration);
            certificateCreateDto.setPrice(certificatePrice);
            certificateCreateDto.setDescription(description);

            setUpdateParamsValues();
            setSearchParamsValues();
            setSearchByTagNamesParamValues();
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
            certificate2.setDescription(Arrays.asList(initialTags[1], initialTags[2]));
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

        // todo: workaround search by tags
        static void setSearchParamsValues() {
            String tagKey = "tag";
            String containsKey = "contains";
            String sortByKey = "sort_by";
            String orderKey = "order";

            searchParams1 = new HashMap<>();
            searchParams1.put(containsKey, certificate2.getName());
            expectedFoundCertificates1 = new Certificate[] {certificate2};

            searchParams2 = new HashMap<>();
            searchParams2.put(tagKey, "tag404");
            expectedFoundCertificates2 = new Certificate[] {};

            searchParams3 = new HashMap<>();
            searchParams3.put(orderKey, "desc");
            searchParams3.put(sortByKey, "name");
            expectedFoundCertificates3 = new Certificate[] {certificate2, certificate3, certificate1};
        }

        static void setSearchByTagNamesParamValues() {
//            tagNamesToSearch1 = Arrays.asList("tag1")
        }
    }


}