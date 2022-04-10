package com.epam.esm.dao;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.model.dto.CertificateUpdateDto;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CertificateDao {

    Certificate createCertificate(CertificateCreateDto dto, Date createdAt);

    Certificate getCertificateById(long id);

    List<Certificate> getAllCertificates(long limit, long offset);

    Certificate update(CertificateUpdateDto dto);

    boolean delete(long id);

    List<Certificate> searchCertificates(long limit, long offset, Map<String, String> parameters);

    List<Certificate> searchCertificatesByTagNames(List<String> tagNames, long limit, long offset);

}
