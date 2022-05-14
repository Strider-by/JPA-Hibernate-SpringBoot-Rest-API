package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.exception.CertificateNotFoundException;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.User;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.service.CertificateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
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

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.epam.esm.controller.api.impl.CertificateControllerImplTest.Data.*;

@SpringBootTest
class CertificateControllerImplTest {

    private MockMvc mockMvc;
    private CertificateService service;

//    @Test
//    void getAllCertificates() {
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<Certificate> certificates = service.getAllCertificates(pageable);
//        assertEquals(0, certificates.getContent().size());
//
//        //add3certificates();
//        Certificate certificate = service.createCertificate(new CertificateCreateDto());
//        certificates = service.getAllCertificates(pageable);
//        assertEquals(1, certificates.getContent().size());
//    }

    @BeforeEach
    void setUp() {
        service = Mockito.mock(CertificateService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new CertificateControllerImpl(service))
                .build();
    }

    @Test
    void getAllCertificates() throws Exception {
        when(service.getAllCertificates(any())).thenReturn(certificatePage);
        mockMvc.perform(get("/certificates/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.elements_on_current_page").value(elementsOnPage));
    }

    @Test
    void getCertificate_success() throws Exception {
        when(service.getCertificate(certificateId)).thenReturn(certificate);
        mockMvc.perform(get("/certificates/{id}", certificateId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void getCertificate_fail_notFound() throws Exception {
        mockMvc.perform(get("/certificates/{id}", certificateId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    void createCertificate() throws Exception {
        long id = 1L;
        String name = "new_name";
        int price = 400;

        CertificateCreateDto dto = new CertificateCreateDto();
        dto.setName(name);
        dto.setPrice(price);

        Certificate expected = new Certificate();
        expected.setId(id);
        expected.setName(name);
        expected.setPrice(price);

        when(service.createCertificate(dto)).thenReturn(expected);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", name);
        params.add("price", Integer.toString(price));
        params.add("id", Long.toString(id));


        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/certificates")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .characterEncoding("UTF-8")
                .params(params);


        this.mockMvc.perform(builder)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.price").value(price));
    }

    @Test
    void deleteCertificate_success() throws Exception {
        long id = 1L;
        mockMvc.perform(delete("/certificates/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()));
    }

    @Test
    void deleteCertificate_fail_notFound() throws Exception {
        long id = 404L;
        doThrow(new CertificateNotFoundException(id)).when(service).deleteCertificate(id);
        mockMvc.perform(delete("/certificates/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()));
    }

    static class Data {
        static final long certificateId = 1L;
        static final Certificate certificate = new Certificate();
        static final int pageNumber = 0;
        static final int pageSize = 10;
        static final int elementsOnPage = pageSize;
        static final Pageable pageable = PageRequest.of(pageNumber, pageSize);
        static final long elementsTotal = 25;
        static final List<Certificate> certificates = Collections.unmodifiableList(
                IntStream.range(0, pageSize).mapToObj(i -> new Certificate()).collect(Collectors.toList()));
        static final Page<Certificate> certificatePage = new PageImpl(certificates, pageable, elementsTotal);

        static {
            certificate.setId(certificateId);
        }

    }

}