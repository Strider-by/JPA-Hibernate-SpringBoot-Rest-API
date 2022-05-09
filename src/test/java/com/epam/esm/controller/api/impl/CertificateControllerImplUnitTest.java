package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.exception.CertificateNotFoundException;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.service.CertificateService;
import com.epam.esm.util.ContentStringBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class CertificateControllerImplUnitTest {

//    @Autowired // todo: work without autowiring?
    private MockMvc mockMvc;
    private CertificateService service;
    @Autowired
    private WebApplicationContext context;

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
        int pageNumber = 0;
        int pageSize = 10;
        List<Certificate> expectedContent = createNCertificates(pageSize);
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        long elementsTotal = 25;

        when(service.getAllCertificates(any())).thenReturn(
                new PageImpl<>(expectedContent, pageable, elementsTotal));
        
        mockMvc.perform(get("/certificates/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // todo: remove? this doesn't seems like it should be part of this test
                .andExpect(jsonPath("$.elements_on_current_page").value(pageSize));
    }

    @Test
    void getCertificate_success() throws Exception {
        long id = 1L;
        when(service.getCertificate(id)).thenReturn(new Certificate());

        mockMvc.perform(get("/certificates/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    void getCertificate_fail_notFound() throws Exception {
        long id = 404L;
        mockMvc.perform(get("/certificates/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // todo: should it be part of this test?
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

        String content = new ContentStringBuilder()
                .add("name", name)
                .add("price", price)
                .add("id", id)
                .build();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/certificates")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .characterEncoding("UTF-8")
                .content(content);


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


    
    private List<Certificate> createNCertificates(int n) {
        List<Certificate> certificates = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            Certificate certificate = new Certificate();
            certificates.add(certificate);
        }
        return certificates;
    }

}