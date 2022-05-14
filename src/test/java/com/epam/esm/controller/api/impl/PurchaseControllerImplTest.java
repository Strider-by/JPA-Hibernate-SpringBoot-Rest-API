package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.exception.CertificateNotFoundException;
import com.epam.esm.controller.api.exception.PurchaseNotFoundException;
import com.epam.esm.controller.api.exception.UserNotFoundException;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Purchase;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import com.epam.esm.service.CertificateService;
import com.epam.esm.service.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PurchaseControllerImplTest {

    private MockMvc mockMvc;
    private PurchaseService service;


    @BeforeEach
    void setUp() {
        service = Mockito.mock(PurchaseService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new PurchaseControllerImpl(service))
                .build();
    }

    @Test
    void getPurchaseById_success() throws Exception {
        long id = 1L;
        when(service.getPurchaseById(id)).thenReturn(new Purchase());

        mockMvc.perform(get("/purchases/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getPurchaseById_fail_notFound() throws Exception {
        long id = 1L;
        when(service.getPurchaseById(id)).thenThrow(new PurchaseNotFoundException(id));

        mockMvc.perform(get("/purchases/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    // todo: do I need it? It doesn't test my code, it tests Spring MVC
    void getPurchaseById_fail_badIdParam() throws Exception {
        String badParam = "badToTheBone";
        mockMvc.perform(get("/purchases/{id}", badParam))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllPurchases() throws Exception {
        int pageNumber = 0;
        int pageSize = 10;
        List<Purchase> expectedContent =
                IntStream.range(0, pageSize).mapToObj(i -> new Purchase()).collect(Collectors.toList());
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        long elementsTotal = 25;

        when(service.getAllPurchases(any())).thenReturn(
                new PageImpl<>(expectedContent, pageable, elementsTotal));

        mockMvc.perform(get("/purchases/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // todo: remove? this doesn't seems like it should be part of this test
                .andExpect(jsonPath("$.elements_on_current_page").value(pageSize));
    }

    @Test
    void getUserPurchases_success() throws Exception {
        long userId = 1L;
        int pageNumber = 0;
        int pageSize = 10;
        List<Purchase> expectedContent =
                IntStream.range(0, pageSize).mapToObj(i -> new Purchase()).collect(Collectors.toList());
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        long elementsTotal = 25;

        // todo: ask. when(service.getUserPurchases(any(), any())) causes NPE
        when(service.getUserPurchases(userId, pageable)).thenReturn(
                new PageImpl<>(expectedContent, pageable, elementsTotal));

        mockMvc.perform(get("/purchases/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // todo: remove? this doesn't seems like it should be part of this test
                .andExpect(jsonPath("$.elements_on_current_page").value(pageSize));
    }

    @Test
    @Disabled // fixme
    void getUserPurchases_fail_userNotFound() throws Exception {
        long userId = 1L;
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        when(service.getUserPurchases(userId, pageable)).thenThrow(new UserNotFoundException(userId));

        mockMvc.perform(get("/purchases/user/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getUserPrimaryTags_success() throws Exception {
        long userId = 1L;
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Tag> expectedContent = IntStream.range(0, pageSize).mapToObj(i -> new Tag()).collect(Collectors.toList());
        Page<Tag> page = new PageImpl<>(expectedContent, pageable, pageSize);

        when(service.getUserPrimaryTags(userId, pageable)).thenReturn(page);

        mockMvc.perform(get("/purchases/primary-tags/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.elements_on_current_page").value(pageSize));
    }

    @Test
    @Disabled // fixme
    void getUserPrimaryTags_fail_userNotFound() throws Exception {
        long userId = 1L;
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        when(service.getUserPrimaryTags(userId, pageable)).thenThrow(new UserNotFoundException(userId));

        mockMvc.perform(get("/purchases/primary-tags/user/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getPrimaryTags() throws Exception {
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Tag> expectedContent = IntStream.range(0, pageSize).mapToObj(i -> new Tag()).collect(Collectors.toList());
        Page<Tag> page = new PageImpl<>(expectedContent, pageable, pageSize);

        when(service.getPrimaryTags(pageable)).thenReturn(page);

        mockMvc.perform(get("/purchases/primary-tags/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.elements_on_current_page").value(pageSize));
    }


    @Test
    void purchaseCertificate_success() throws Exception {
        long userId = 1;
        long certificateId = 2;
        Purchase expected = new Purchase(new User(), new Certificate());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userId", Long.toString(userId));
        params.add("certificateId", Long.toString(certificateId));

        when(service.purchaseCertificate(userId, certificateId)).thenReturn(expected);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/purchases")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .characterEncoding("UTF-8")
                .params(params);

        mockMvc.perform(builder)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Disabled // fixme
    void purchaseCertificate_fail_userNotFound() throws Exception {
        long userId = 1;
        long certificateId = 2;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userId", Long.toString(userId));
        params.add("certificateId", Long.toString(certificateId));

        when(service.purchaseCertificate(userId, certificateId)).thenThrow(new UserNotFoundException(userId));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/purchases")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .characterEncoding("UTF-8")
                .params(params);

        mockMvc.perform(builder)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Disabled // fixme
    void purchaseCertificate_fail_certificateNotFound() throws Exception {
        long userId = 1;
        long certificateId = 2;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userId", Long.toString(userId));
        params.add("certificateId", Long.toString(certificateId));

        when(service.purchaseCertificate(userId, certificateId)).thenThrow(new CertificateNotFoundException(certificateId));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/purchases")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .characterEncoding("UTF-8")
                .params(params);

        mockMvc.perform(builder)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deletePurchase_success() throws Exception {
        long id = 1;
        mockMvc.perform(delete("/purchases/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()));
    }

    @Test
    @Disabled // fixme
    void deletePurchase_fail_certificateNotFound() throws Exception {
        long id = 1;

        doThrow(new CertificateNotFoundException(id)).when(service).deletePurchase(id);

        mockMvc.perform(delete("/purchases/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}