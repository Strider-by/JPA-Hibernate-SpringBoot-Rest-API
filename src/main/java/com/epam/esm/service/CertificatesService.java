package com.epam.esm.service;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.dto.CertificateCreateDto;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

public interface CertificatesService {

    Certificate createCertificate(CertificateCreateDto dto);

    Certificate getCertificate(long id);

    List<Certificate> getAllCertificates(long limit, long offset);

    Certificate updateCertificate(long id, MultiValueMap<String, String> params);

    boolean deleteCertificate(long id);

    List<Certificate> searchCertificates(long limit, long offset, Map<String, String> parameters);

    List<Certificate> searchCertificatesByTagNames(List<String> tagNames, long limit, long offset);

}
