package com.epam.esm.repository.impl;

import com.epam.esm.controller.api.exception.CertificateNotFoundException;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.epam.esm.repository.CustomCertificateRepository;
import com.epam.esm.repository.JpaCertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.util.QueryParametersSetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.epam.esm.repository.impl.CustomCertificateRepositoryImpl.CertificatesSearchConstants.*;

@Repository
public class CustomCertificateRepositoryImpl implements CustomCertificateRepository {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private JpaCertificateRepository certificateRepository;
    @Autowired
    private TagRepository tagRepository;

    private static final String ID_PARAM = "id";
    private static final String CONTAINS_PARAM = "contains";
    private static final String TAG_PARAM = "tag";

    private static final String GET_CERTIFICATE_BY_ID =
            "SELECT c FROM Certificate c WHERE c.id = :" + ID_PARAM;


    @Override
    public Certificate createCertificate(Certificate certificate) {
        return save(certificate);
    }

    @Override
    public Certificate updateCertificate(Certificate certificate) {
        return save(certificate);
    }

    @Transactional
    private Certificate save(Certificate certificate) {
        List<Tag> description = certificate.getDescription();
        certificate.setDescription(tagRepository.persistTags(description));
        return certificateRepository.save(certificate);
    }

    @Override
    public Certificate getCertificateById(long id) {
        return entityManager.createQuery(GET_CERTIFICATE_BY_ID, Certificate.class)
                .setParameter(ID_PARAM, id)
                .getResultList()
                .stream()
                .findAny()
                // todo: ask if this inappropriate use of map method looks too bad
                .map(this::detach)
                .orElseThrow(() -> new CertificateNotFoundException(id));
    }

    @Override
    @Transactional
    public void deleteCertificateById(long id) {
        Certificate certificate = certificateRepository.findById(id).orElse(null);
        if (certificate == null) {
            throw new CertificateNotFoundException(id);
        }
        certificateRepository.delete(certificate);
    }

    @Override
    @Transactional
    public Page<Certificate> searchCertificates(Map<String, String> parameters, Pageable pageable) {
        String qlString = buildSearchCertificatesQueryString(parameters);
        String countingQlString = buildSearchCertificatesCountingQueryString(parameters);

        TypedQuery<Certificate> query = entityManager.createQuery(qlString, Certificate.class);
        mapSearchCertificatesQueryParameters(query, parameters);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Certificate> resultList = query.getResultList();

        TypedQuery<Long> countingQuery = entityManager.createQuery(countingQlString, Long.class);
        mapSearchCertificatesQueryParameters(countingQuery, parameters);
        long elementsTotal = countingQuery.getSingleResult();

        Page<Certificate> page = new PageImpl<>(resultList, pageable, elementsTotal);
        return page;
    }

    @Override
    @Transactional
    public Page<Certificate> searchCertificatesByTagNames(List<String> tagNames, Pageable pageable) {
        String qlString = buildSearchCertificatesByTagNamesQueryString(tagNames.size());
        TypedQuery<Certificate> query = entityManager.createQuery(qlString, Certificate.class);
        mapSearchCertificatesByTagNamesQueryParameters(query, tagNames);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Certificate> resultList = query.getResultList();

        String countingQlString = buildSearchCertificatesByTagNamesCountingQueryString(tagNames.size());
        TypedQuery<Long> countingQuery = entityManager.createQuery(countingQlString, Long.class);
        mapSearchCertificatesByTagNamesQueryParameters(countingQuery, tagNames);
        long elementsTotal = countingQuery.getSingleResult();

        Page<Certificate> page = new PageImpl<>(resultList, pageable, elementsTotal);
        return page;
    }

    private void mapSearchCertificatesByTagNamesQueryParameters(Query query, List<String> tagNames) {
        for (int i = 0; i < tagNames.size(); i++) {
            query.setParameter(i, tagNames.get(i));
        }
    }

    private void mapSearchCertificatesQueryParameters(Query query, Map<String, String> parameters) {
        QueryParametersSetter.of(query)
                .setIf(parameters.containsKey(TAG_PARAM),
                        TAG_NAME_PARAM, parameters.get(TAG_PARAM))
                .setIf(parameters.containsKey(CONTAINS_PARAM),
                        TAG_NAME_LIKE_PARAM, parameters.get(CONTAINS_PARAM))
                .setIf(parameters.containsKey(CONTAINS_PARAM),
                        CERTIFICATE_NAME_LIKE_PARAM, parameters.get(CONTAINS_PARAM));
    }

    private Certificate detach(Certificate certificate) {
        entityManager.detach(certificate);
        return certificate;
    }

    private String buildSearchCertificatesQueryString(Map<String, String> parameters) {
        return buildSearchCertificatesQueryString(false, parameters);
    }

    private String buildSearchCertificatesCountingQueryString(Map<String, String> parameters) {
        return buildSearchCertificatesQueryString(true, parameters);
    }

