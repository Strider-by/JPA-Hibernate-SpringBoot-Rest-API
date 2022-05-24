package com.epam.esm.service.impl;

import com.epam.esm.controller.api.CertificateController;
import com.epam.esm.controller.api.exception.CertificateNotFoundException;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.dto.CertificateCreateDto;
import com.epam.esm.service.CertificateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



//@RunWith(SpringRunner.class)
@SpringBootTest
class CertificateServiceImpl_UsingInnerDB_Test {

    @Autowired
    CertificateService service;

    @Test
    void getAllCertificates() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Certificate> certificates = service.getAllCertificates(pageable);
        assertEquals(0, certificates.getContent().size());

        Certificate certificate = service.createCertificate(new CertificateCreateDto());
        certificates = service.getAllCertificates(pageable);
        assertEquals(1, certificates.getContent().size());
    }


}