package com.epam.esm.service.impl;

import com.epam.esm.controller.api.exception.TagNotFoundException;
import com.epam.esm.model.Tag;
import com.epam.esm.model.dto.TagCreateDto;
import com.epam.esm.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.epam.esm.service.impl.TagsServiceImplTest.Data.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class TagsServiceImplTest {

    @Mock
    private TagRepository repository;
    @InjectMocks
    private TagsServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTag() {
        when(repository.createTag(tag)).thenReturn(tag);
        Tag actual = service.createTag(createDto);
        assertTrue(actual == tag);
    }

    @Test
    void deleteTag_success() {
        service.deleteTag(tagName);
    }

    @Test
    void deleteTag_fail_tagNotFound() {
        doThrow(new TagNotFoundException(tagName)).when(repository).deleteByName(tagName);
        assertThrows(TagNotFoundException.class, () -> service.deleteTag(tagName));
    }

    @Test
    void getAllTags() {
        when(repository.findAll(pageable)).thenReturn(tagPage);
        Page<Tag> actual = service.getAllTags(pageable);
        assertTrue(tagPage == actual);
    }

    @Test
    void getTag_success() {
        when(repository.findByName(tagName)).thenReturn(tag);
        Tag actual = service.getTag(tagName);
        assertTrue(tag == actual);
    }

    @Test
    void getTag_fail_tagNotFound() {
        when(repository.findByName(tagName)).thenThrow(new TagNotFoundException(tagName));
        assertThrows(TagNotFoundException.class, () -> service.getTag(tagName));
    }

    static class Data {

        static final String tagName = "Random tag name";
        static final Tag tag = new Tag();
        static final TagCreateDto createDto = new TagCreateDto();

        static final int pageNumber = 0;
        static final int pageSize = 10;
        static final Pageable pageable = PageRequest.of(pageNumber, pageSize);
        static final long elementsTotal = 25;

        static final List<Tag> tags = Collections.unmodifiableList(
                IntStream.range(0, pageSize).mapToObj(i -> new Tag()).collect(Collectors.toList()));
        static final Page<Tag> tagPage = new PageImpl<>(tags, pageable, elementsTotal);

    }

}