    private String buildSearchCertificatesQueryString(boolean countingQuery, Map<String, String> parameters) {
        boolean searchByTagName = parameters.containsKey("tag");
        boolean searchByContent = parameters.containsKey("contains");

        String sortOrderParam = parameters.getOrDefault("sort_by", "");
        String sortByField = defineSortByFieldParam(sortOrderParam);

        String sortOrder = parameters.getOrDefault("order", SortOrder.DEFAULT)
                .toUpperCase();

        StringBuilder sb = new StringBuilder();
        sb.append(countingQuery
                ? COUNT_SEARCH_FOR_CERTIFICATES_RESULTS
                : SEARCH_FOR_CERTIFICATES);

        if (searchByTagName && searchByContent) {
            sb.append(TAG_EQUALS)
                    // fixme
                    .append(AND)
                    .append(TEXT_CONTAINS);
        } else if (searchByContent && !searchByTagName) {
            sb.append(TEXT_CONTAINS);
        } else if (searchByTagName && !searchByContent) {
            sb.append(TAG_EQUALS);
        } else {
            sb.append(WHERE_CLAUSE_DUMMY_VALUE);
        }

        if (!countingQuery) {
            String orderQlSubstring = String.format(ORDER_BY_TEMPLATE, String.format(sortByField, sortOrder));
            sb.append(orderQlSubstring);
        }
        return sb.toString();
    }

    private String defineSortByFieldParam(String sortOrderParam) {
        String sortByField;
        switch (sortOrderParam) {
            case "date":
                sortByField = SortByTemplate.CREATE_DATE;
                break;
            case "updated":
                sortByField = SortByTemplate.UPDATE_DATE;
                break;
            case "name":
                sortByField = SortByTemplate.NAME;
                break;
            default:
                sortByField = SortByTemplate.DEFAULT;
        }
        return sortByField;
    }

    private String buildSearchCertificatesByTagNamesQueryString(int tagsQuantity) {
        return buildSearchCertificatesByTagNamesQueryString(false, tagsQuantity);
    }

    private String buildSearchCertificatesByTagNamesCountingQueryString(int tagsQuantity) {
        return buildSearchCertificatesByTagNamesQueryString(true, tagsQuantity);
    }

    private String buildSearchCertificatesByTagNamesQueryString(boolean countQuery, int tagsQuantity) {
        StringBuilder sb = new StringBuilder();
        sb.append(!countQuery
                ? SEARCH_BY_TAGS_NAMES
                : COUNT_SEARCH_BY_TAGS_NAMES_RESULTS);
        sb.append(tagsQuantity != 0
                ? multiplyAndJoin(SINGLE_TAG_NAME_ENTRY_TEMPLATE, AND, tagsQuantity)
                : WHERE_CLAUSE_DUMMY_VALUE);

        Object[] params = new String[tagsQuantity];
        for (int i = 0; i < params.length; i++) {
            params[i] = "?" + i;
        }
        String queryString = String.format(sb.toString(), params);
        return queryString;
    }


    static class CertificatesSearchConstants {

        static final String SEARCH_FOR_CERTIFICATES =
                "SELECT DISTINCT c FROM Certificate c LEFT JOIN c.description d WHERE ";
        static final String COUNT_SEARCH_FOR_CERTIFICATES_RESULTS =
                "SELECT COUNT(DISTINCT c) FROM Certificate c LEFT JOIN c.description d WHERE";
        static final String SEARCH_BY_TAGS_NAMES =
                "SELECT DISTINCT c FROM Certificate c WHERE ";
        static final String COUNT_SEARCH_BY_TAGS_NAMES_RESULTS =
                "SELECT COUNT(DISTINCT c) FROM Certificate c WHERE ";
        static final String SINGLE_TAG_NAME_ENTRY_TEMPLATE =
                " EXISTS (\n"
                        + "    SELECT 1 \n"
                        + "    FROM c.description d \n"
                        + "    WHERE d.name = %s\n"
                        + ") ";
        static final String TAG_NAME_PARAM = "tag";
        static final String TAG_NAME_LIKE_PARAM = "tagNameLike";
        static final String CERTIFICATE_NAME_LIKE_PARAM = "certNameLike";
        static final String TAG_EQUALS = String.format(" d.name = :%s ", TAG_NAME_PARAM);
        static final String TEXT_CONTAINS =
                String.format(" (d.name LIKE CONCAT('%%', :%s, '%%') "
                        + "OR c.name LIKE CONCAT('%%', :%s, '%%')) ", TAG_NAME_LIKE_PARAM, CERTIFICATE_NAME_LIKE_PARAM);
        static final String ORDER_BY_TEMPLATE = " ORDER BY %s ";

        static final String AND = " AND ";
        static final String WHERE_CLAUSE_DUMMY_VALUE = " 1 = 1 ";

        static class SortByTemplate {

            static final String CREATE_DATE = "c.createDate %s, c.id";
            static final String UPDATE_DATE = "c.lastUpdateDate %s, c.id";
            static final String NAME = "c.name %s, c.id";
            static final String ID = "c.id %s";
            static final String DEFAULT = ID;

        }

        static class SortOrder {
            static final String ASC = " ASC ";
            static final String DESC = " DESC ";
            static final String DEFAULT = ASC;
        }

    }

    private String multiplyAndJoin(String string, String delimiter, int count) {
        return Arrays.stream(new String[count])
                .map(s -> string)
                .collect(Collectors.joining(delimiter));
    }

}
