package com.epam.esm.repository;

import com.epam.esm.model.Certificate;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface CustomCertificateRepository {

    List<Certificate> getAllCertificates(int limit, long offset);

    Certificate createCertificate(Certificate certificate);

    Certificate updateCertificate(Certificate certificate);

    Certificate getCertificateForUpdate(long id);

    Page<Certificate> searchCertificates(Map<String, String> parameters, int pageNumber, int pageSize);

//    Page<Certificate> searchCertificatesByTagNames(List<String> tagNames, long limit, long offset);

    Page<Certificate> searchCertificatesByTagNames(List<String> tagNames, int pageNumber, int pageSize);

//    Certificate updateCertificate(Certificate certificate);

}
