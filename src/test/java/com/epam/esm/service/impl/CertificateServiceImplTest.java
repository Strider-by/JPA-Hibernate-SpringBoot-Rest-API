package com.epam.esm.service.impl;

import com.epam.esm.controller.api.exception.CertificateNotFoundException;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.model.util.DtoConverter;
import com.epam.esm.repository.CertificateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
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

class CertificateServiceImplTest {

    @Mock
    private CertificateRepository repository;
    @InjectMocks
    private CertificateServiceImpl service;
    @Captor
    ArgumentCaptor argCaptor;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCertificate() {
        when(repository.createCertificate(any())).thenReturn(createdCertificate);
        Certificate actual = service.createCertificate(certificateCreateDto);
        assertTrue(createdCertificate == actual);
        verify(repository).createCertificate((Certificate) argCaptor.capture());

        Certificate arg = (Certificate) argCaptor.getValue();
        assertEquals(certificateCreateDto.getDuration(), arg.getDuration());
        assertEquals(certificateCreateDto.getName(), arg.getName());
        assertEquals(certificateCreateDto.getPrice(), arg.getPrice());
        assertNotNull(arg.getCreateDate());
        assertNotNull(arg.getLastUpdateDate());
        assertNull(arg.getId());
    }

    @Test
    void getCertificate_success() {
        when(repository.getCertificateById(certificateId)).thenReturn(foundCertificate);
        Certificate actual = service.getCertificate(certificateId);
        assertTrue(foundCertificate == actual);
        verify(repository).getCertificateById(certificateId);
    }

    @Test
    void getCertificate_fail_certificateNotFound() {
        when(repository.getCertificateById(certificateId)).thenThrow(new CertificateNotFoundException(certificateId));
        assertThrows(CertificateNotFoundException.class, () -> service.getCertificate(certificateId));
        verify(repository).getCertificateById(certificateId);
    }

    @Test
    void getAllCertificates() {
        when(repository.findAll(pageable)).thenReturn(certificatePage);
        Page<Certificate> actual = service.getAllCertificates(pageable);
        assertTrue(certificatePage == actual);
        verify(repository).findAll(pageable);
    }

    @Test
    void updateCertificate_success() {
        when(repository.getCertificateById(certificateId)).thenReturn(foundCertificate);
        when(repository.updateCertificate(foundCertificate)).thenReturn(updatedCertificate);
        Certificate actual = service.updateCertificate(certificateId, certificateUpdateParams);
        assertTrue(updatedCertificate == actual);
        verify(repository).getCertificateById(certificateId);
        verify(repository).updateCertificate(foundCertificate);
    }

    @Test
    void updateCertificate_fail_certificateNotFound() {
        when(repository.getCertificateById(certificateId)).thenThrow(new CertificateNotFoundException(certificateId));
        assertThrows(CertificateNotFoundException.class, () -> service.updateCertificate(certificateId, certificateUpdateParams));
        verify(repository).getCertificateById(certificateId);
    }

    @Test
    void deleteCertificate_success() {
        service.deleteCertificate(certificateId);
        verify(repository).deleteCertificateById(certificateId);
    }

    @Test
    void deleteCertificate_fail_certificateNotFound() {
        doThrow(new CertificateNotFoundException(certificateId)).when(repository).deleteCertificateById(certificateId);
        assertThrows(CertificateNotFoundException.class, () -> service.deleteCertificate(certificateId));
        verify(repository).deleteCertificateById(certificateId);
    }

    @Test
    void searchCertificates() {
        when(repository.searchCertificates(searchParams, pageable)).thenReturn(certificatePage);
        Page<Certificate> actual = service.searchCertificates(searchParams, pageable);
        assertTrue(certificatePage == actual);
        verify(repository).searchCertificates(searchParams, pageable);
    }

    @Test
    void searchCertificatesByTagNames() {
        when(repository.searchCertificatesByTagNames(tagNames, pageable)).thenReturn(certificatePage);
        Page<Certificate> actual = service.searchCertificatesByTagNames(tagNames, pageable);
        assertTrue(certificatePage == actual);
        verify(repository).searchCertificatesByTagNames(tagNames, pageable);
    }


    static class Data {

        static final long certificateId = 1L;
        static final String certificateName = "generic name";
        static final int certificatePrice = 200;
        static final int duration = 2;

        static final CertificateCreateDto certificateCreateDto = new CertificateCreateDto();
        static final Certificate createdCertificate;
        static final Certificate foundCertificate = new Certificate();
        static final Certificate updatedCertificate = new Certificate();

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

        static {
            certificateCreateDto.setName(certificateName);
            certificateCreateDto.setPrice(certificatePrice);
            certificateCreateDto.setDuration(duration);
            createdCertificate = DtoConverter.toCertificate(certificateCreateDto);
            createdCertificate.setId(certificateId);
        }
    }

}