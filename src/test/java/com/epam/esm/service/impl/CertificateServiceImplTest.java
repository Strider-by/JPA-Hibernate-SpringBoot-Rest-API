package com.epam.esm.service.impl;

import com.epam.esm.controller.api.exception.CertificateNotFoundException;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.service.CertificateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.epam.esm.service.impl.CertificateServiceImplTest.Data.*;

@SpringBootTest
class CertificateServiceImplTest {

    @Mock
    private CertificateRepository certificateRepository;
    @Mock
    private TagRepository tagRepository;
    private CertificateService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new CertificateServiceImpl(certificateRepository, tagRepository);
    }

    @Test
    void createCertificate() {
        when(certificateRepository.createCertificate(any())).thenReturn(createdCertificate);
        Certificate actual = service.createCertificate(certificateCreateDto);
        assertEquals(createdCertificate, actual);
    }

    @Test
    void getCertificate_success() {
        when(certificateRepository.getCertificateById(certificateId)).thenReturn(foundCertificate);
        Certificate actual = service.getCertificate(certificateId);
        assertEquals(foundCertificate, actual);
    }

    @Test
    void getCertificate_fail_certificateNotFound() {
        when(certificateRepository.getCertificateById(certificateId)).thenThrow(new CertificateNotFoundException(certificateId));
        assertThrows(CertificateNotFoundException.class, () -> service.getCertificate(certificateId));
    }

    @Test
    void getAllCertificates() {
        when(certificateRepository.findAll(pageable)).thenReturn(certificatePage);
        Page<Certificate> actual = service.getAllCertificates(pageable);
        assertEquals(certificatePage, actual);
    }

    @Test
    void updateCertificate_success() {
        when(certificateRepository.getCertificateById(certificateId)).thenReturn(foundCertificate);
        when(certificateRepository.updateCertificate(foundCertificate)).thenReturn(updatedCertificate);
        Certificate actual = service.updateCertificate(certificateId, certificateUpdateParams);
        assertEquals(updatedCertificate, actual);
    }

    @Test
    void updateCertificate_fail_certificateNotFound() {
        when(certificateRepository.getCertificateById(certificateId)).thenThrow(new CertificateNotFoundException(certificateId));
        assertThrows(CertificateNotFoundException.class, () -> service.updateCertificate(certificateId, certificateUpdateParams));
    }

    @Test
    void deleteCertificate_success() {
        service.deleteCertificate(certificateId);
    }

    @Test
    void deleteCertificate_fail_certificateNotFound() {
        doThrow(new CertificateNotFoundException(certificateId)).when(certificateRepository).deleteCertificateById(certificateId);
        assertThrows(CertificateNotFoundException.class, () -> service.deleteCertificate(certificateId));
    }

    @Test
    void searchCertificates() {
        when(certificateRepository.searchCertificates(searchParams, pageable)).thenReturn(certificatePage);
        Page<Certificate> actual = service.searchCertificates(searchParams, pageable);
        assertEquals(certificatePage, actual);
    }

    @Test
    void searchCertificatesByTagNames() {
        when(certificateRepository.searchCertificatesByTagNames(tagNames, pageable)).thenReturn(certificatePage);
        Page<Certificate> actual = service.searchCertificatesByTagNames(tagNames, pageable);
        assertEquals(certificatePage, actual);
    }


    static class Data {

        static final long certificateId = 1L;

        static final CertificateCreateDto certificateCreateDto = new CertificateCreateDto();
        static final Certificate createdCertificate = new Certificate();
        static final Certificate foundCertificate = createdCertificate;
        static final Certificate updatedCertificate = createdCertificate;

        static final int pageNumber = 0;
        static final int pageSize = 10;
        static final Pageable pageable = PageRequest.of(pageNumber, pageSize);
        static final long elementsTotal = 25;
        static final List<Certificate> certificates = Collections.unmodifiableList(
                IntStream.range(0, pageSize).mapToObj(i -> new Certificate()).collect(Collectors.toList()));
        static final Page<Certificate> certificatePage = new PageImpl<>(certificates, pageable, elementsTotal);

        static final MultiValueMap<String, String> certificateUpdateParams = new LinkedMultiValueMap<>();
        static final Map<String, String> searchParams = new HashMap<>();
        static final List<String> tagNames = new ArrayList<>();
    }

}