package com.epam.esm.repository;

import com.epam.esm.model.Certificate;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Repository
public interface CustomCertificateRepository {

    List<Certificate> getAllCertificates(long limit, long offset);

    Certificate createCertificate(Certificate certificate);

    Certificate updateCertificate(long id, MultiValueMap<String, String> params);

}
