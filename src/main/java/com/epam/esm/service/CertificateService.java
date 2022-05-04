package com.epam.esm.service;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.dto.CertificateCreateDto;
import org.springframework.data.domain.Page;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

public interface CertificateService {

    Certificate createCertificate(CertificateCreateDto dto);

    Certificate getCertificate(long id);

    List<Certificate> getAllCertificates(int limit, long offset);

    Page<Certificate> getAllCertificates(int pageNumber, int pageSize);

    Certificate updateCertificate(long id, MultiValueMap<String, String> params);

    boolean deleteCertificate(long id);

    Page<Certificate> searchCertificates(Map<String, String> parameters, int pageNumber, int pageSize);

//    Page<Certificate> searchCertificatesByTagNames(List<String> tagNames, int pageNumber, long pageSize);

    Page<Certificate> searchCertificatesByTagNames(List<String> tagNames, int pageNumber, int pageSize);

}
