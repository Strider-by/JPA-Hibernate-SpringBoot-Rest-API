package com.epam.esm.controller.api.impl;

import com.epam.esm.controller.api.exception.CertificateNotFoundException;
import com.epam.esm.controller.api.exception.PurchaseNotFoundException;
import com.epam.esm.controller.api.exception.UserNotFoundException;
import com.epam.esm.model.Certificate;
import com.epam.esm.model.Purchase;
import com.epam.esm.model.Tag;
import com.epam.esm.model.User;
import com.epam.esm.service.PurchaseService;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static com.epam.esm.controller.api.impl.PurchaseControllerImplTest.Data.*;

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
        when(service.getPurchaseById(purchaseId)).thenReturn(new Purchase());
        mockMvc.perform(get("/purchases/{id}", purchaseId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getPurchaseById_fail_notFound() throws Exception {
        when(service.getPurchaseById(purchaseId)).thenThrow(new PurchaseNotFoundException(purchaseId));
        mockMvc.perform(get("/purchases/{id}", purchaseId))
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
        when(service.getAllPurchases(any())).thenReturn(purchasesPage);
        mockMvc.perform(get("/purchases/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.elements_on_current_page").value(elementsOnPage));
    }

    @Test
    void getUserPurchases_success() throws Exception {
        // todo: ask. when(service.getUserPurchases(any(), any())) causes NPE
        when(service.getUserPurchases(userId, pageable)).thenReturn(purchasesPage);
        mockMvc.perform(get("/purchases/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.elements_on_current_page").value(elementsOnPage));
    }

    @Test
    void getUserPurchases_fail_userNotFound() throws Exception {
        when(service.getUserPurchases(userId, pageable)).thenThrow(new UserNotFoundException(userId));
        mockMvc.perform(get("/purchases/user/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getUserPrimaryTags_success() throws Exception {
        when(service.getUserPrimaryTags(userId, pageable)).thenReturn(tagPage);
        mockMvc.perform(get("/purchases/primary-tags/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.elements_on_current_page").value(elementsOnPage));
    }

    @Test
    void getUserPrimaryTags_fail_userNotFound() throws Exception {
        when(service.getUserPrimaryTags(userId, pageable)).thenThrow(new UserNotFoundException(userId));
        mockMvc.perform(get("/purchases/primary-tags/user/{userId}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getPrimaryTags() throws Exception {
        when(service.getPrimaryTags(pageable)).thenReturn(tagPage);
        mockMvc.perform(get("/purchases/primary-tags/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.elements_on_current_page").value(elementsOnPage));
    }

    @Test
    void purchaseCertificate_success() throws Exception {
        when(service.purchaseCertificate(userId, certificateId)).thenReturn(expected);

        MockHttpServletRequestBuilder purchaseRequest = MockMvcRequestBuilders.post("/purchases")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .characterEncoding("UTF-8")
                .params(purchaseParams);

        mockMvc.perform(purchaseRequest)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void purchaseCertificate_fail_userNotFound() throws Exception {
        when(service.purchaseCertificate(userId, certificateId)).thenThrow(new UserNotFoundException(userId));

        MockHttpServletRequestBuilder purchaseRequest = MockMvcRequestBuilders.post("/purchases")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .characterEncoding("UTF-8")
                .params(purchaseParams);

        mockMvc.perform(purchaseRequest)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void purchaseCertificate_fail_certificateNotFound() throws Exception {
        when(service.purchaseCertificate(userId, certificateId)).thenThrow(new CertificateNotFoundException(certificateId));

        MockHttpServletRequestBuilder purchaseRequest = MockMvcRequestBuilders.post("/purchases")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .characterEncoding("UTF-8")
                .params(purchaseParams);

        mockMvc.perform(purchaseRequest)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deletePurchase_success() throws Exception {
        mockMvc.perform(delete("/purchases/{id}", purchaseId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()));
    }

    @Test
    void deletePurchase_fail_certificateNotFound() throws Exception {
        doThrow(new CertificateNotFoundException(certificateId)).when(service).deletePurchase(purchaseId);
        mockMvc.perform(delete("/purchases/{id}", purchaseId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    static class Data {

        static final Long certificateId = 1L;
        static final Long userId = 2L;
        static final Long purchaseId = 3L;
        static final MultiValueMap<String, String> purchaseParams = new LinkedMultiValueMap<>();
        static final Purchase expected = new Purchase(new User(), new Certificate());

        static final int pageNumber = 0;
        static final int pageSize = 10;
        static final int elementsOnPage = pageSize;
        static final long elementsTotal = 25;
        static final Pageable pageable = PageRequest.of(pageNumber, pageSize);

        static final List<Purchase> purchases =
                IntStream.range(0, elementsOnPage).mapToObj(i -> new Purchase()).collect(Collectors.toList());
        static final Page<Purchase> purchasesPage = new PageImpl<>(purchases, pageable, elementsTotal);

        static final List<Tag> tags =
                IntStream.range(0, elementsOnPage).mapToObj(i -> new Tag()).collect(Collectors.toList());
        static final Page<Tag> tagPage = new PageImpl<>(tags, pageable, elementsTotal);

        static {
            purchaseParams.add("userId", Long.toString(userId));
            purchaseParams.add("certificateId", Long.toString(certificateId));
        }

    }

}