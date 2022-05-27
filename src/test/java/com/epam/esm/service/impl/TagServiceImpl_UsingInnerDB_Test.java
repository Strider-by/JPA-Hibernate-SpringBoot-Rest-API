package com.epam.esm.service.impl;

import com.epam.esm.controller.api.exception.TagNotFoundException;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.model.dto.TagCreateDto;
import com.epam.esm.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class TagServiceImpl_UsingInnerDB_Test {

    @Autowired
    TagService service;

    @Test
    void getAllTags() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tag> tagsBefore = service.getAllTags(pageable);
        // works properly, db finds 3 instances of Tag that were loaded manually via "import.sql" file usage
        assertEquals(3, tagsBefore.getContent().size());
    }

    @Test
    void createTag() {
        Pageable pageable = PageRequest.of(0, 10);
        Tag tag = service.createTag(new TagCreateDto("tag4"));
        assertNotNull(tag.getId());
        Page<Tag> tagsAfter = service.getAllTags(pageable);
        // should find 3 tags added via "import.sql" + newly added tag
        assertEquals(4, tagsAfter.getContent().size());
    }

    @Test
    void deleteTag_tagExists() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tag> tags = service.getAllTags(pageable);
        assertEquals(3, tags.getContent().size());

        service.deleteTag("tag1");
        tags = service.getAllTags(pageable);
        assertEquals(2, tags.getContent().size());
    }

    @Test
    void deleteTag_tagNotFound() {
        assertThrows(TagNotFoundException.class, () -> service.deleteTag("tag_404"));
    }

    @Test
    void getTagByName_tagExists() {
        Tag tag = service.getTag("tag1");
        assertNotNull(tag.getId());
    }

    @Test
    void getTagByName_tagNotFound() {
        assertThrows(TagNotFoundException.class, () -> service.getTag("tag_404"));
    }


}