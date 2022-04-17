package com.epam.esm.repository.impl;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.epam.esm.repository.JpaCertificateRepository;
import com.epam.esm.repository.CustomCertificateRepository;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.repository.util.QueryParametersSetter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;
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

    private static final String GET_CERTIFICATE_BY_ID =
            "SELECT c FROM Certificate c WHERE c.id = :" + ID_PARAM;


    @Override
    public List<Certificate> getAllCertificates(long limit, long offset) {
        // to make this work, name table with the name of the Entity (with capital 'C')
        return entityManager.createQuery("SELECT c FROM Certificate c", Certificate.class)
                .getResultList();
    }

    @Override
    @Transactional
    public Certificate createCertificate(Certificate certificate) {
        List<Tag> description = certificate.getDescription();
        certificate.setDescription(tagRepository.persistTags(description));
        return certificateRepository.save(certificate);
    }

    @Override
    public Certificate getCertificateForUpdate(long id) {
        return entityManager.createQuery(GET_CERTIFICATE_BY_ID, Certificate.class)
                .setParameter(ID_PARAM, id)
                .getResultList()
                .stream()
                .findAny()
                // todo: ask if this inappropriate use of map method looks too bad
                .map(this::detach)
                .orElse(null);
    }

    @Override
    public List<Certificate> searchCertificates(Map<String, String> parameters, long limit, long offset) {
        String qlString = buildSearchCertificatesQueryString(parameters);
        String countingQlString = buildSearchCertificatesCountingQueryString(parameters);
        // fixme
        System.err.println(qlString);

        TypedQuery<Certificate> query = entityManager.createQuery(qlString, Certificate.class);
        mapSearchCertificatesQueryParameters(query, parameters);
        TypedQuery<Long> countingQuery = entityManager.createQuery(countingQlString, Long.class);
        mapSearchCertificatesQueryParameters(countingQuery, parameters);

        Long totalQuantity = countingQuery.getSingleResult();

        query.setMaxResults((int) limit);
        // todo: ask WHY int not long
        // todo: make limit int
        query.setFirstResult((int) offset);
        List<Certificate> resultList = query.getResultList();

        new PageImpl<>(resultList, Pageable.ofSize((int) limit), totalQuantity);

        return resultList;
    }

    @Override
    public List<Certificate> searchCertificatesByTagNames(List<String> tagNames, long limit, long offset) {
        String qlString = buildSearchCertificatesByTagNamesQueryString(tagNames.size());
        System.err.println(qlString);
        TypedQuery<Certificate> query = entityManager.createQuery(qlString, Certificate.class);
        mapSearchCertificatesByTagNamesQueryParameters(query, tagNames);
        query.setFirstResult((int) offset);
        query.setMaxResults((int) limit);
        List<Certificate> resultList = query.getResultList();

        String countingQlString = buildSearchCertificatesByTagNamesCountingQueryString(tagNames.size());
        TypedQuery<Long> countingQuery = entityManager.createQuery(countingQlString, Long.class);
        mapSearchCertificatesByTagNamesQueryParameters(countingQuery, tagNames);
        Long elementsTotal = countingQuery.getSingleResult();

//        System.err.printf("elements: %d %n %s", elementsTotal, resultList);
        return resultList;
    }

    private void mapSearchCertificatesByTagNamesQueryParameters(Query query, List<String> tagNames) {
        for (int i = 0; i < tagNames.size(); i++) {
            query.setParameter(i, tagNames.get(i));
        }
    }

    private void mapSearchCertificatesQueryParameters(Query query, Map<String, String> parameters) {
        QueryParametersSetter.of(query)
                .setIf(parameters.containsKey("tag"),
                        TAG_NAME_PARAM, parameters.get("tag"))
                .setIf(parameters.containsKey("contains"),
                        TAG_NAME_LIKE_PARAM, parameters.get("contains"))
                .setIf(parameters.containsKey("contains"),
                        CERTIFICATE_NAME_LIKE_PARAM, parameters.get("contains"));
    }

//    @Override
//    public Certificate updateCertificate(Certificate certificate) {
//        if (certificate == null) {
//            return null;
//        }
//        return certificateRepository.save(certificate);
//        //return certificateRepository.getById(id);
//    }

//    private void updateCertificateProperties(MultiValueMap<String, String> params, Certificate certificate) {
//        if (params.containsKey("name")) certificate.setName(params.getFirst("name"));
//        if (params.containsKey("price")) certificate.setPrice(Integer.parseInt(params.getFirst("price")));
//        if (params.containsKey("duration")) certificate.setDuration(Integer.parseInt(params.getFirst("duration")));
//
//        if (params.containsKey("description")) {
//            List<Tag> description = params.get("description").stream()
//                    .map(DtoConverter::toTag)
//                    .collect(Collectors.toList());
//
//            tagRepository.persistTags(description);
//            certificate.setDescription(description);
//        }
//    }

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
        // todo: make separate methods for defining parameters values
        boolean searchByTagName = parameters.containsKey("tag");
        String sortOrderParam = parameters.getOrDefault("sort_by", "");
        String sortByField;

        switch (sortOrderParam) {
            case "date":
                sortByField = SortBy.CREATE_DATE;
                break;
            case "name":
                sortByField = SortBy.NAME;
                break;
            default:
                sortByField = SortBy.DEFAULT;
        }

        String sortOrder = parameters.getOrDefault("order", SortOrder.DEFAULT)
                .toUpperCase();

        StringBuilder sb = new StringBuilder();
        sb.append(countingQuery
                ? COUNT_SEARCH_FOR_CERTIFICATES_RESULTS
                : SEARCH_FOR_CERTIFICATES);

        if (searchByTagName) {
            sb.append(TAG_EQUALS)
                    // fixme
                    .append(AND)
                    .append(TEXT_CONTAINS);
        } else {
            sb.append(TEXT_CONTAINS);
        }

        sb.append(String.format(ORDER_BY_TEMPLATE, sortByField, sortOrder));
