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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Certificate> expected = new PageImpl<>(certificates, pageable, total);
        when(service.searchCertificatesByTagNames(any(), any())).thenReturn(expected);

        mockMvc.perform(get("/searchByTags"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.elements_on_current_page").value(certificates.size()));
    }

}