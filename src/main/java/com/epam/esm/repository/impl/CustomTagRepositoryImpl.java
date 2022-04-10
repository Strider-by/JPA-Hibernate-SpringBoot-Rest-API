package com.epam.esm.repository.impl;

import com.epam.esm.model.Tag;
import com.epam.esm.repository.CustomTagRepository;
import com.epam.esm.repository.JpaTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class CustomTagRepositoryImpl implements CustomTagRepository {

    private static final String NAMES_PARAM = "names";
    private static final String GET_TAGS_WITH_SELECTED_NAMES =
            "SELECT t FROM Tag t WHERE t.name IN :" + NAMES_PARAM;

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private JpaTagRepository repository;

    @Override
    @Transactional
    public List<Tag> persistTags(List<Tag> tags) {

        List<String> tagsNames = tags.stream().map(Tag::getName).collect(Collectors.toList());
        //em.getTransaction().begin();
        List<Tag> foundTags = em.createQuery(GET_TAGS_WITH_SELECTED_NAMES, Tag.class)
                .setParameter(NAMES_PARAM, tagsNames)
                .getResultList();
        Set<String> foundTagsNames = foundTags.stream().map(Tag::getName).collect(Collectors.toSet());

        List<Tag> tagsToPersist = tags.stream()
                .filter(tag -> !foundTagsNames.contains(tag.getName()))
                .collect(Collectors.toList());
        persistTransientTags(tagsToPersist);
        //em.getTransaction().commit();

        List<Tag> persistedTags = Stream.of(foundTags, tagsToPersist)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

//        tagsToPersist.clear();
//        tagsToPersist.addAll(persistedTags);
        return persistedTags;
    }

    // todo: ask if transaction usage is correct here
    @Transactional
    private void persistTransientTags(List<Tag> tagsToPersist) {
        //em.getTransaction().begin();
        for (int i = 0; i < tagsToPersist.size(); i++) {
            em.persist(tagsToPersist.get(i));
        }
        //em.getTransaction().commit();
    }

}
