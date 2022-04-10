package com.epam.esm.repository.impl;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.epam.esm.model.util.DtoConverter;
import com.epam.esm.repository.JpaCertificateRepository;
import com.epam.esm.repository.CustomCertificateRepository;
import com.epam.esm.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CustomCertificateRepositoryImpl implements CustomCertificateRepository {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private JpaCertificateRepository certificateRepository;
    @Autowired
    private TagRepository tagRepository;

    @Override
    public List<Certificate> getAllCertificates(long limit, long offset) {
        // to make this work, name table with the name of the Entity (with capital 'C')
        return entityManager.createQuery("SELECT c FROM Certificate c", Certificate.class)
                .getResultList();
    }

    @Override
    public Certificate createCertificate(Certificate certificate) {
        List<Tag> description = certificate.getDescription();
        certificate.setDescription(tagRepository.persistTags(description));
        return certificateRepository.save(certificate);
    }

//    @Override
//    public Certificate getCertificateForUpdate(long id) {
//        return certificateRepository.findById(id)
//                .map(certificate -> {
//
//                })
//                .ifPresent(entityManager::detach)
//    }

    @Override
    public Certificate updateCertificate(long id, MultiValueMap<String, String> params) {
        Certificate certificate = certificateRepository.findById(id).orElse(null);
        if (certificate == null) {
            return null;
        }

        updateCertificateProperties(params, certificate);
        return certificateRepository.save(certificate);
        //return certificateRepository.getById(id);
    }

    private void updateCertificateProperties(MultiValueMap<String, String> params, Certificate certificate) {
        if (params.containsKey("name")) certificate.setName(params.getFirst("name"));
        if (params.containsKey("price")) certificate.setPrice(Integer.parseInt(params.getFirst("price")));
        if (params.containsKey("duration")) certificate.setDuration(Integer.parseInt(params.getFirst("duration")));

        if (params.containsKey("description")) {
            List<Tag> description = params.get("description").stream()
                    .map(DtoConverter::toTag)
                    .collect(Collectors.toList());

            tagRepository.persistTags(description);
            certificate.setDescription(description);
        }
    }

}
