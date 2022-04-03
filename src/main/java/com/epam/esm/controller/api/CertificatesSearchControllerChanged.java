package com.epam.esm.controller.api;

import com.epam.esm.model.Certificate;
import com.epam.esm.model.representation.HateoasView;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@RequestMapping("/new_search")
public interface CertificatesSearchControllerChanged {

    String DEFAULT_LIMIT_AS_STRING = "500";
    long DEFAULT_LIMIT = 500;

    @GetMapping(produces = "application/json")
    HttpEntity<HateoasView<List<Certificate>>> searchCertificatesByPartOfNameOrDescription(
            @RequestParam
            Map<String, String> params);





}
