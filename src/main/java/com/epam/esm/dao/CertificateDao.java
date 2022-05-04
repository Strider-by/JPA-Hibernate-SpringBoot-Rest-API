package com.epam.esm.dao;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.model.dto.CertificateUpdateDto;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface CertificateDao {

    Certificate createCertificate(CertificateCreateDto dto, Date createdAt);

    Certificate getCertificateById(long id);

    List<Certificate> getAllCertificates(long limit, long offset);

    Certificate update(CertificateUpdateDto dto);

    boolean delete(long id);

    List<Certificate> searchCertificates(int pageNumber, int pageSize, Map<String, String> parameters);

    Page<Certificate> searchCertificatesByTagNames(List<String> tagNames, int pageNumber, int pageSize);

}
