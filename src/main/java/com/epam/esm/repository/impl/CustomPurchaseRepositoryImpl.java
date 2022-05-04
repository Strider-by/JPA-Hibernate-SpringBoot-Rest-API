package com.epam.esm.repository.impl;

import com.epam.esm.controller.api.ControllerHelper;
import com.epam.esm.model.Purchase;
import com.epam.esm.model.Tag;
import com.epam.esm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

@Repository
public class CustomPurchaseRepositoryImpl implements CustomPurchaseRepository {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private JpaPurchaseRepository purchaseRepository;

    private static final String USER_ID_PARAM = "userId";


    private static final String COUNT_MOST_WIDELY_USED_IN_PURCHASES_TAG_USAGE =
            "SELECT COUNT(d) AS count_ FROM Purchase p JOIN p.certificate c JOIN c.description d GROUP BY d.id ORDER BY count_ DESC";

    private static final String COUNT_MOST_WIDELY_USED_IN_PURCHASES_TAG_USAGE_FOR_SPECIFIC_USER =
            "SELECT COUNT(d) AS count_ FROM Purchase p JOIN p.certificate c JOIN c.description d "
                    + "WHERE p.user.id = :" + USER_ID_PARAM + " "
                    + "GROUP BY d.id ORDER BY count_ DESC";

    private static final String QUANTITY_PARAM = "quantity";

    private static final String GET_MOST_WIDELY_USED_IN_PURCHASES_TAGS =
            "SELECT DISTINCT d FROM Purchase p JOIN p.certificate c JOIN c.description d GROUP BY d.id "
                    + "HAVING COUNT(d.id) = :"
                    + QUANTITY_PARAM
                    + " ORDER BY d.id";

    private static final String GET_MOST_WIDELY_USED_IN_PURCHASES_TAGS_FOR_SPECIFIC_USER =
            "SELECT DISTINCT d FROM Purchase p JOIN p.certificate c JOIN c.description d "
//                    + "WHERE p.user.id = :"
//                    + USER_ID_PARAM
                    + " GROUP BY d.id "
                    + " HAVING COUNT(d.id) = :"
                    + QUANTITY_PARAM
                    + " ORDER BY d.id";

//    private static final String COUNT_MOST_WIDELY_USED_IN_PURCHASES_TAGS___OLD =
//            "SELECT COUNT(DISTINCT d) FROM Purchase p JOIN p.certificate c JOIN c.description d GROUP BY d.id "
//                    + "HAVING COUNT(d.id) = :"
//                    + QUANTITY_PARAM;

    private static final String COUNT_MOST_WIDELY_USED_IN_PURCHASES_TAGS =
            "SELECT 1 AS count_ FROM Purchase p "
                    + "JOIN p.certificate c JOIN c.description d "
                    + "GROUP BY d.id "
                    + "HAVING COUNT(d.id) = :"
                    + QUANTITY_PARAM;

    private static final String COUNT_MOST_WIDELY_USED_IN_PURCHASES_TAGS_FOR_SPECIFIC_USER =
            "SELECT 1 AS count_ FROM Purchase p "
                    + "JOIN p.certificate c JOIN c.description d "
                    + "WHERE p.user.id = :"
                    + USER_ID_PARAM + " "
                    + "GROUP BY d.id "
                    + "HAVING COUNT(d.id) = :"
                    + QUANTITY_PARAM;

//        private static final String COUNT_MOST_WIDELY_USED_TAG_USAGE =
//                "SELECT COUNT(t) FROM Purchase p JOIN Tag t"
//                        + " GROUP BY t";

        private static final String GET_USER_PURCHASES =
                "SELECT DISTINCT p FROM Purchase p WHERE p.user.id = :" + USER_ID_PARAM + " ORDER BY p.id ASC";

        private static final String COUNT_USER_PURCHASES =
                "SELECT COUNT(DISTINCT p) FROM Purchase p WHERE p.user.id = :" + USER_ID_PARAM;