//        sb.append(LIMIT_AND_OFFSET);
        return sb.toString();

    }

    private String buildSearchCertificatesByTagNamesQueryString(int tagsQuantity) {
        return buildSearchCertificatesByTagNamesQueryString(false, tagsQuantity);
    }

    private String buildSearchCertificatesByTagNamesCountingQueryString(int tagsQuantity) {
        return buildSearchCertificatesByTagNamesQueryString(true, tagsQuantity);
    }

    private String buildSearchCertificatesByTagNamesQueryString(boolean countQuery, int tagsQuantity) {
        StringBuilder sb = new StringBuilder();
        sb.append(
                !countQuery
                ? SEARCH_BY_TAGS_NAMES
                : COUNT_SEARCH_BY_TAGS_NAMES_RESULTS
        );
        sb.append(
                tagsQuantity != 0
                ? multiplyAndJoin(SINGLE_TAG_NAME_ENTRY_TEMPLATE, AND, tagsQuantity)
                : WHERE_CLAUSE_DUMMY_VALUE
        );
        String[] params = new String[tagsQuantity];
        for (int i = 0; i < params.length; i++) {
            params[i] = "?" + i;
        }
        String queryString = String.format(sb.toString(), params);
        return queryString;
    }

//    private String buildSearchCertificatesCountingQuery(Map<String, String> parameters) {
//        boolean searchByTagName = parameters.containsKey("tag");
//        String sortOrderParam = parameters.getOrDefault("sort_by", "");
//        String sortByField;
//
//        switch (sortOrderParam) {
//            case "date":
//                sortByField = "c.createDate";
//                break;
//            case "name":
//                sortByField = "c.name";
//                break;
//            default:
//                sortByField = DEFAULT_ORDER_BY_VALUE;
//        }
//
//        String sortOrder = parameters.getOrDefault("order", SORT_ORDER_ASC)
//                .toUpperCase();
//
//        StringBuilder sb = new StringBuilder();
//        sb.append(COUNT_SEARCH_RESULTS);
//
//        if (searchByTagName) {
//            sb.append(TAG_EQUALS)
//                    .append(AND)
//                    .append(TEXT_CONTAINS);
//        } else {
//            sb.append(TEXT_CONTAINS);
//        }
//
//        sb.append(String.format(ORDER_BY_TEMPLATE, sortByField, sortOrder));
////        sb.append(LIMIT_AND_OFFSET);
//        return sb.toString();
//    }

//    private static class QueryParametersSetter {
//
//        private Query query;
//
//        private QueryParametersSetter(Query query) {
//            this.query = query;
//        }
//
//        public static QueryParametersSetter of(Query query) {
//            QueryParametersSetter qps = new QueryParametersSetter(query);
//            return qps;
//        }
//
//        public QueryParametersSetter set(String paramName, Object paramValue) {
//            query.setParameter(paramName, paramValue);
//            return this;
//        }
//
//        public QueryParametersSetter setIf(boolean condition, String paramName, Object paramValue) {
//            if (condition) {
//                query.setParameter(paramName, paramValue);
//            }
//            return this;
//        }
//
//        public QueryParametersSetter set(int paramPosition, Object paramValue) {
//            query.setParameter(paramPosition, paramValue);
//            return this;
//        }
//
//        public QueryParametersSetter setIf(boolean condition, int paramPosition, Object paramValue) {
//            if (condition) {
//                query.setParameter(paramPosition, paramValue);
//            }
//            return this;
//        }
//    }


    static class CertificatesSearchConstants {

        static final String SEARCH_FOR_CERTIFICATES =
                "SELECT DISTINCT c FROM Certificate c JOIN c.description d WHERE ";
        static final String COUNT_SEARCH_FOR_CERTIFICATES_RESULTS =
                "SELECT COUNT(DISTINCT c) FROM Certificate c JOIN c.description d WHERE";
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
        static final String ORDER_BY_TEMPLATE = " ORDER BY %s %s";

        static final String AND = " AND ";
        static final String WHERE_CLAUSE_DUMMY_VALUE = " 1 = 1 ";

        static class SortBy {
            static final String CREATE_DATE = "c.createDate, c.id";
            static final String NAME = "c.name, c.id";
            static final String DEFAULT = "c.id";
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

//    private class GeneralPurposeSqlConstants {
//
//        private static final String UPDATE_FIELD_VALUE_TEMPLATE_MASK = "%s = ?";
//        private static final String VALUE_MASK_IN_BRACKETS = "(?)";
//        private static final String SIMPLE_VALUE_MASK = "?";
//        private static final String COMMA_DELIMITER = ", ";
//        private static final String WHERE_CLAUSE_DUMMY_VALUE = " 1 = 1 ";
//        private static final String AND_CONJUNCTION = " AND ";
//        private static final String OR_CONJUNCTION = " OR ";
//        private static final String LIMIT_AND_OFFSET = " LIMIT ? OFFSET ? ";
//
//    }

}
