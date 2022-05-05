package com.epam.esm.service;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.dto.CertificateCreateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

public interface CertificateService {

    Certificate createCertificate(CertificateCreateDto dto);

    Certificate getCertificate(long id);

    Page<Certificate> getAllCertificates(Pageable pageable);

    Certificate updateCertificate(long id, MultiValueMap<String, String> params);

    void deleteCertificate(long id);

    Page<Certificate> searchCertificates(Map<String, String> parameters, Pageable pageable);

    Page<Certificate> searchCertificatesByTagNames(List<String> tagNames, Pageable pageable);

}
