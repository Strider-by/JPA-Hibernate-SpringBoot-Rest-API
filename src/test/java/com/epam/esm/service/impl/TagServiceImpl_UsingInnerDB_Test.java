package com.epam.esm.service.impl;

import com.epam.esm.controller.api.exception.TagNotFoundException;
import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.TagCreateDto;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.service.TagService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static com.epam.esm.service.impl.TagServiceImpl_UsingInnerDB_Test.Data.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class TagServiceImpl_UsingInnerDB_Test {

    @Autowired
    TagService service;
    @Autowired
    TagRepository repository;

    @BeforeEach
    void setUp() {
        Data.init();
        setInitialTags();
    }

    @AfterEach
    void cleanUp() {
        clearTagRepository();
    }

    @Test
    void getAllTags() {
        Page<Tag> tagsBefore = service.getAllTags(pageable);
        assertEquals(initialTagsQuantity, tagsBefore.getTotalElements());
        assertEquals(initialTagsQuantity, tagsBefore.getContent().size());
    }

    @Test
    void createTag_thatNotExistsYet() {
        Tag tag = service.createTag(createDtoForTagThatNotExistsYet);
        assertNotNull(tag.getId());
        Page<Tag> tagsAfter = service.getAllTags(pageable);
        assertEquals(initialTagsQuantity + 1, tagsAfter.getContent().size());
    }

    @Test
    void createTag_thatAlreadyExists_noNewTagShouldBeCreated() {
        Tag tag = service.createTag(createDtoForTagThatAlreadyExists);
        assertNotNull(tag.getId());
        Page<Tag> tagsAfter = service.getAllTags(pageable);
        assertEquals(initialTagsQuantity, tagsAfter.getContent().size());
    }

    @Test
    void deleteTag_tagExists() {
        service.deleteTag(initialTags[0].getName());
        Page<Tag> tags = service.getAllTags(pageable);
        assertEquals(initialTagsQuantity - 1, tags.getContent().size());
    }

    @Test
    void deleteTag_tagNotFound() {
        assertThrows(TagNotFoundException.class, () -> service.deleteTag(tagThatNotPersistsYet.getName()));
    }

    @Test
    void getTagByName_tagExists() {
        Tag tag = service.getTag(initialTags[0].getName());
        assertNotNull(tag.getId());
    }

    @Test
    void getTagByName_tagNotFound() {
        assertThrows(TagNotFoundException.class, () -> service.getTag(tagThatNotPersistsYet.getName()));
    }

    void clearTagRepository() {
        repository.deleteAll();
        long count = repository.count();
        assertEquals(count, 0);
    }

    void setInitialTags() {
        for (Tag tag : initialTags) {
            Tag savedTag = repository.save(tag);
            assertNotNull(savedTag.getId());
        }

        long countAfterInsertion = repository.count();
        assertEquals(countAfterInsertion, initialTagsQuantity);
    }

    static class Data {
        static Pageable pageable;

        static Tag tag1;
        static Tag tag2;
        static Tag tag3;
        static Tag[] initialTags;
        static int initialTagsQuantity;

        static Tag tagThatNotPersistsYet;
        static TagCreateDto createDtoForTagThatNotExistsYet;
        static TagCreateDto createDtoForTagThatAlreadyExists;
        
        static void init() {
            pageable = PageRequest.of(0, 10);

            tag1 = new Tag("tag1");
            tag2 = new Tag("tag2");
            tag3 = new Tag("tag3");
            initialTags = new Tag[] {tag1, tag2, tag3};
            initialTagsQuantity = initialTags.length;

            tagThatNotPersistsYet = new Tag("tag4");
            createDtoForTagThatNotExistsYet = new TagCreateDto(tagThatNotPersistsYet.getName());
            createDtoForTagThatAlreadyExists = new TagCreateDto(tag1.getName());
        }
    }


}