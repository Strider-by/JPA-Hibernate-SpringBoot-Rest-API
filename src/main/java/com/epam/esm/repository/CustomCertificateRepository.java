package com.epam.esm.repository;

import com.epam.esm.model.Certificate;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

public interface CustomCertificateRepository {

    List<Certificate> getAllCertificates(long limit, long offset);

    Certificate createCertificate(Certificate certificate);

    Certificate getCertificateForUpdate(long id);

    List<Certificate> searchCertificates(Map<String, String> parameters, long limit, long offset);

    List<Certificate> searchCertificatesByTagNames(List<String> tagNames, long limit, long offset);

//    Certificate updateCertificate(Certificate certificate);

}
