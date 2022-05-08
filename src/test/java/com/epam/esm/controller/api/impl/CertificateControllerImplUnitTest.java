package com.epam.esm.controller.api.impl;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.service.CertificateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringRunner.class)
@SpringBootTest
class CertificateControllerImplUnitTest {

//    @Autowired
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
        service = context.getBean("certificatesServiceMock", CertificateService.class);
//        CertificatesController controller = context.getBean(CertificatesController.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new CertificateControllerImpl(service))
                .build();
    }

    @Test
    void getAllCertificates() throws Exception {
        mockMvc.perform(get("/certificates/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getCertificate() {
    }

    @Test
    void createCertificate() {
    }

    @Test
    void deleteCertificate() {
    }


    private void add3certificates() {
        IntStream.of(1, 2, 3).forEach(val -> service.createCertificate(new CertificateCreateDto()));
    }

}