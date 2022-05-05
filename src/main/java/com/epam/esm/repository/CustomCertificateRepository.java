package com.epam.esm.repository;

import com.epam.esm.model.Certificate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface CustomCertificateRepository {

    Certificate createCertificate(Certificate certificate);

    Certificate updateCertificate(Certificate certificate);

    Certificate getCertificateForUpdate(long id);

    void deleteCertificateById(long id);

    Page<Certificate> searchCertificates(Map<String, String> parameters, Pageable pageable);

    Page<Certificate> searchCertificatesByTagNames(List<String> tagNames, Pageable pageable);

}
