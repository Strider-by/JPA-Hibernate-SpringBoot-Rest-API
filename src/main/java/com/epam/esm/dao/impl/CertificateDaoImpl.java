package com.epam.esm.dao.impl;

import com.epam.esm.dao.CertificateDao;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.model.dto.CertificateUpdateDto;
import com.epam.esm.model.dto.Pair;
import com.epam.esm.model.dto.TagDownstreamDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CertificateDaoImpl implements CertificateDao {

    private static final String CHECK_IF_CERTIFICATE_EXISTS =
            "SELECT (SELECT COUNT(id) FROM gift_certificate WHERE id = ?) > 0";

    private static final String GET_CERTIFICATE =
            "SELECT * FROM gift_certificate WHERE id = ?";

    private static final String GET_TAGS_BOUND_TO_CERTIFICATE =
            "SELECT tag.id AS id, tag.name AS name FROM tags_to_certificates ttc LEFT JOIN tag "
                    + "ON ttc.tag_id = tag.id "
                    + "WHERE certificate_id = ?";

    private static final String GET_ALL_TAGS_TO_CERTIFICATES_BOUNDS =
            "SELECT DISTINCT ttc.certificate_id, tag.id AS tag_id, tag.name AS tag_name "
                    + "FROM tags_to_certificates ttc LEFT JOIN tag "
                    + "ON tag.id = ttc.tag_id; ";

    private static final String GET_ALL_TAGS_TO_SELECTED_CERTIFICATES_BOUNDS_TEMPLATE_MASK =
            "SELECT DISTINCT ttc.certificate_id, tag.id AS tag_id, tag.name AS tag_name "
                    + "FROM tags_to_certificates ttc LEFT JOIN tag "
                    + "ON tag.id = ttc.tag_id "
                    + "WHERE ttc.certificate_id IN (%s);";

    private static final String GET_ALL_CERTIFICATES =
            "SELECT * FROM gift_certificate "
                    + "LIMIT ? OFFSET ?";

    private static final String SEARCH_FOR_CERTIFICATES =
            "SELECT cert.id, cert.name, cert.price, cert.duration, cert.create_date, cert.last_update_date, tag.name AS tag_name  \n"
                    + "FROM gift_certificate cert \n"
                    + "LEFT JOIN tags_to_certificates ttc ON cert.id = ttc.certificate_id \n"
                    + "LEFT JOIN tag ON tag.id = ttc.tag_id \n"
                    + "WHERE cert.id IN \n"
                    + "(SELECT cert.id FROM gift_certificate cert \n"
                    + "LEFT JOIN tags_to_certificates ttc ON cert.id = ttc.certificate_id \n"
                    + "LEFT JOIN tag ON ttc.tag_id = tag.id \n"
                    + "WHERE ";

//    private static final String SEARCH_FOR_CERTIFICATES_BY_TAGS =
//            "SELECT cert.id, cert.name, cert.price, cert.duration, cert.create_date, cert.last_update_date \n"
//                    + "FROM gift_certificate cert \n"
//                    + "LEFT JOIN tags_to_certificates ttc ON cert.id = ttc.certificate_id \n"
//                    + "LEFT JOIN tag ON tag.id = ttc.tag_id \n"
//                    + "WHERE ";

    private static final String SEARCH_FOR_CERTIFICATES_BY_TAGS_TEMPLATE_MASK =
            "SELECT cert.id, cert.name, cert.price, cert.duration, cert.create_date, cert.last_update_date\n"
                    + "   FROM gift_certificate cert\n"
                    + "   WHERE cert.id IN\n"
                    + "   (\n"
                    + "        SELECT ttc.certificate_id FROM  tags_to_certificates ttc JOIN tag ON ttc.tag_id = tag.id\n"
                    + "            WHERE %s\n"
                    + "            GROUP BY ttc.certificate_id\n"
                    + "            HAVING COUNT(ttc.certificate_id) > ? - 1\n"
                    + "    )"
                    + "LIMIT ? OFFSET ?;";

    private static final String CREATE_CERTIFICATE =
            "INSERT INTO gift_certificate (name, price, duration, create_date, last_update_date) "
                    + "VALUES (?, ?, ?, ?, ?);";

    private static final String GET_LAST_CREATED_ID =
            "SELECT LAST_INSERT_ID();";

    private static final String CREATE_TAGS_AND_BOUND_THEM_TO_CERTIFICATE_TEMPLATE_MASK =
            "INSERT IGNORE INTO tag (name) VALUES %s; \n"
                    + "INSERT INTO tags_to_certificates (certificate_id, tag_id) "
                    + "SELECT ?, tag.id FROM tag WHERE tag.name IN (%s);";

    private static final String UPDATE_CERTIFICATE_TEMPLATE_MASK =
            "UPDATE gift_certificate SET %s WHERE id = ?";

    private static final String DELETE_TAGS_TO_CERTIFICATE_BOUNDS =
            "DELETE FROM tags_to_certificates WHERE certificate_id = ?; ";

    private static final String DELETE_CERTIFICATE =
            "DELETE FROM gift_certificate WHERE id = ?";

    private static final String  TAG_NAME_EQUALS =
            " tag.name = ? ";

    private static final String UPDATE_FIELD_VALUE_TEMPLATE_MASK = "%s = ?";
    private static final String VALUE_MASK_IN_BRACKETS = "(?)";
    private static final String SIMPLE_VALUE_MASK = "?";
    private static final String COMMA_DELIMITER = ", ";
    private static final String WHERE_CLAUSE_DUMMY_VALUE = " 1 = 1 ";
    private static final String AND_CONJUNCTION = " AND ";
    private static final String OR_CONJUNCTION = " OR ";
    private static final String LIMIT_AND_OFFSET = " LIMIT ? OFFSET ? ";

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TransactionTemplate transactionTemplate;

    public CertificateDaoImpl() {
    }

    public CertificateDaoImpl(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public Certificate createCertificate(CertificateCreateDto dto, Date createdAt) {

        return transactionTemplate.execute(status -> {

            jdbcTemplate.update(CREATE_CERTIFICATE,
                    dto.getName(),
                    dto.getPrice(),
                    dto.getDuration(),
                    createdAt,
                    createdAt
            );

            long certificateId = jdbcTemplate.queryForObject(GET_LAST_CREATED_ID, Long.class);

            List<TagDownstreamDto> description = dto.getDescription();
            if (description != null && !description.isEmpty()) {
                List<String> tagsNames = description.stream()
                        .map(TagDownstreamDto::getName)
                        .collect(Collectors.toList());
                createAndBindTags(certificateId, tagsNames);
            }

            return getCertificateById(certificateId);
        });
    }

    @Override
    public Certificate getCertificateById(long id) {

        try {
            Certificate certificate = jdbcTemplate.queryForObject(
                    GET_CERTIFICATE,
                    new CertificateRowMapper(),
                    id
            );
            List<Tag> tags = getCertificateTags(id);
            certificate.setDescription(tags);
            return certificate;

        } catch (IncorrectResultSizeDataAccessException ex) {
            return null;
        }
    }

    private boolean checkIfCertificateExists(long id) {
        return jdbcTemplate.queryForObject(CHECK_IF_CERTIFICATE_EXISTS, Boolean.class, id);
    }

    private List<Tag> getCertificateTags(long certificateId) {
        return jdbcTemplate.query(GET_TAGS_BOUND_TO_CERTIFICATE, new TagRowMapper(), certificateId);
    }

    @Override
    public List<Certificate> getAllCertificates(long limit, long offset) {

        return transactionTemplate.execute(status -> {
            TreeMap<Long, Certificate> certificates =
                    jdbcTemplate.query(GET_ALL_CERTIFICATES, new CertificateRowMapper(), limit, offset).stream()
                    .collect(Collectors.toMap(certificate -> certificate.getId(), Function.identity(), (o1, o2) -> o1, TreeMap::new));

            if (certificates.isEmpty()) {
                return Collections.emptyList();
            }

            Object[] certificatesIds = certificates.keySet().toArray();
            String tagsQuery = buildSearchBoundTagsQuery(certificatesIds.length);

            Map<Long, List<Tag>> tagsToCertificatesBounds =
                    jdbcTemplate.query(tagsQuery, new TagToCertificateRowMapper(), certificatesIds).stream()
                            .collect(Collectors.groupingBy(Pair::getFirstValue,
                                    Collectors.mapping(Pair::getSecondValue, Collectors.toList())));

            for (Map.Entry<Long, List<Tag>> entry : tagsToCertificatesBounds.entrySet()) {
                long certificateId = entry.getKey();
                certificates.get(certificateId).setDescription(entry.getValue());
            }

            return Stream.of(certificates)
                    .map(Map::values)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        });

    }

    @Override
    public Certificate update(CertificateUpdateDto dto) {

        return transactionTemplate.execute(status -> {
            if (!checkIfCertificateExists(dto.getId())) {
                return null;
            }

            String name;
            Integer price;
            Integer duration;
            Date lastUpdate;
            List<String> parametersToChange = new ArrayList<>();
            if (Objects.nonNull(name = dto.getName())) parametersToChange.add("name");
            if (Objects.nonNull(price = dto.getPrice())) parametersToChange.add("price");
            if (Objects.nonNull(duration = dto.getDuration())) parametersToChange.add("duration");
            parametersToChange.add("last_update_date");
            lastUpdate = dto.getLastUpdate();
            String queryString = buildUpdateCertificateQuery(parametersToChange.toArray(new String[0]));
            Object[] params = Stream.of(name, price, duration, lastUpdate, dto.getId())
                    .filter(Objects::nonNull)
                    .toArray();

            jdbcTemplate.update(queryString, params);
            replaceTagsOnCertificateUpdate(dto);
            return getCertificateById(dto.getId());
        });
    }

    @Override
    public boolean delete(long id) {
        return jdbcTemplate.update(DELETE_CERTIFICATE, id) == 1;
    }

    @Override
    public List<Certificate> searchCertificates(long limit, long offset, Map<String, String> parameters) {
        String query = buildSearchCertificatesQuery(parameters);

        String textContains = parameters.getOrDefault("contains", "");
        String tagName = parameters.get("tag");
        Object[] paramsToBePassed = Stream.of(tagName, textContains, textContains, limit, offset)
                .filter(Objects::nonNull)
                .toArray();

        return transactionTemplate.execute(status -> {
            TreeMap<Long, Certificate> certificates = jdbcTemplate.query(query, new CertificateRowMapper(), paramsToBePassed).stream()
                    .collect(Collectors.toMap(certificate -> certificate.getId(), Function.identity(), (o1, o2) -> o1, TreeMap::new));

            if (certificates.isEmpty()) {
                return Collections.emptyList();
            }

            Object[] certificatesIds = certificates.keySet().toArray();
            String tagsQuery = buildSearchBoundTagsQuery(certificatesIds.length);
            List<Pair<Long, Tag>> queryResult = jdbcTemplate.query(tagsQuery, new TagToCertificateRowMapper(), certificatesIds);
            Map<Long, List<Tag>> tagsToCertificatesBounds = queryResult.stream()
                    .collect(Collectors.groupingBy(Pair::getFirstValue,
                            Collectors.mapping(Pair::getSecondValue, Collectors.toList())));


            for (Map.Entry<Long, List<Tag>> entry : tagsToCertificatesBounds.entrySet()) {
                long certificateId = entry.getKey();
                Certificate certificate = certificates.get(certificateId);
                if (certificate != null) {
                    certificate.setDescription(entry.getValue());
                }
            }

            return Stream.of(certificates)
                    .map(Map::values)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        });
    }

    @Override
    public List<Certificate> searchCertificatesByTagNames(List<String> tagNames, long limit, long offset) {
        String query = buildSearchCertificatesByTagNamesQuery(tagNames.size());

        Object[] paramsToBePassed = Stream.concat(
                tagNames.stream(),
                Stream.of(tagNames.size(), limit, offset))
                .toArray();

        return transactionTemplate.execute(status -> {
            TreeMap<Long, Certificate> certificates = jdbcTemplate.query(query, new CertificateRowMapper(), paramsToBePassed).stream()
                    .collect(Collectors.toMap(certificate -> certificate.getId(), Function.identity(), (o1, o2) -> o1, TreeMap::new));

            if (certificates.isEmpty()) {
                return Collections.emptyList();
            }

            Object[] certificatesIds = certificates.keySet().toArray();

            String tagsQuery = buildSearchBoundTagsQuery(certificatesIds.length);
            List<Pair<Long, Tag>> queryResult = jdbcTemplate.query(tagsQuery, new TagToCertificateRowMapper(), certificatesIds);
            Map<Long, List<Tag>> tagsToCertificatesBounds = queryResult.stream()
                    .collect(Collectors.groupingBy(Pair::getFirstValue,
                            Collectors.mapping(Pair::getSecondValue, Collectors.toList())));

            for (Map.Entry<Long, List<Tag>> entry : tagsToCertificatesBounds.entrySet()) {
                long certificateId = entry.getKey();
                Certificate certificate = certificates.get(certificateId);
                if (certificate != null) {
                    certificate.setDescription(entry.getValue());
                }
            }

            return Stream.of(certificates)
                    .map(Map::values)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        });

    }

    private void replaceTagsOnCertificateUpdate(CertificateUpdateDto dto) {
        List<String> tags = dto.getDescription();
        if (Objects.nonNull(tags)) {
            jdbcTemplate.update(DELETE_TAGS_TO_CERTIFICATE_BOUNDS, dto.getId());
            createAndBindTags(dto.getId(), tags);
        }
    }

    private void createAndBindTags(long certificateId, List<String> tagsNames) {
        if (!tagsNames.isEmpty()) {
            String createAndBoundTagsQuery = buildCreateTagsQuery(tagsNames.size());
            Object[] arguments = Stream.of(tagsNames.toArray(), new Object[]{certificateId}, tagsNames.toArray())
                    .flatMap(Arrays::stream)
                    .toArray();
            jdbcTemplate.update(createAndBoundTagsQuery, arguments);
        }
    }

    private String buildUpdateCertificateQuery(String... fields) {
        String fieldsValuesPartTemplate = multiplyAndJoin(UPDATE_FIELD_VALUE_TEMPLATE_MASK, COMMA_DELIMITER, fields.length);
        String fieldsValuesPart = String.format(fieldsValuesPartTemplate, (Object[]) fields);
        String queryString = String.format(UPDATE_CERTIFICATE_TEMPLATE_MASK, fieldsValuesPart);
        return queryString;
    }

    private String buildCreateTagsQuery(int tagsToCreate) {
        String valuePlaceholders1 = multiplyAndJoin(VALUE_MASK_IN_BRACKETS, COMMA_DELIMITER, tagsToCreate);
        String valuePlaceholders2 = multiplyAndJoin(SIMPLE_VALUE_MASK, COMMA_DELIMITER, tagsToCreate);
        return String.format(CREATE_TAGS_AND_BOUND_THEM_TO_CERTIFICATE_TEMPLATE_MASK, valuePlaceholders1, valuePlaceholders2);
    }

    private String buildSearchCertificatesQuery(Map<String, String> parameters) {

        boolean searchByTagName = parameters.containsKey("tag");
        String sortOrderParam = parameters.get("sort_by");
        String sortByField;
        if (sortOrderParam == null) {
            sortByField = CertificatesSearchConstants.DEFAULT_ORDER_BY_VALUE;
        } else {
            sortByField = sortOrderParam.equals("date")
                    ? "create_date"
                    : sortOrderParam.equals("name")
                        ? "name"
                        : "id";
        }
        String sortOrder = parameters.getOrDefault("order", CertificatesSearchConstants.SORT_ORDER_ASC)
                .toUpperCase();

        StringBuilder sb = new StringBuilder();
        sb.append(SEARCH_FOR_CERTIFICATES);

        if (searchByTagName) {
            sb.append(CertificatesSearchConstants.TAG_EQUALS)
                    .append(CertificatesSearchConstants.AND)
                    .append(CertificatesSearchConstants.TEXT_CONTAINS);
        } else {
            sb.append(CertificatesSearchConstants.TEXT_CONTAINS);
        }

        sb.append(String.format(CertificatesSearchConstants.ORDER_BY_TEMPLATE, sortByField, sortOrder));
        sb.append(LIMIT_AND_OFFSET);
        return sb.toString();
    }

    private String buildSearchCertificatesByTagNamesQuery(int tags) {
        String whereMaskFiller;
        if (tags == 0) {
            whereMaskFiller = WHERE_CLAUSE_DUMMY_VALUE;
        } else {
            whereMaskFiller = multiplyAndJoin(TAG_NAME_EQUALS, OR_CONJUNCTION, tags);
        }

        return String.format(SEARCH_FOR_CERTIFICATES_BY_TAGS_TEMPLATE_MASK, whereMaskFiller);
    }

    private String buildSearchBoundTagsQuery(int certificatesQuantity) {
        String inPart = multiplyAndJoin(SIMPLE_VALUE_MASK, COMMA_DELIMITER, certificatesQuantity);
        String query = String.format(GET_ALL_TAGS_TO_SELECTED_CERTIFICATES_BOUNDS_TEMPLATE_MASK, inPart);
        return query;
    }

    private String multiplyAndJoin(String string, String delimiter, int count) {
        return Arrays.stream(new String[count])
                .map(s -> string)
                .collect(Collectors.joining(delimiter));
    }

    private static class CertificateRowMapper implements RowMapper<Certificate> {

        @Override
        public Certificate mapRow(ResultSet rs, int rowNum) throws SQLException {
            Certificate certificate = new Certificate();
            certificate.setId(rs.getLong("id"));
            certificate.setName(rs.getString("name"));
            certificate.setPrice(rs.getInt("price"));
            certificate.setDuration(rs.getInt("duration"));
            certificate.setCreateDate(rs.getTimestamp("create_date"));
            certificate.setLastUpdateDate(rs.getTimestamp("last_update_date"));

            return certificate;
        }

    }

    private static class TagRowMapper implements RowMapper<Tag> {

        @Override
        public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
            Tag tag = new Tag();
            tag.setId(rs.getLong("id"));
            tag.setName(rs.getString("name"));

            return tag;
        }

    }

    private static class TagToCertificateRowMapper implements RowMapper<Pair<Long, Tag>> {

        @Override
        public Pair<Long, Tag> mapRow(ResultSet rs, int rowNum) throws SQLException {
            Pair<Long, Tag> pair = new Pair<>();
            pair.setFirstValue(rs.getLong("certificate_id"));
            Tag tag = new Tag();
            tag.setId(rs.getLong("tag_id"));
            tag.setName(rs.getString("tag_name"));
            pair.setSecondValue(tag);

            return pair;
        }

    }

    private class CertificatesSearchConstants {

        private static final String AND = " AND ";
        private static final String TAG_EQUALS = " tag.name = ? ";
        private static final String TEXT_CONTAINS =
                " (tag.name LIKE CONCAT('%', ?, '%') "
                        + "OR cert.name LIKE CONCAT('%', ?, '%'))) ";
        private static final String ORDER_BY_TEMPLATE = " ORDER BY %s %s";
        private static final String DEFAULT_ORDER_BY_VALUE = "cert.id";
        private static final String SORT_ORDER_ASC = " ASC ";
        private static final String SORT_ORDER_DESC = " DESC ";
        private static final String LIMIT_OFFSET = " LIMIT ? OFFSET ? ";

    }

}
