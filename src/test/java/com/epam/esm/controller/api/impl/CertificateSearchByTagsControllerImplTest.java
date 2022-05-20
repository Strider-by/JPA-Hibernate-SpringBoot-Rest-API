package com.epam.esm.controller.api.impl;

import com.epam.esm.model.Certificate;
import com.epam.esm.service.CertificateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CertificateSearchByTagsControllerImplTest {

    private MockMvc mockMvc;
    private CertificateService service;


    @BeforeEach
    void setUp() {
        service = Mockito.mock(CertificateService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new CertificateSearchByTagsControllerImpl(service))
                .build();
    }

    @Test
    void search() throws Exception {
        int pageSize = 10;
        int pageNumber = 0;
        long total = 30;
        List<Certificate> certificates = IntStream.range(0, 3).mapToObj(i -> new Certificate()).collect(Collectors.toList());
        List<String> tagsParamsList = IntStream.range(0, 7).mapToObj(Integer::toString).collect(Collectors.toList());
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("tag", tagsParamsList);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Certificate> expected = new PageImpl<>(certificates, pageable, total);
        when(service.searchCertificatesByTagNames(tagsParamsList, pageable)).thenReturn(expected);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/searchByTags")
                .characterEncoding("UTF-8")
                .params(params);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.elements_on_current_page").value(certificates.size()));

        verify(service).searchCertificatesByTagNames(tagsParamsList, pageable);
    }

}