    @Override
    @Transactional
    public Page<Purchase> getUserPurchases(long userId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<Purchase> purchases = entityManager.createQuery(GET_USER_PURCHASES, Purchase.class)
                .setMaxResults(pageSize)
                .setParameter(USER_ID_PARAM, userId)
                .setFirstResult(ControllerHelper.calcOffset(pageSize, pageNumber))
                .getResultList();

        long resultsTotal = entityManager.createQuery(COUNT_USER_PURCHASES, Long.class)
                .setParameter("userId", userId)
                .getSingleResult();

        Page<Purchase> page = new PageImpl<>(purchases, pageable, resultsTotal);
        return page;
    }


//    @Override
//    @Transactional
//    public Page<Tag> getUserPrimaryTags(long userId) {
//
//    }

    @Override
    @Transactional
    public Page<Tag> getPrimaryTags(int pageNumber, int pageSize) {

        long mostRepeatedTimes = entityManager.createQuery(COUNT_MOST_WIDELY_USED_IN_PURCHASES_TAG_USAGE, Long.class)
                .setMaxResults(1)
                .getResultList().stream().findAny().orElse(0L);

        List<Tag> tags;
        long resultsTotal;
        int offset = ControllerHelper.calcOffset(pageNumber, pageSize);
        System.err.printf("Mostly repeated times %d%n", mostRepeatedTimes);
        System.err.printf("Params are: page %d size %d offset %d%n", pageNumber, pageSize, offset);

        if (mostRepeatedTimes != 0) {
            tags = entityManager.createQuery(GET_MOST_WIDELY_USED_IN_PURCHASES_TAGS, Tag.class)
                    .setParameter(QUANTITY_PARAM, mostRepeatedTimes)
                    .setFirstResult(offset)
                    .setMaxResults(pageSize)
                    .getResultList();

//            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
//            Root<Purchase> root = cq.from(Purchase.class);
//            cq.select(cb.count(cq.from(Tag.class)));
//            cq.groupBy(root.get());

            // todo: fix this abomination
            resultsTotal = entityManager.createQuery(COUNT_MOST_WIDELY_USED_IN_PURCHASES_TAGS, Integer.class)
                    .setParameter(QUANTITY_PARAM, mostRepeatedTimes)
                    .getResultList().size();

            System.err.printf("Results total: %d%n", resultsTotal);
        } else {
            tags = Collections.EMPTY_LIST;
            resultsTotal = 0L;
        }

//        System.err.printf("Page nad offset: %d %d%n", pageNumber, offset);
//        System.err.printf("Quantity of most used tags: %d%n", result);
//        System.err.printf("Tags found: %s%n", tags);

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Tag> page = new PageImpl<>(tags, pageable, resultsTotal);

        return page;
    }

    @Override
    public Page<Tag> getUserPrimaryTags(long userId, int pageNumber, int pageSize) {
        List<Tag> tags;
        long resultsTotal;
        int offset = ControllerHelper.calcOffset(pageSize, pageNumber);
        System.err.printf("Request data: page %d limit %d offset %d", pageNumber, pageSize, offset);

        long mostRepeatedTimes = entityManager.createQuery(COUNT_MOST_WIDELY_USED_IN_PURCHASES_TAG_USAGE_FOR_SPECIFIC_USER, Long.class)
                .setParameter(USER_ID_PARAM, userId)
                .setMaxResults(1)
                .getResultList().stream().findAny().orElse(0L);

        if (mostRepeatedTimes != 0) {
            tags = entityManager.createQuery(GET_MOST_WIDELY_USED_IN_PURCHASES_TAGS_FOR_SPECIFIC_USER, Tag.class)
                    //.setParameter(USER_ID_PARAM, userId)
                    .setParameter(QUANTITY_PARAM, mostRepeatedTimes)
                    .setMaxResults(pageSize)
                    .setFirstResult(offset)
                    .getResultList();

            // todo: fix this abomination
            resultsTotal = entityManager.createQuery(COUNT_MOST_WIDELY_USED_IN_PURCHASES_TAGS_FOR_SPECIFIC_USER, Integer.class)
                    .setParameter(USER_ID_PARAM, userId)
                    .setParameter(QUANTITY_PARAM, mostRepeatedTimes)
                    .getResultList().size();
        } else {
            resultsTotal = 0L;
            tags = Collections.EMPTY_LIST;
        }

        System.err.printf("total: %d, count: %d", resultsTotal, mostRepeatedTimes);
        // todo: fix pagination
        Pageable pageable = PageRequest.of(pageNumber-1, pageSize);
        Page<Tag> page = new PageImpl<>(tags, pageable, resultsTotal);
        return page;
    }

}
