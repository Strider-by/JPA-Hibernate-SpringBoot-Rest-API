package com.epam.esm.service.impl;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.model.util.DtoConverter;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.epam.esm.model.util.DtoConverter.*;

@Service
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private TagRepository tagRepository;

    public CertificateServiceImpl(CertificateRepository certificateRepository, TagRepository tagRepository) {
        this.certificateRepository = certificateRepository;
        this.tagRepository = tagRepository;
    }

    public CertificateServiceImpl() {
    }

    @Override
    // todo: ask about why rollback doesn't want to work
    @Transactional(rollbackFor = {Throwable.class})
    public Certificate createCertificate(CertificateCreateDto dto) {
        Certificate certificate = toCertificate(dto);
        affixTimestampsOnCreation(certificate);
        return certificateRepository.createCertificate(certificate);
    }

    @Override
    public Certificate getCertificate(long id) {
        Certificate certificate = certificateRepository.getCertificateById(id);
        return certificate;
    }

    @Override
    public Page<Certificate> getAllCertificates(Pageable pageable) {
        return certificateRepository.findAll(pageable);
    }

    @Override
    public Certificate updateCertificate(long id, MultiValueMap<String, String> params) {
        Certificate certificate = certificateRepository.getCertificateById(id);
        updateCertificateProperties(certificate, params);
        return certificateRepository.updateCertificate(certificate);
    }

    @Override
    public void deleteCertificate(long id) {
        certificateRepository.deleteCertificateById(id);
    }

    @Override
    public Page<Certificate> searchCertificates(Map<String, String> parameters, Pageable pageable) {
        return certificateRepository.searchCertificates(parameters, pageable);
    }

    @Override
    public Page<Certificate> searchCertificatesByTagNames(List<String> tagNames, Pageable pageable) {
        return certificateRepository.searchCertificatesByTagNames(tagNames, pageable);
    }

    private void affixTimestampsOnCreation(Certificate certificate) {
        Timestamp date = new Timestamp(new Date().getTime());
        certificate.setCreateDate(date);
        certificate.setLastUpdateDate(date);
    }

    private void affixTimestampOnUpdate(Certificate certificate) {
        if (certificate != null) {
            Timestamp date = new Timestamp(new Date().getTime());
            certificate.setLastUpdateDate(date);
        }
    }

    private void updateCertificateProperties(Certificate certificate, MultiValueMap<String, String> params) {
        if (params.containsKey("name")) certificate.setName(params.getFirst("name"));
        if (params.containsKey("price")) certificate.setPrice(Integer.parseInt(params.getFirst("price")));
        if (params.containsKey("duration")) certificate.setDuration(Integer.parseInt(params.getFirst("duration")));

        if (params.containsKey("description")) {
            List<Tag> description = params.get("description").stream()
                    .map(DtoConverter::toTag)
                    .collect(Collectors.toList());

            certificate.setDescription(description);
        }

        affixTimestampOnUpdate(certificate);
    }

}
