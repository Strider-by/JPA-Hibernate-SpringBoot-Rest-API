package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.exception.TagNotFoundException;
import com.epam.esm.model.Tag;
import com.epam.esm.service.TagsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.epam.esm.controller.api.impl.TagControllerImplTest.Data.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TagControllerImplTest {

    private MockMvc mockMvc;
    private TagsService service;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(TagsService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new TagControllerImpl(service))
                .build();
    }

    @Test
    void getAllTags() throws Exception {
        when(service.getAllTags(any())).thenReturn(tagPage);
        mockMvc.perform(get("/tags"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.elements_on_current_page").value(pageSize));
    }

    @Test
    void getTag_success() throws Exception {
        when(service.getTag(tagName)).thenReturn(singleTag);
        mockMvc.perform(get("/tags/{tagName}", tagName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(tagId))
                .andExpect(jsonPath("$.name").value(tagName));
    }

    @Test
    void getTag_fail_tagNotFound() throws Exception {
        when(service.getTag(tagName)).thenThrow(new TagNotFoundException(tagName));
        mockMvc.perform(get("/tags/{tagName}", tagName))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createTag() throws Exception {
        when(service.createTag(any())).thenReturn(singleTag);
        mockMvc.perform(post("/tags"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(tagId))
                .andExpect(jsonPath("$.name").value(tagName));
    }

    @Test
    void deleteTag_success() throws Exception {
        mockMvc.perform(delete("/tags/{tagId}", tagName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteTag_fail_tagNotFound() throws Exception {
        doThrow(new TagNotFoundException(tagName)).when(service).deleteTag(tagName);
        mockMvc.perform(delete("/tags/{tagName}", tagName))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    final static class Data {
        static final long tagId = 1;
        static final String tagName = "generic name";
        static final Tag singleTag = new Tag(tagId, tagName);
        static final int pageNumber = 0;
        static final int pageSize = 10;
        static final Pageable pageable = PageRequest.of(pageNumber, pageSize);
        static final long elementsTotal = 25;
        static final List<Tag> tags = Collections.unmodifiableList(
                IntStream.range(0, pageSize).mapToObj(i -> new Tag()).collect(Collectors.toList()));
        static final Page<Tag> tagPage = new PageImpl(tags, pageable, elementsTotal);

    }

}