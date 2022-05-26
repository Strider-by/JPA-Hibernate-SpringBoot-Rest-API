package com.epam.esm.repository.impl;

import com.epam.esm.controller.api.exception.PurchaseNotFoundException;
import com.epam.esm.model.Purchase;
import com.epam.esm.model.Tag;
import com.epam.esm.repository.CustomPurchaseRepository;
import com.epam.esm.repository.JpaPurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
            "SELECT DISTINCT d FROM Purchase p JOIN p.certificate c JOIN c.description d "
                    + "GROUP BY d.id "
                    + "HAVING COUNT(d.id) = :"
                    + QUANTITY_PARAM
                    + " ORDER BY d.id";

    private static final String GET_MOST_WIDELY_USED_IN_PURCHASES_TAGS_FOR_SPECIFIC_USER =
            "SELECT DISTINCT d FROM Purchase p JOIN p.certificate c JOIN c.description d "
                    + "WHERE p.user.id = :"
                    + USER_ID_PARAM
                    + " GROUP BY d.id "
                    + " HAVING COUNT(d.id) = :"
                    + QUANTITY_PARAM
                    + " ORDER BY d.id";

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

    private static final String GET_USER_PURCHASES =
            "SELECT DISTINCT p FROM Purchase p WHERE p.user.id = :" + USER_ID_PARAM + " ORDER BY p.id ASC";

    private static final String COUNT_USER_PURCHASES =
            "SELECT COUNT(DISTINCT p) FROM Purchase p WHERE p.user.id = :" + USER_ID_PARAM;


    @Override
    @Transactional
    public Page<Purchase> getUserPurchases(long userId, Pageable pageable) {
        List<Purchase> purchases = entityManager.createQuery(GET_USER_PURCHASES, Purchase.class)
                .setMaxResults(pageable.getPageSize())
                .setParameter(USER_ID_PARAM, userId)
                .setFirstResult((int) pageable.getOffset())
                .getResultList();

        long elementsTotal = entityManager.createQuery(COUNT_USER_PURCHASES, Long.class)
                .setParameter(USER_ID_PARAM, userId)
                .getSingleResult();

        Page<Purchase> page = new PageImpl<>(purchases, pageable, elementsTotal);
        return page;
    }

    @Override
    @Transactional
    public Page<Tag> getPrimaryTags(Pageable pageable) {

        long mostRepeatedTimes = entityManager.createQuery(COUNT_MOST_WIDELY_USED_IN_PURCHASES_TAG_USAGE, Long.class)
                .setMaxResults(1)
                .getResultList().stream().findAny().orElse(0L);

        List<Tag> tags;
        long elementsTotal;

        if (mostRepeatedTimes != 0) {
            tags = entityManager.createQuery(GET_MOST_WIDELY_USED_IN_PURCHASES_TAGS, Tag.class)
                    .setParameter(QUANTITY_PARAM, mostRepeatedTimes)
                    .setFirstResult((int) pageable.getOffset())
                    .setMaxResults(pageable.getPageSize())
                    .getResultList();

            // todo: fix this abomination
            elementsTotal = entityManager.createQuery(COUNT_MOST_WIDELY_USED_IN_PURCHASES_TAGS, Integer.class)
                    .setParameter(QUANTITY_PARAM, mostRepeatedTimes)
                    .getResultList().size();

        } else {
            tags = Collections.EMPTY_LIST;
            elementsTotal = 0L;
        }

        Page<Tag> page = new PageImpl<>(tags, pageable, elementsTotal);

        return page;
    }

    @Override
    @Transactional
    public Page<Tag> getUserPrimaryTags(long userId, Pageable pageable) {
        List<Tag> tags;
        long elementsTotal;

        long mostRepeatedTimes = entityManager.createQuery(COUNT_MOST_WIDELY_USED_IN_PURCHASES_TAG_USAGE_FOR_SPECIFIC_USER, Long.class)
                .setParameter(USER_ID_PARAM, userId)
                .setMaxResults(1)
                .getResultList().stream().findAny().orElse(0L);

        if (mostRepeatedTimes != 0) {
            tags = entityManager.createQuery(GET_MOST_WIDELY_USED_IN_PURCHASES_TAGS_FOR_SPECIFIC_USER, Tag.class)
                    .setParameter(USER_ID_PARAM, userId)
                    .setParameter(QUANTITY_PARAM, mostRepeatedTimes)
                    .setMaxResults(pageable.getPageSize())
                    .setFirstResult((int) pageable.getOffset())
                    .getResultList();

            // todo: fix this abomination
            elementsTotal = entityManager.createQuery(COUNT_MOST_WIDELY_USED_IN_PURCHASES_TAGS_FOR_SPECIFIC_USER, Integer.class)
                    .setParameter(USER_ID_PARAM, userId)
                    .setParameter(QUANTITY_PARAM, mostRepeatedTimes)
                    .getResultList().size();
        } else {
            elementsTotal = 0L;
            tags = Collections.EMPTY_LIST;
        }

        Page<Tag> page = new PageImpl<>(tags, pageable, elementsTotal);
        return page;
    }

    @Override
    @Transactional
    public void deletePurchaseById(long id) {
        Purchase purchase = purchaseRepository.findById(id).orElseThrow(() -> new PurchaseNotFoundException(id));
        purchaseRepository.delete(purchase);
    }

}
