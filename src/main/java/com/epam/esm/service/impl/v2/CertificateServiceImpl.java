package com.epam.esm.service.impl.v2;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.model.util.DtoConverter;
import com.epam.esm.repository.CertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.epam.esm.model.util.DtoConverter.*;

@Service
@Primary
public class CertificateServiceImpl implements CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private TagRepository tagRepository;

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
        Certificate certificate = certificateRepository.findById(id).orElse(null);
        return certificate;
    }

    @Override
    public List<Certificate> getAllCertificates(int limit, long offset) {
        List<Certificate> certificates = certificateRepository.getAllCertificates(limit, offset);
        return certificates;
    }

    @Override
    public Page<Certificate> getAllCertificates(int pageNumber, int pageSize) {
        return certificateRepository.findAll(PageRequest.of(pageNumber, pageSize));
        //return repository.findAll();
        //List<Certificate> certificates = certificateRepository.getAllCertificates(limit, offset);
        //return certificates;
    }

//    @Override
//    public Certificate updateCertificate(long id, MultiValueMap<String, String> params) {
//        Certificate certificate = repository.getById(id);
//
//        if (params.containsKey("name")) certificate.setName(params.getFirst("name"));
//        if (params.containsKey("price")) certificate.setPrice(Integer.parseInt(params.getFirst("price")));
//        if (params.containsKey("duration")) certificate.setDuration(Integer.parseInt(params.getFirst("duration")));
//        //if (params.containsKey("description")) certificate.setDescription(params.get("description")); // todo: fix
//        affixTimestampOnUpdate(certificate);
//
//        return repository.save(certificate);
//    }

//    @Override
//    public Certificate updateCertificate(long id, MultiValueMap<String, String> params) {
//        Certificate certificate = certificateRepository.findById(id).orElse(null);
//        if (certificate != null) {
////            updateCertificateProperties(certificate, params);
//            return certificateRepository.updateCertificate(id, params);
//        } else {
//            return null;
//        }
//    }

    @Override
    public Certificate updateCertificate(long id, MultiValueMap<String, String> params) {
        // yep, it is required to get detached certificate by calling appropriate method
        Certificate certificate = certificateRepository.getCertificateForUpdate(id);
        if (certificate != null) {
            updateCertificateProperties(certificate, params);
            certificate = certificateRepository.updateCertificate(certificate);
        }
        return certificate;
    }

    @Override
    public boolean deleteCertificate(long id) {
        Certificate certificate = certificateRepository.findById(id).orElse(null);
        boolean toBeDeleted = certificate != null;

        if (toBeDeleted) {
            certificateRepository.delete(certificate);
        }

        return toBeDeleted;
    }

    @Override
    public Page<Certificate> searchCertificates(Map<String, String> parameters, int pageNumber, int pageSize) {
        return certificateRepository.searchCertificates(parameters, pageNumber, pageSize);
    }

    @Override
    public Page<Certificate> searchCertificatesByTagNames(List<String> tagNames, int pageNumber, int pageSize) {
        return certificateRepository.searchCertificatesByTagNames(tagNames, pageNumber, pageSize);
    }


    private void affixTimestampsOnCreation(Certificate certificate) {
        Date date = new Date();
        certificate.setCreateDate(date);
        certificate.setLastUpdateDate(date);
    }

    private void affixTimestampOnUpdate(Certificate certificate) {
        if (certificate != null) {
            Date date = new Date();
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
