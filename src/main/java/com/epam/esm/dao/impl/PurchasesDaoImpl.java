package com.epam.esm.dao.impl;

import com.epam.esm.dao.PurchasesDao;
import com.epam.esm.entity.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Component
public class PurchasesDaoImpl implements PurchasesDao {

    private static final String GET_ALL_PURCHASES =
            "SELECT * FROM purchase LIMIT ? OFFSET ?;";

    private static final String GET_PURCHASE_BY_ID =
            "SELECT * FROM purchase WHERE id = ?;";

    private static final String GET_USER_PURCHASES =
            "SELECT * FROM purchase WHERE user_id = ? LIMIT ? OFFSET ?;";

    private static final String CREATE_PURCHASE =
            "SET @userId = ?;\n"
                + "SET @certificateId = ?;\n"
                + "SET @timestamp = ?;\n"
                + "SET @price = (SELECT price FROM gift_certificate WHERE id = @certificateId);\n"
                + "INSERT INTO purchase (user_id, certificate_id, cost, timestamp) \n"
                + "VALUES (@userId, @certificateId, @price, @timestamp);";


    private static final String DELETE_PURCHASE =
            "DELETE FROM purchase WHERE id = ?";

    private static final String GET_LAST_CREATED_ID =
            "SELECT LAST_INSERT_ID();";

    private static final String GET_PRIMARY_TAGS =
            "SELECT * FROM (\n"
                    + "SELECT tag.name, COUNT(tag.name) AS quantity\n"
                    + "    FROM purchase p LEFT JOIN tags_to_certificates ttc ON p.certificate_id = ttc.certificate_id\n"
                    + "        LEFT JOIN tag ON ttc.tag_id = tag.id WHERE p.user_id IN\n"
                    + "        (\n"
                    + "            SELECT t.user_id FROM\n"
                    + "            (\n"
                    + "                SELECT user_id, SUM(cost) AS user_overall_purchases_cost FROM purchase \n"
                    + "                    GROUP BY user_id\n"
                    + "                    ORDER BY user_overall_purchases_cost DESC\n"
                    + "                    -- FETCH FIRST 1 ROW WITH TIES\n"
                    + "                    LIMIT 1\n"
                    + "            ) t\n"
                    + "        )\n"
                    + "    GROUP BY tag.name\n"
                    + "    ORDER BY quantity DESC\n"
                    + "    FETCH FIRST 1 ROW WITH TIES"
                    + ") t2 ORDER BY name LIMIT ? OFFSET ?;";

    private static final String GET_USER_PRIMARY_TAG =
            "SELECT tag.name, COUNT(tag.name) AS quantity\n"
                    + "    FROM purchase p LEFT JOIN tags_to_certificates ttc ON p.certificate_id = ttc.certificate_id\n"
                    + "        LEFT JOIN tag ON ttc.tag_id = tag.id WHERE p.user_id = ?\n"
                    + "    GROUP BY tag.name\n"
                    + "    ORDER BY quantity DESC\n"
                    + "    FETCH FIRST 1 ROW WITH TIES";


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TransactionTemplate transactionTemplate;


    @Override
    public List<Purchase> getUserPurchases(long userId, long limit, long offset) {
        List<Purchase> purchases = jdbcTemplate.query(
                GET_USER_PURCHASES,
                new PurchaseRowMapper(),
                userId, limit, offset);

        return purchases;
    }

    @Override
    public List<Purchase> getAllPurchases(long limit, long offset) {
        List<Purchase> purchases = jdbcTemplate.query(
                GET_ALL_PURCHASES,
                new PurchaseRowMapper(),
                limit, offset);

        return purchases;
    }

    @Override
    public List<String> getUserPrimaryTags(long userId) {
        return jdbcTemplate.query(GET_USER_PRIMARY_TAG, new PrimaryPurchaseTagRowMapper(), userId);
    }

    @Override
    public List<String> getPrimaryTags(long limit, long offset) {
        return jdbcTemplate.query(GET_PRIMARY_TAGS, new PrimaryPurchaseTagRowMapper(), limit, offset);
    }

    @Override
    public Purchase purchaseCertificate(long userId, long certificateId, Date timestamp) {
        return transactionTemplate.execute(status -> {
            jdbcTemplate.update(CREATE_PURCHASE,
                    userId,
                    certificateId,
                    timestamp
            );

            long purchaseId = jdbcTemplate.queryForObject(GET_LAST_CREATED_ID, Long.class);
            return getPurchaseById(purchaseId);
        });
    }


    @Override
    public Purchase getPurchaseById(long id) {

        try {
            Purchase purchase = jdbcTemplate.queryForObject(
                    GET_PURCHASE_BY_ID,
                    new PurchaseRowMapper(),
                    id
            );

            return purchase;

        } catch (IncorrectResultSizeDataAccessException ex) {
            return null;
        }
    }

    @Override
    public boolean deletePurchase(long id) {
        return jdbcTemplate.update(DELETE_PURCHASE, id) == 1;
    }

    private static class PurchaseRowMapper implements RowMapper<Purchase> {

        @Override
        public Purchase mapRow(ResultSet rs, int rowNum) throws SQLException {
            Purchase purchase = new Purchase();
            purchase.setId(rs.getLong("id"));
            purchase.setTimestamp(rs.getTimestamp("timestamp"));
            purchase.setCost(rs.getInt("cost"));
            purchase.setCertificateId(rs.getLong("certificate_id"));
            purchase.setUserId(rs.getLong("user_id"));

            return purchase;
        }
    }

    private static class PrimaryPurchaseTagRowMapper implements RowMapper<String> {

        @Override
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("name");
        }
    }

}
