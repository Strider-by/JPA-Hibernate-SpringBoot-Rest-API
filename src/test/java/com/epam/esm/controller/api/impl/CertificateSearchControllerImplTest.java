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
import org.springframework.http.HttpStatus;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CertificateSearchControllerImplTest {

    private MockMvc mockMvc;
    private CertificateService service;


    @BeforeEach
    void setUp() {
        service = Mockito.mock(CertificateService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new CertificateSearchControllerImpl(service))
                .build();
    }

    @Test
    void searchCertificatesByPartOfNameOrDescription_success() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        int pageNumber = 1;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Certificate> certificates = IntStream.range(0, 10).mapToObj(i -> new Certificate()).collect(Collectors.toList());
        long total = 20;

        params.add("sort_by", "name");
        params.add("order", "desc");
        params.add("contains", "text");
        params.add("tag", "tag_name_example");
        params.add("page", Integer.toString(pageNumber));
        params.add("limit", Integer.toString(pageSize));

        Page<Certificate> searchResult = new PageImpl<>(certificates, pageable, total);

        // todo: workaround to use actual parameters instead of any()?
        when(service.searchCertificates(any(), any())).thenReturn(searchResult);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/search")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .characterEncoding("UTF-8")
                .params(params);

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.elements_on_current_page").value(certificates.size()));

        verify(service).searchCertificates(any(), any());
    }

    @Test
    void searchCertificatesByPartOfNameOrDescription_fail_badRequest_badParamName() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("bad param name", "irrelevant value");

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/search")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .characterEncoding("UTF-8")
                .params(params);

        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()));

        verifyNoInteractions(service);
    }

    @Test
    void searchCertificatesByPartOfNameOrDescription_fail_badRequest_badSortByParamValue() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("sort_by", "bad param value");

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/search")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .characterEncoding("UTF-8")
                .params(params);

        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()));

        verifyNoInteractions(service);
    }

    @Test
    void searchCertificatesByPartOfNameOrDescription_fail_badRequest_badOrderParamValue() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("order", "bad param value");

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/search")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .characterEncoding("UTF-8")
                .params(params);

        mockMvc.perform(builder)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()));

        verifyNoInteractions(service);
    }


}