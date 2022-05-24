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


//@RunWith(SpringRunner.class)
@SpringBootTest
//@EnableJpaRepositories
class TagServiceImpl_UsingInnerDBTest {

    @Autowired
    TagService service;

    @Test
    void getAllCertificates() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tag> tags = service.getAllTags(pageable);
        assertEquals(0, tags.getContent().size());

        Tag tag = service.createTag(new TagCreateDto("tag4"));
        tags = service.getAllTags(pageable);
        assertEquals(1, tags.getContent().size());
    }

    @Test
    void deleteCertificate() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tag> tags = service.getAllTags(pageable);
        assertEquals(0, tags.getContent().size());

        service.deleteTag("tag1");
        tags = service.getAllTags(pageable);
        assertEquals(3, tags.getContent().size());
    }

    @Test
    void getTagByName_tagExists() {
        Tag tag = service.getTag("tag1");
        assertNotNull(tag.getId());
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tag> tags = service.getAllTags(pageable);
        System.err.println(tags.getContent().size());
    }

    @Test
    void getTagByName_tagNotFound() {
        assertThrows(TagNotFoundException.class, () -> service.getTag("tag_404"));
    }


}