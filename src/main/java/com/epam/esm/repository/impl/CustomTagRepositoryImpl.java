package com.epam.esm.repository.impl;

import com.epam.esm.controller.api.exception.TagNotFoundException;
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
    private static final String NAME_PARAM = "name";

    private static final String GET_TAGS_WITH_SELECTED_NAMES =
            "SELECT t FROM Tag t WHERE t.name IN :" + NAMES_PARAM;
    private static final String GET_TAG_WITH_SELECTED_NAME =
            "SELECT t FROM Tag t WHERE t.name = :" + NAME_PARAM;

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private JpaTagRepository repository;


    @Override
    @Transactional
    public List<Tag> persistTags(List<Tag> tags) {

        List<String> tagsNames = tags.stream().map(Tag::getName).collect(Collectors.toList());

        List<Tag> foundTags = entityManager.createQuery(GET_TAGS_WITH_SELECTED_NAMES, Tag.class)
                .setParameter(NAMES_PARAM, tagsNames)
                .getResultList();
        Set<String> foundTagsNames = foundTags.stream().map(Tag::getName).collect(Collectors.toSet());

        List<Tag> tagsToPersist = tags.stream()
                .filter(tag -> !foundTagsNames.contains(tag.getName()))
                .collect(Collectors.toList());
        persistTransientTags(tagsToPersist);

        List<Tag> persistedTags = Stream.of(foundTags, tagsToPersist)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return persistedTags;
    }


    // todo: ask if transaction usage is correct here
    @Transactional
    private void persistTransientTags(List<Tag> tagsToPersist) {
        for (int i = 0; i < tagsToPersist.size(); i++) {
            entityManager.persist(tagsToPersist.get(i));
        }
    }


    @Override
    public Tag findByName(String name) {
        return entityManager.createQuery(GET_TAG_WITH_SELECTED_NAME, Tag.class)
                .setParameter(NAME_PARAM, name)
                .setMaxResults(1)
                .getResultList().stream()
                .findAny().orElseThrow(() -> new TagNotFoundException(name));
    }


    @Override
    @Transactional
    public void deleteByName(String tagName) {
        Tag tag = findByName(tagName);
        if (tag == null) {
            throw new TagNotFoundException(tagName);
        }
        repository.delete(tag);
    }

    @Override
    @Transactional
    public Tag createTag(Tag tag) {
        tag = findByName(tag.getName());
        if (tag == null) {
            tag = repository.save(tag);
        }
        return tag;
    }

}
